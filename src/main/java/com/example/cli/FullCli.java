package com.example.cli;

import com.example.warehouse.Report;
import com.example.warehouse.Warehouse;
import com.example.warehouse.delivery.ReportDelivery;
import com.example.warehouse.export.*;

import java.io.PrintStream;
import java.util.List;

public class FullCli extends Cli {

    public FullCli(List<String> args, Warehouse warehouse, List<ReportDelivery> reportDeliveries) {
        super(args, warehouse, reportDeliveries);
    }

    @Override
    protected Exporter newExporter(Report report, ExportType type, PrintStream out) {
        if (type == ExportType.CSV) {
            return new CsvExporter(report, out, true);
        } else if (type == ExportType.TXT) {
            return new TxtExporter(report, out);
        } else if (type == ExportType.HTML) {
            return new HtmlExporter(report, out);
        } else if (type == ExportType.JSON) {
            return new JsonExporter(report, out);
        } else {
            throw new IllegalStateException(String.format("Chosen exporter %s not handled, this cannot happen.", type));
        }
    }
}
