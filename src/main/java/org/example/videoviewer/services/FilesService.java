package org.example.videoviewer.services;

import org.example.videoviewer.exceptions.FileExistsException;
import org.example.videoviewer.exceptions.WrongMetadataException;
import org.example.videoviewer.models.CreateFileRequest;
import org.example.videoviewer.models.FileType;
import org.example.videoviewer.models.PageFilesResponse;
import org.example.videoviewer.utils.NaturalOrderComparator;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.imageio.ImageIO;

@Service
public class FilesService {
    @Value("${home.dir}")
    private String homeDir;
    @Value("${resources.dir}")
    private String resourcesDir;
    @Autowired
    private FFMpegImageExtractor imageExtractor;

    public PageFilesResponse getFiles(String path, long page, long size) {
        PageFilesResponse response = new PageFilesResponse();

        File dir = new File(getPath(path));

        if (dir.exists() && dir.isDirectory()) {
            var filesList = Arrays.stream(dir.listFiles()).map(f -> {
                try {
                    String mimeType = f.isDirectory()? "directory" : Files.probeContentType(f.toPath());
                    mimeType = mimeType == null ? "other" : mimeType;

                    return new org.example.videoviewer.models.File(
                            f.getName(),
                            f.getPath().replaceAll("\\\\", "/").replace(homeDir, ""),
                            FileType.getTypeFor(mimeType),
                            mimeType,
                            f.getTotalSpace());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//            }).sorted((f1, f2) -> f1.getName().compareTo(f2.getName())).toList();
            }).sorted(new NaturalOrderComparator()).toList();

            var totalElementsCount = filesList.size();
            var totalPagesCount = (int) Math.ceil((double) totalElementsCount / size);
            var elementsOnPage = filesList.stream().skip((page-1) * size).limit(size).count();
            var isPageFull = elementsOnPage == size;
            var files = filesList.stream().skip((page-1) * size).limit(size).toList();

            response.setTotalElements(totalElementsCount);
            response.setTotalPages(totalPagesCount);
            response.setRequestedPage(page);
            response.setRequestedSize(size);
            response.setElementsOnPage(elementsOnPage);
            response.setPageFull(isPageFull);
            response.setFiles(files);
        }

        return response;
    }

    public ResponseEntity<FileSystemResource> getFile(String path) throws IOException {
        Path filePath = Paths.get(getPath(path));

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            throw new IllegalArgumentException("File '" + filePath.getFileName() + "' can not be downloaded!");
        }

        String mimeType = Files.probeContentType(filePath);
        mimeType = mimeType == null? "application/octet-stream" : mimeType;

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).body(new FileSystemResource(filePath));
    }

    public ResponseEntity<org.example.videoviewer.models.File> createDirectoryAt(final String path, final String dirName) throws IOException {
        var normalizedPath = Paths.get(getPath(path), dirName);
        if (Files.exists(normalizedPath) && Files.isDirectory(normalizedPath)) {
            throw new FileExistsException(dirName);
        }

        var dirPath = Files.createDirectory(normalizedPath);
        var dir = new File(dirPath.toUri());

        var dirResponse = new org.example.videoviewer.models.File(dir.getName(),
                dir.getPath().replaceAll("\\\\", "/").replace(homeDir, ""),
                FileType.DIRECTORY,
                Files.probeContentType(dirPath),
                dir.getTotalSpace());


        return ResponseEntity.status(HttpStatus.CREATED).body(dirResponse);
    }

    public ResponseEntity<org.example.videoviewer.models.File> importFile(final CreateFileRequest metadata, final MultipartFile file) throws IOException {
        var normalizedPath = Paths.get(getPath(metadata.getPath()), file.getOriginalFilename());

        if (!file.getOriginalFilename().matches(String.format("%s\\..+", metadata.getName()))) {
            throw new WrongMetadataException(String.format("'%s' File name from metadata does not match actual file name '%s'.", metadata.getName(), getFileName(file.getOriginalFilename())));
        }

        if (Files.exists(normalizedPath)) {
            throw new FileExistsException(metadata.getName());
        }

        var newFilePath = Files.write(normalizedPath, file.getBytes());
        var newFile = new File(newFilePath.toUri());

        var mimetype = Files.probeContentType(newFilePath);

        var fileResponse = new org.example.videoviewer.models.File(newFile.getName(),
                newFile.getPath().replaceAll("\\\\", "/").replace(homeDir, ""),
                FileType.getTypeFor(mimetype),
                mimetype,
                newFile.getTotalSpace());

        return ResponseEntity.status(HttpStatus.CREATED).body(fileResponse);
    }

    private String getFileName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private String getPath(String path) {
        if (!homeDir.substring(homeDir.length() - 1).equals("/")) {
            return homeDir + "/" + path.replaceFirst("/", "");
        }

        return homeDir + path.replaceFirst("/", "");
    }

    public Resource getImage(String filePath, FileType type) {
        Path path = getCorrectFileImagePath(filePath, type);
        return new FileSystemResource(path);
    }

    public Resource getScaledImage(String filePath, FileType type) throws IOException {
        Path path = getCorrectFileImagePath(filePath, type);
        var image = Scalr.resize(ImageIO.read(path.toFile()), Scalr.Method.SPEED, Scalr.Mode.AUTOMATIC, 320, 180, Scalr.OP_ANTIALIAS);
        var baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return new ByteArrayResource(baos.toByteArray());
    }

    public ResponseEntity<Resource> getImageResponse(String filePath, FileType type) throws IOException, InterruptedException {
        ResponseEntity<Resource> response;

        if (!FileType.VIDEO.equals(type)) {
            Path path = getCorrectFileImagePath(filePath, type);
            response = ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(path))).body(getImage(filePath, type));
        } else {
            response =  ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new ByteArrayResource(getFirstVideoFrameBytes(filePath)));
        }

        return response;
    }

    public ResponseEntity<Resource> getScaledImageResponse(String filePath, FileType type) throws IOException, InterruptedException {
        ResponseEntity<Resource> response;

        if (!FileType.VIDEO.equals(type)) {
            Path path = getCorrectFileImagePath(filePath, type);
            response = ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(path))).body(getScaledImage(filePath, type));
        } else {
            response =  ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new ByteArrayResource(getScaledFirstVideoFrameBytes(filePath)));
        }

        return response;
    }

    private Path getCorrectFileImagePath(String filePath, FileType type) {
        switch (type) {
            case DIRECTORY:
                return Paths.get(getImagesDir() + "/default/folder.png");
            case TEXT_FILE:
                return Paths.get(getImagesDir() + "/default/txt.png");
            case PDF_FILE:
                return Paths.get(getImagesDir() + "/default/pdf.png");
            case IMAGE:
                return Paths.get(getPath(filePath));
            default:
                return Paths.get(getImagesDir() + "/default/file.png");
        }
    }

    private byte[] getFirstVideoFrameBytes(String filePath) throws IOException, InterruptedException {
        return imageExtractor.getFirstFrameFromVideo(getPath(filePath), getImagesDir());
    }

    private byte[] getScaledFirstVideoFrameBytes(String filePath) throws IOException, InterruptedException {
        return imageExtractor.getScaledFirstFrameFromVideo(getPath(filePath), getImagesDir());
    }

    private String getImagesDir() {
        return resourcesDir + "/images";
    }
}
