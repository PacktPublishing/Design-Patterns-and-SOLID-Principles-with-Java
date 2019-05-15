package com.example.warehouse.plot;

import com.example.warehouse.Report;

import java.io.OutputStream;

public final class DummyChartPlotter implements ChartPlotter {

    @Override
    public void plot(Report report, OutputStream out) {
        throw new UnsupportedOperationException("Chart plotting not available.");
    }
}
