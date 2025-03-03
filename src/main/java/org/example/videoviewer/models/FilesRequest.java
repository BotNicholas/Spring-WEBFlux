package org.example.videoviewer.models;


public class FilesRequest {
    private String path;

    public FilesRequest() {
    }

    public FilesRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
