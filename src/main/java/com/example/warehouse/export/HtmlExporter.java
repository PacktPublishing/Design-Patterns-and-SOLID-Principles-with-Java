package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

public class HtmlExporter extends AbstractExporter {

    public HtmlExporter(Report report, OutputStream out) {
        super(report, out);
    }

    @Override
    protected void beforeLabels() {
        println("<table>");
        println("\t<thead>");
    }

    @Override
    protected void handleLabels(List<String> labels) {
        println("\t\t<tr>");
        for (String label : labels) {
            printf("\t\t\t<td>%s</td>%n", label);
        }
        println("\t\t</tr>");
    }

    @Override
    protected void afterLabels() {
        println("\t</thead>");
    }

    @Override
    protected void beforeRecords() {
        println("\t<tbody>");
    }

    @Override
    protected void handleRecord(List<String> record, boolean first, boolean last) {
        println("\t\t<tr>");
        for (String field : record) {
            printf("\t\t\t<td>%s</td>%n", field);
        }
        println("\t\t</tr>");
    }

    @Override
    protected void afterRecords() {
        println("\t</tbody>");
        println("</table>");
    }
}
