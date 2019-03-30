package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

public final class CsvExporter extends AbstractExporter {

    private static final String SEPARATOR = ",";

    private final boolean includeHeader;

    public CsvExporter(Report report, OutputStream out, boolean includeHeader) {
        super(report, out);
        this.includeHeader = includeHeader;
    }

    @Override
    protected void handleLabels(List<String> labels) {
        if (includeHeader) {
            printStrings(labels);
        }
    }

    @Override
    protected void handleRecord(List<String> record, boolean first, boolean last) {
        printStrings(record);
    }

    private void printStrings(List<String> records) {
        println(records.stream().collect(Collectors.joining(SEPARATOR)));
    }
}
