package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Exporter extends AbstractExporter {

    private static final String BORDER = "*";
    private static final String LEFT_BORDER = BORDER + " ";
    private static final String RIGHT_BORDER = " " + BORDER;
    private static final String SEPARATOR = " | ";

    private final List<Integer> widths;

    private final int totalWidth;

    public Exporter(Report report, PrintStream out) {
        super(report, out);
        this.widths = calcWidths(report);

        totalWidth = widths.stream().mapToInt(Integer::intValue).sum()
            + LEFT_BORDER.length()
            + SEPARATOR.length() * (report.getLabels().size() - 1)
            + RIGHT_BORDER.length();
    }

    private List<Integer> calcWidths(Report report) {
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

    @Override
    protected void beforeLabels(PrintStream out) {
        printBorder(out);
    }

    @Override
    protected void handleLabels(PrintStream out, List<String> labels) {
        printStrings(out, labels);
    }

    @Override
    protected void afterLabels(PrintStream out) {
        printBorder(out);
    }

    @Override
    protected void handleRecord(PrintStream out, List<String> records, boolean first, boolean last) {
        printStrings(out, records);
    }

    @Override
    protected void afterRecords(PrintStream out) {
        printBorder(out);
    }

    private void printBorder(PrintStream out) {
        for (int i = 0; i < totalWidth; i++) {
            out.print(BORDER);
        }
        out.println();
    }

    private void printStrings(PrintStream out, List<String> strings) {
        out.print(LEFT_BORDER);
        out.print(IntStream.range(0, strings.size())
            .mapToObj(i -> {
                String fmt = String.format("%%%ss", widths.get(i));
                return String.format(fmt, strings.get(i));
            })
            .collect(Collectors.joining(SEPARATOR)));
        out.print(RIGHT_BORDER);
        out.println();
    }
}
