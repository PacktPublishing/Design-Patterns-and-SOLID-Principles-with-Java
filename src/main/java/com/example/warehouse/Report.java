package com.example.warehouse;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class Report {

    public enum Type {
        DAILY_REVENUE
    }

    private List<String> labels;
    private List<List<String>> records;

    Report() {
        this.labels = new ArrayList<>();
        this.records = new ArrayList<>();
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<List<String>> getRecords() {
        return records;
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    public void addRecord(List<Object> record) {
        records.add(record.stream()
            .map(String::valueOf)
            .collect(toList()));
    }
}
