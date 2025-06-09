package org.example.videoviewer.models;

import java.util.List;

public class PageFilesResponse {
    private List<File> files;
    private long totalElements;
    private long totalPages;
    private long requestedPage;
    private long requestedSize;
    private long elementsOnPage;
    private boolean isPageFull;

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public long getRequestedPage() {
        return requestedPage;
    }

    public void setRequestedPage(long requestedPage) {
        this.requestedPage = requestedPage;
    }

    public long getRequestedSize() {
        return requestedSize;
    }

    public void setRequestedSize(long requestedSize) {
        this.requestedSize = requestedSize;
    }

    public long getElementsOnPage() {
        return elementsOnPage;
    }

    public void setElementsOnPage(long elementsOnPage) {
        this.elementsOnPage = elementsOnPage;
    }

    public boolean isPageFull() {
        return isPageFull;
    }

    public void setPageFull(boolean pageFull) {
        isPageFull = pageFull;
    }
}
