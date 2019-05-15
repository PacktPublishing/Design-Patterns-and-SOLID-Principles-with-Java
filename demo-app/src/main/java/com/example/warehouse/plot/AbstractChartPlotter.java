package com.example.warehouse.plot;

import com.example.warehouse.Report;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

abstract class AbstractChartPlotter implements ChartPlotter {

    static final int PNG_WIDTH = 1280;
    static final int PNG_HEIGHT = 720;

    final Report.Type reportType;
    final ChartType chartType;

    int dateIndex = -1;

    AbstractChartPlotter(Report.Type reportType, ChartType chartType) {
        this.reportType = reportType;
        this.chartType = chartType;
    }

    @Override
    public final void plot(Report report, OutputStream out) throws IOException {
        if (report.getRecords().isEmpty()) {
            throw new IllegalArgumentException("Cannot plot empty report.");
        }
        checkReport(report);
        doPlot(report, out);
    }

    abstract void doPlot(Report report, OutputStream out) throws IOException;

    private void checkReport(Report report) {
        List<List<Report.Field>> records = report.getRecords();
        if (!records.isEmpty()) {
            List<Report.Field> firstRecord = records.get(0);
            long dateCount = firstRecord.stream()
                .filter(f -> f.getDataType() == Report.DataType.DATE)
                .count();
            if (dateCount == 0) {
                throw new IllegalArgumentException("Can't handle report without a date component yet.");
            } else if (dateCount == 1) {
                Report.Field dateField = firstRecord.stream()
                    .filter(f -> f.getDataType() == Report.DataType.DATE)
                    .findFirst()
                    .orElseThrow();
                dateIndex = firstRecord.indexOf(dateField);
            } else {
                throw new IllegalStateException("Cannot handle plotting report with multiple date columns.");
            }
        }
    }
}
