package org.example.videoviewer.controllers;

import org.example.videoviewer.models.File;
import org.example.videoviewer.models.FilesRequest;
import org.example.videoviewer.models.FilesResponse;
import org.example.videoviewer.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class FilesController {
    @Autowired
    FilesService filesService;

    @GetMapping
    public FilesResponse getFiles(@RequestBody FilesRequest request) {
        request.setPath(normalizePath(request.getPath()));
        return filesService.getFiles(request.getPath());
    }

    @GetMapping("/image")
    public ResponseEntity<Resource> loadImage(@RequestBody File file) throws IOException, InterruptedException {
        return filesService.getImageResponse(file);
    }

    private String normalizePath(String path) {
        return path.replaceAll("\\.+/", "/");
    }
}
