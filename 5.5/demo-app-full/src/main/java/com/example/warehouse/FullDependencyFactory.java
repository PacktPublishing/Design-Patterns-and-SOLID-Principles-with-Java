package com.example.warehouse;

import com.example.warehouse.export.ExportType;
import com.example.warehouse.export.Exporter;
import com.example.warehouse.export.ExporterFactory;
import com.example.warehouse.export.FullExporterFactory;
import com.example.warehouse.plot.ChartPlotter;
import com.example.warehouse.plot.ChartPlotterFactory;
import com.example.warehouse.plot.ChartType;
import com.example.warehouse.plot.FullChartPlotterFactory;

import java.io.OutputStream;

public final class FullDependencyFactory implements DependencyFactory {

    private ExporterFactory exporterFactory = new FullExporterFactory();
    private ChartPlotterFactory plotterFactory = new FullChartPlotterFactory();

    @Override
    public Exporter newExporter(Report report, ExportType type, OutputStream out) {
        return exporterFactory.newExporter(report, type, out);
    }

    @Override
    public ChartPlotter newPlotter(Report.Type reportType, ChartType chartType) {
        return plotterFactory.newPlotter(reportType, chartType);
    }
}
