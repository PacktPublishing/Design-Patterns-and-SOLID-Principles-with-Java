package com.example.warehouse.plot;

import com.example.warehouse.Report;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.jfree.chart.ChartFactory.createTimeSeriesChart;
import static org.jfree.chart.ChartFactory.createXYBarChart;
import static org.jfree.chart.ChartUtils.writeChartAsPNG;

public final class ComplexChartPlotter extends AbstractChartPlotter implements ChartPlotter {

    public ComplexChartPlotter(Report.Type reportType, ChartType chartType) {
        super(reportType, chartType);
    }

    @Override
    void doPlot(Report report, OutputStream out) throws IOException {
        List<String> labels = report.getLabels();
        List<List<Report.Field>> records = report.getRecords();

        String dateLabel = labels.get(dateIndex);
        List<String> valueLabels = labels
            .stream()
            .filter(l -> !l.equals(dateLabel))
            .collect(Collectors.toList());

        TimeSeriesCollection seriesCollection = new TimeSeriesCollection();
        for (String valueLabel : valueLabels) {
            seriesCollection.addSeries(new TimeSeries(valueLabel));
        }

        for (List<Report.Field> record : records) {
            Report.Field dateField = record.get(dateIndex);
            Day day = Day.parseDay(dateField.getAsString());
            List<Number> values = record.stream()
                .filter(f -> !f.equals(dateField))
                .map(Report.Field::getAsNumber)
                .collect(Collectors.toList());

            for (int j = 0; j < values.size(); j++) {
                Number value = values.get(j);
                TimeSeries series = seriesCollection.getSeries(j);
                series.add(day, value);
            }
        }

        JFreeChart chart;
        if (chartType == ChartType.LINE) {
            chart = createTimeSeriesChart(
                reportType.getDisplayName(),
                dateLabel,
                null,
                seriesCollection
            );
        } else if (chartType == ChartType.BAR) {
            chart = createXYBarChart(
                reportType.getDisplayName(),
                dateLabel,
                true,
                null,
                seriesCollection
            );
        } else {
            throw new IllegalStateException(String.format("Chart type %s not handled.", chartType));
        }
        writeChartAsPNG(out, chart, PNG_WIDTH, PNG_HEIGHT);
    }
}
