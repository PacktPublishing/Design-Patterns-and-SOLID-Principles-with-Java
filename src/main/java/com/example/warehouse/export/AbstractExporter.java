package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.PrintStream;
import java.util.List;

public abstract class AbstractExporter {

    private final Report report;
    protected final PrintStream out;

    AbstractExporter(Report report, PrintStream out) {
        this.report = report;
        this.out = out;
    }

    public final void export() {
        beforeLabels();
        handleLabels(report.getLabels());
        afterLabels();

        beforeRecords();
        handleRecords();
        afterRecords();
    }

    protected void beforeLabels() {
    }

    protected abstract void handleLabels(List<String> labels);

    protected void afterLabels() {
    }

    protected void beforeRecords() {
    }

    private void handleRecords() {
        List<List<String>> records = report.getRecords();
        if (records.size() == 1) {
            handleRecord(records.get(0), true, true);
        } else if (records.size() >= 2) {
            handleRecord(records.get(0), true, false);
            for (List<String> record : records.subList(1, records.size() - 1)) {
                handleRecord(record, false, false);
            }
            handleRecord(records.get(records.size() - 1), false, true);
        }
    }

    protected abstract void handleRecord(List<String> records, boolean first, boolean last);

    protected void afterRecords() {
    }
}
