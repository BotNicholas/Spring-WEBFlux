package org.example.videoviewer.models;

public enum FileType {
    DIRECTORY,
    TEXT_FILE,
    PDF_FILE,
    IMAGE,
    VIDEO,
    OTHER_FILE;

    public static FileType getTypeFor(String file) {
        if ("directory".equals(file)) {
            return DIRECTORY;
        }
        if (file.matches("text/.+")) {
            return TEXT_FILE;
        }
        if (file.equals("application/pdf")) {
            return PDF_FILE;
        }
        if (file.matches("image/.+")) {
            return IMAGE;
        }
        if (file.matches("video/.+")) {
            return VIDEO;
        }
        return OTHER_FILE;
    }
}
