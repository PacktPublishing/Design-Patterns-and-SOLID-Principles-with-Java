package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.PrintStream;
import java.util.List;

public class JsonExporter implements Exporter {

    private final Report report;
    private final PrintStream out;

    public JsonExporter(Report report, PrintStream out) {
        this.report = report;
        this.out = out;
    }

    @Override
    public void export() {
        out.println("[");
        List<List<String>> records = report.getRecords();
        for (int i = 0; i < records.size(); i++) {
            out.println("\t{");
            List<String> record = records.get(i);
            for (int j = 0; j < report.getLabels().size(); j++) {
                String label = report.getLabels().get(j);
                String field = record.get(j);
                out.printf("\t\t\"%s\": ", label);
                if (j == 0) {
                    // string field
                    out.printf("\"%s\"", field);
                } else {
                    // TODO: this is a hack, it is assumed that every JSON field will be a number field
                    // expect the very first. This exporter needs type information to work correctly,
                    // others don't and this piece of information is not available here.
                    // number field
                    out.printf("%s", field);
                }
                if (j != report.getLabels().size() - 1) {
                    out.print(",");
                }
                out.printf("%n");
            }
            out.print("\t}");
            if (i != records.size() - 1) {
                out.print(",");
            }
            out.println();
        }
        out.println("]");
    }
}
