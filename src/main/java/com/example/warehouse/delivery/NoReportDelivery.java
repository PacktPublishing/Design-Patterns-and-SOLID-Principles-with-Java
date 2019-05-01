package com.example.warehouse.delivery;

import com.example.warehouse.Report;
import com.example.warehouse.export.ExportType;

public class NoReportDelivery extends AbstractReportDelivery {

    public NoReportDelivery() {
        super("No report delivery");
    }

    @Override
    public void deliver(Report.Type reportType, ExportType exportType, byte[] bytes) {
        // INFO: intentionally left empty.
    }
}
