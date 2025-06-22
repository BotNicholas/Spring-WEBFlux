package org.example.videoviewer.models;

import org.springframework.web.multipart.MultipartFile;

public class CreateDirectoryRequest {
    private String name;
    private String path;

    public CreateDirectoryRequest() {
    }

    public CreateDirectoryRequest(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
