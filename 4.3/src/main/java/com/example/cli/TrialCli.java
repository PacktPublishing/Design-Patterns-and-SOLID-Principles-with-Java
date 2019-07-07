package com.example.cli;

import com.example.warehouse.Report;
import com.example.warehouse.Warehouse;
import com.example.warehouse.delivery.ReportDelivery;
import com.example.warehouse.export.ExportType;
import com.example.warehouse.export.Exporter;
import com.example.warehouse.export.TxtExporter;

import java.io.PrintStream;
import java.util.List;

public class TrialCli extends Cli {

    public TrialCli(List<String> args, Warehouse warehouse, List<ReportDelivery> reportDeliveries) {
        super(args, warehouse, reportDeliveries);
    }

    @Override
    Exporter newExporter(Report report, ExportType type, PrintStream out) {
        if (type == ExportType.TXT) {
            return new TxtExporter(report, out);
        } else {
            throw new UnsupportedOperationException(String.format("Chosen exporter %s not available.", type));
        }
    }
}
