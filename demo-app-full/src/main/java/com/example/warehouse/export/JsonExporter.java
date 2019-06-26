package com.example.warehouse.export;

import com.example.warehouse.Report;

import java.io.PrintStream;
import java.util.List;

public class JsonExporter implements Exporter {

    private final Report report;
    private final PrintStream out;

    JsonExporter(Report report, PrintStream out) {
        this.report = report;
        this.out = out;
    }

    @Override
    public void export() {
        out.println("[");
        List<List<Report.Field>> records = report.getRecords();
        for (int i = 0; i < records.size(); i++) {
            out.println("\t{");
            List<Report.Field> record = records.get(i);
            for (int j = 0; j < report.getLabels().size(); j++) {
                String label = report.getLabels().get(j);
                Report.Field field = record.get(j);
                out.printf("\t\t\"%s\": ", label);
                if (field.getDataType() == Report.DataType.NUMBER) {
                    // number field
                    out.printf("%s", field.getAsNumber());
                } else {
                    // date or string field
                    out.printf("\"%s\"", field.getAsString());
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
