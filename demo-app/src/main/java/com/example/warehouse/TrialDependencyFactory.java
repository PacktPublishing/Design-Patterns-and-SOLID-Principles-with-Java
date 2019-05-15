package com.example.warehouse;

import com.example.warehouse.export.ExportType;
import com.example.warehouse.export.Exporter;
import com.example.warehouse.export.ExporterFactory;
import com.example.warehouse.export.TrialExporterFactory;
import com.example.warehouse.plot.ChartPlotter;
import com.example.warehouse.plot.ChartPlotterFactory;
import com.example.warehouse.plot.ChartType;
import com.example.warehouse.plot.TrialChartPlotterFactory;

import java.io.OutputStream;

public final class TrialDependencyFactory implements DependencyFactory {

    private ExporterFactory exporterFactory = new TrialExporterFactory();
    private ChartPlotterFactory plotterFactory = new TrialChartPlotterFactory();

    @Override
    public Exporter newExporter(Report report, ExportType type, OutputStream out) {
        return exporterFactory.newExporter(report, type, out);
    }

    @Override
    public ChartPlotter newPlotter(Report.Type reportType, ChartType chartType) {
        return plotterFactory.newPlotter(reportType, chartType);
    }
}
