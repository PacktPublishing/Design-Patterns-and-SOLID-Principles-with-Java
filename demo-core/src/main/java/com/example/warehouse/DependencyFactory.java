package com.example.warehouse;

import com.example.warehouse.export.ExporterFactory;
import com.example.warehouse.plot.ChartPlotterFactory;

public interface DependencyFactory extends ExporterFactory, ChartPlotterFactory {
}
