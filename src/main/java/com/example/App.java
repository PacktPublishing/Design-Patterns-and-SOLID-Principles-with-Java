package com.example;

import com.example.warehouse.Report;
import com.example.warehouse.export.*;

import java.io.OutputStream;
import java.io.PrintStream;

public class App {

    protected Exporter newExporter(Report report, ExportType type, OutputStream out) {
        if (type == ExportType.CSV) {
            return new CsvExporter(report, new PrintStream(out), true);
        } else if (type == ExportType.TXT) {
            return new TxtExporter(report, new PrintStream(out));
        } else if (type == ExportType.HTML) {
            return new HtmlExporter(report, new PrintStream(out));
        } else if (type == ExportType.JSON) {
            return new JsonExporter(report, new PrintStream(out));
        } else {
            throw new IllegalStateException(String.format("Chosen exporter %s not handled, this cannot happen.", type));
        }
    }
}
