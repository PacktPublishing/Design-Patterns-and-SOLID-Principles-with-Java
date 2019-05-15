package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.OutputStream;
import java.io.PrintStream;

public final class TrialExporterFactory implements ExporterFactory {

    @Override
    public Exporter newExporter(Report report, ExportType type, OutputStream out) {
        if (type == ExportType.TXT) {
            return new TxtExporter(report, new PrintStream(out));
        } else {
            throw new UnsupportedOperationException(String.format("Chosen exporter %s not available.", type));
        }
    }
}
