package org.example.videoviewer.models;

import org.springframework.web.multipart.MultipartFile;

public class CreateFileRequest {
    private String path;

    public CreateFileRequest() {
    }

    public CreateFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
