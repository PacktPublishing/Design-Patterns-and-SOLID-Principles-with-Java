package com.example.warehouse.plot;

import com.example.warehouse.Report;

import java.io.IOException;
import java.io.OutputStream;

public interface ChartPlotter {

    void plot(Report report, OutputStream out) throws IOException;
}
