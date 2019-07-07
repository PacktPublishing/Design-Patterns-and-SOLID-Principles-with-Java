package com.example.warehouse.plot;

import com.example.warehouse.Report;

public interface ChartPlotterFactory {

    ChartPlotter newPlotter(Report.Type reportType, ChartType chartType);
}
