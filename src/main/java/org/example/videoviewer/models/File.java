package org.example.videoviewer.models;

public class File {
    private String name;
    private String path;
    private FileType type;
    private String mimeType;
    private Long size;

    public File() {
    }

    public File(String name, String path, FileType type, String mimeType, Long size) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.mimeType = mimeType;
        this.size = size;
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

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
