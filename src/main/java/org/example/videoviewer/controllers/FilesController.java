package org.example.videoviewer.controllers;

import org.example.videoviewer.models.CreateDirectoryRequest;
import org.example.videoviewer.models.CreateFileRequest;
import org.example.videoviewer.models.File;
import org.example.videoviewer.models.FileType;
import org.example.videoviewer.models.FilesRequest;
import org.example.videoviewer.models.FilesResponse;
import org.example.videoviewer.models.PageFilesResponse;
import org.example.videoviewer.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

@CrossOrigin(origins = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/files")
public class FilesController {
    @Autowired
    FilesService filesService;

    @PostMapping
    public ResponseEntity<FilesResponse> getFiles(@RequestBody FilesRequest request, @RequestParam long page, @RequestParam long size) {
        request.setPath(normalizePath(request.getPath()));
        page = page<1 ? 1 : page;
        size = size<1 ? 1 : size;
        var pageResponse = filesService.getFiles(request.getPath(), page, size);
        return ResponseEntity.ok().headers(getPageHeaders(pageResponse)).body(new FilesResponse(pageResponse.getFiles()));
    }

    @PostMapping("/download")
    public ResponseEntity<FileSystemResource> downloadFile(@RequestBody FilesRequest request) throws IOException {
        return filesService.getFile(request.getPath());
    }

    private HttpHeaders getPageHeaders(PageFilesResponse response) {
        HttpHeaders headers = new HttpHeaders();

        headers.put("P-Total-Elements", List.of(String.valueOf(response.getTotalElements())));
        headers.put("P-Total-Pages", List.of(String.valueOf(response.getTotalPages())));
        headers.put("P-Requested-Page", List.of(String.valueOf(response.getRequestedPage())));
        headers.put("P-Requested-Size", List.of(String.valueOf(response.getRequestedSize())));
        headers.put("P-Elements-On-Page", List.of(String.valueOf(response.getElementsOnPage())));
        headers.put("P-Is-Page-Full", List.of(String.valueOf(response.isPageFull())));

        return headers;
    }

    @GetMapping("/image")
    public ResponseEntity<Resource> loadImage(@RequestParam("path") String filePath, @RequestParam("type")FileType type) throws IOException, InterruptedException {
        try {
            return filesService.getImageResponse(new String(Base64.getDecoder().decode(filePath)), type);
        } catch (Exception e) {
            if (e.getMessage().contains("Illegal base64 character")) {
                throw new RuntimeException("Illegal base64 encoding");
            }
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/image/preview")
    public ResponseEntity<Resource> loadImagePreview(@RequestParam("path") String filePath, @RequestParam("type")FileType type) throws IOException, InterruptedException {
        try {
            return filesService.getScaledImageResponse(new String(Base64.getDecoder().decode(filePath)), type);
        } catch (Exception e) {
            if (e.getMessage().contains("Illegal base64 character")) {
                throw new RuntimeException("Illegal base64 encoding");
            }
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/create/directory")
    public ResponseEntity<File> createDirectory(@RequestBody CreateDirectoryRequest request) throws IOException {
        return filesService.createDirectoryAt(request.getPath(), request.getName());
    }

    @PostMapping("/import")
    public ResponseEntity<List<File>> importFile(@RequestPart(value = "metadata") CreateFileRequest request, @RequestPart(value = "files") List<MultipartFile> files) throws IOException {
        return filesService.importFiles(request, files);
    }

    private String normalizePath(String path) {
        return path.replaceAll("\\.+/", "/");
    }
}
