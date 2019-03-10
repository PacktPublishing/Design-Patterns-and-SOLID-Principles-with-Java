package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExporter extends PrintStream implements Exporter {

    private final Report report;

    AbstractExporter(Report report, OutputStream out) {
        super(out);
        this.report = report;
    }

    @Override
    public final void export() {
        beforeLabels();
        handleLabels(report.getLabels());
        afterLabels();

        beforeRecords();
        handleRecords();
        afterRecords();
    }

    protected List<Integer> calcWidths(Report report) {
        List<Integer> widths = new ArrayList<>();
        report.getLabels().forEach(l -> widths.add(l.length()));
        for (List<String> record : report.getRecords()) {
            for (int i = 0; i < widths.size(); i++) {
                int maxWidth = widths.get(i);
                int width = record.get(i).length();
                if (width > maxWidth) {
                    widths.set(i, width);
                }
            }
        }
        return widths;
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

    protected abstract void handleRecord(List<String> record, boolean first, boolean last);

    protected void afterRecords() {
    }
}
