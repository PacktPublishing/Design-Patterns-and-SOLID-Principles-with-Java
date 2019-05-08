package com.example.web;

import com.example.warehouse.Report;
import com.example.warehouse.Warehouse;
import com.example.warehouse.delivery.ReportDelivery;
import com.example.warehouse.export.*;
import com.example.web.util.HtmlEscaperOutputStream;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class FullWeb extends Web {

    public FullWeb(List<String> args, Warehouse warehouse, List<ReportDelivery> reportDeliveries) {
        super(args, warehouse, reportDeliveries);
    }

    @Override
    Exporter newExporter(Report report, ExportType type, OutputStream baos) {
        if (type == ExportType.CSV) {
            return new CsvExporter(report, new PrintStream(baos), true);
        } else if (type == ExportType.TXT) {
            return new TxtExporter(report, new PrintStream(baos));
        } else if (type == ExportType.HTML) {
            return new HtmlExporter(report, new PrintStream(new HtmlEscaperOutputStream(baos)));
        } else if (type == ExportType.JSON) {
            return new JsonExporter(report, new PrintStream(baos));
        }
        throw new IllegalStateException(String.format("Chosen exporter %s not handled, this cannot happen.", type));
    }
}
