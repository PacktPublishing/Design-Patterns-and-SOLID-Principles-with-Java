package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

public final class CsvExporter extends AbstractExporter {

    private static final String SEPARATOR = ",";

    private final boolean includeHeader;

    public CsvExporter(Report report, PrintStream out, boolean includeHeader) {
        super(report, out);
        this.includeHeader = includeHeader;
    }

    @Override
    protected void handleLabels(PrintStream out, List<String> labels) {
        if (includeHeader) {
            printStrings(out, labels);
        }
    }

    @Override
    protected void handleRecord(PrintStream out, List<String> record, boolean first, boolean last) {
        printStrings(out, record);
    }

    private void printStrings(PrintStream out, List<String> records) {
        out.println(records.stream().collect(Collectors.joining(SEPARATOR)));
    }
}
