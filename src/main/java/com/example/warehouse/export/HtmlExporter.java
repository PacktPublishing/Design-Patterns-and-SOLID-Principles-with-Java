package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.PrintStream;
import java.util.List;

public class HtmlExporter extends AbstractExporter {

    public HtmlExporter(Report report, PrintStream out) {
        super(report, out);
    }

    @Override
    protected void beforeLabels(PrintStream out) {
        out.println("<table>");
        out.println("\t<thead>");
    }

    @Override
    protected void handleLabels(PrintStream out, List<String> labels) {
        out.println("\t\t<tr>");
        for (String label : labels) {
            out.printf("\t\t\t<td>%s</td>%n", label);
        }
        out.println("\t\t</tr>");
    }

    @Override
    protected void afterLabels(PrintStream out) {
        out.println("\t</thead>");
    }

    @Override
    protected void beforeRecords(PrintStream out) {
        out.println("\t<tbody>");
    }

    @Override
    protected void handleRecord(PrintStream out, List<String> record, boolean first, boolean last) {
        out.println("\t\t<tr>");
        for (String field : record) {
            out.printf("\t\t\t<td>%s</td>%n", field);
        }
        out.println("\t\t</tr>");
    }

    @Override
    protected void afterRecords(PrintStream out) {
        out.println("\t</tbody>");
        out.println("</table>");
    }
}
