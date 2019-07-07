package com.example.warehouse;

import com.example.warehouse.export.ExportType;

public class NoReportDelivery implements ReportDelivery {

    @Override
    public String getName() {
        return "No report delivery";
    }

    @Override
    public void deliver(Report.Type reportType, ExportType exportType, byte[] bytes) {
        // INFO: intentionally left empty.
    }
}
