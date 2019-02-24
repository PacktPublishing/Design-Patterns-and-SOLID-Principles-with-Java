package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.PrintStream;
import java.util.List;

public class PdfExporter extends AbstractExporter {

    public PdfExporter(Report report, PrintStream out) {
        super(report, out);
    }

    @Override
    protected void handleLabels(PrintStream out, List<String> labels) {
        // Need to know what is the maximum width of each column here ...
    }

    @Override
    protected void handleRecord(PrintStream out, List<String> record, boolean first, boolean last) {
        // ... and here.
    }
}
