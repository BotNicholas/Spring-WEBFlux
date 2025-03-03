package org.example.videoviewer.models;

import java.util.ArrayList;
import java.util.List;

public class FilesResponse {
    private List<File> files;

    public FilesResponse() {
    }

    public FilesResponse(List<File> files) {
        this.files = files;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public void addFile(File file) {
        if (this.files == null) {
            this.files = new ArrayList<File>();
        }

        this.files.add(file);
    }
}
