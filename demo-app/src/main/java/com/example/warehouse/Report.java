package com.example.warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public final class Report {

    public enum Type {
        DAILY_REVENUE("Daily revenue report");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum DataType {

        DATE,
        NUMBER,
        STRING
    }

    public static final class Field {

        private DataType dataType;
        private Object value;

        public Field(DataType dataType, Object value) {
            this.dataType = dataType;
            this.value = value;
        }

        public DataType getDataType() {
            return dataType;
        }

        public LocalDate getAsDate() {
            if (dataType == DataType.DATE) {
                return (LocalDate) value;
            }
            throw new IllegalStateException("This shouldn't happen.");
        }

        public Number getAsNumber() {
            if (dataType == DataType.NUMBER) {
                return (Number) value;
            }
            throw new IllegalStateException("This shouldn't happen.");
        }

        public String getAsString() {
            return value.toString();
        }
    }

    private List<String> labels;
    private List<List<Field>> records;

    Report() {
        this.labels = new ArrayList<>();
        this.records = new ArrayList<>();
    }

    public List<String> getLabels() {
        return labels;
    }

    public List<List<Field>> getRecords() {
        return records;
    }

    void addLabel(String label) {
        labels.add(label);
    }

    void addRecord(Field... fields) {
        records.add(Arrays.stream(fields).collect(toList()));
    }
}
