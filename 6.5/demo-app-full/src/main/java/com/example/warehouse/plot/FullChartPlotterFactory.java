package com.example.warehouse.plot;

import com.example.warehouse.Report;

public final class FullChartPlotterFactory implements ChartPlotterFactory {

    @Override
    public ChartPlotter newPlotter(Report.Type reportType, ChartType chartType) {
        return new ComplexChartPlotter(reportType, chartType);
    }
}
