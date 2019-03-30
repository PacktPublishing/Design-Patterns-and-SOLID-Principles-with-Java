package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class TxtExporter extends AbstractExporter {

    private static final String BORDER = "*";
    private static final String LEFT_BORDER = BORDER + " ";
    private static final String RIGHT_BORDER = " " + BORDER;
    private static final String SEPARATOR = " | ";

    private final List<Integer> widths;

    private final int totalWidth;

    public TxtExporter(Report report, OutputStream out) {
        super(report, out);
        this.widths = calcWidths(report);

        totalWidth = widths.stream().mapToInt(Integer::intValue).sum()
            + LEFT_BORDER.length()
            + SEPARATOR.length() * (report.getLabels().size() - 1)
            + RIGHT_BORDER.length();
    }

    @Override
    protected void beforeLabels() {
        printBorder();
    }

    @Override
    protected void handleLabels(List<String> labels) {
        printStrings(labels);
    }

    @Override
    protected void afterLabels() {
        printBorder();
    }

    @Override
    protected void handleRecord(List<String> record, boolean first, boolean last) {
        printStrings(record);
    }

    @Override
    protected void afterRecords() {
        printBorder();
    }

    private void printBorder() {
        for (int i = 0; i < totalWidth; i++) {
            print(BORDER);
        }
        println();
    }

    private void printStrings(List<String> strings) {
        print(LEFT_BORDER);
        print(IntStream.range(0, strings.size())
            .mapToObj(i -> {
                String fmt = String.format("%%%ss", widths.get(i));
                return String.format(fmt, strings.get(i));
            })
            .collect(Collectors.joining(SEPARATOR)));
        print(RIGHT_BORDER);
        println();
    }
}
