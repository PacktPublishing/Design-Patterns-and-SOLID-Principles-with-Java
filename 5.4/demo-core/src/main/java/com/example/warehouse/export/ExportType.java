package com.example.warehouse.export;

public enum ExportType {

    TXT("text/plain", "txt"),
    CSV("text/csv", "csv"),
    HTML("text/html", "html"),
    JSON("application/json", "json");

    private final String mimeType;
    private final String fileExtension;

    ExportType(String mimeType, String fileExtension) {
        this.mimeType = mimeType;
        this.fileExtension = fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
