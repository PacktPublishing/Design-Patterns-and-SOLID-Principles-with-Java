package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.OutputStream;

public interface ExporterFactory {

    Exporter newExporter(Report report, ExportType type, OutputStream out);
}
