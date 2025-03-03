package org.example.videoviewer.services;

import org.example.videoviewer.models.FileType;
import org.example.videoviewer.models.FilesRequest;
import org.example.videoviewer.models.FilesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@Service
public class FilesService {
    @Value("${home.dir}")
    private String homeDir;
    @Value("${resources.dir}")
    private String resourcesDir;
    @Autowired
    private FFMpegImageExtractor imageExtractor;

    public FilesResponse getFiles(String path) {
        FilesResponse response = new FilesResponse();

        File dir = new File(getPath(path));

        if (dir.exists() && dir.isDirectory()) {
            Arrays.stream(dir.listFiles()).map(f -> {
                try {
                    String mimeType = Files.probeContentType(f.toPath());

                    return new org.example.videoviewer.models.File(
                            f.getName(),
                            f.getPath().replaceAll("\\\\", "/").replace(homeDir, ""),
                            FileType.getTypeFor(mimeType),
                            mimeType,
                            f.getTotalSpace());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).forEach(f -> response.addFile(f));
        }

        return response;
    }

    private String getPath(String path) {
        if (!homeDir.substring(homeDir.length() - 1).equals("/")) {
            return homeDir + "/" + path.replaceFirst("/", "");
        }

        return homeDir + path.replaceFirst("/", "");
    }

    public Resource getImage(org.example.videoviewer.models.File file) {
        Path path = getCorrectFileImagePath(file);
        return new FileSystemResource(path);
    }

    public ResponseEntity<Resource> getImageResponse(org.example.videoviewer.models.File file) throws IOException, InterruptedException {
        ResponseEntity<Resource> response;

        if (!file.getType().equals(FileType.VIDEO)) {
            Path path = getCorrectFileImagePath(file);
            assert path != null;
            response = ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(path))).body(getImage(file));
        } else {
            response =  ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(new ByteArrayResource(getFirstVideoFrameBytes(file)));
        }

        return response;
    }

    private Path getCorrectFileImagePath(org.example.videoviewer.models.File file) {
        switch (file.getType()) {
            case DIRECTORY:
                return Paths.get(getImagesDir() + "/default/folder.png");
            case TEXT_FILE:
                return Paths.get(getImagesDir() + "/default/txt.png");
            case PDF_FILE:
                return Paths.get(getImagesDir() + "/default/pdf.png");
            case IMAGE:
                return Paths.get(getPath(file.getPath()));
            default:
                return Paths.get(getImagesDir() + "/default/file.png");
        }
    }

    private byte[] getFirstVideoFrameBytes(org.example.videoviewer.models.File file) throws IOException, InterruptedException {
        return imageExtractor.getFirstFrameFromVideo(getPath(file.getPath()), getImagesDir());
    }

    private String getImagesDir() {
        return resourcesDir + "/images";
    }
}
