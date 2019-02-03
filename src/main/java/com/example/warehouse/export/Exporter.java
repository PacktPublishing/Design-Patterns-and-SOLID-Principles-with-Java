package com.example.warehouse.export;

public interface Exporter {

    enum ExportType {
        TXT,
        CSV
    }

    void export() throws ExporterException;
}
