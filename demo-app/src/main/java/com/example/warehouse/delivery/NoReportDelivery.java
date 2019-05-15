package com.example.warehouse.delivery;

import com.example.warehouse.Report;
import com.example.warehouse.export.ExportType;

public class NoReportDelivery extends AbstractReportDelivery {

    public NoReportDelivery() {
        super("No report delivery");
    }

    @Override
    protected void beforeDoDeliver() {
        // INFO: no-op.
    }

    @Override
    protected void doDeliver(Report.Type reportType, ExportType exportType, byte[] bytes) {
        // INFO: intentionally left empty.
    }

    @Override
    protected void afterDoDeliver() {
        // INFO: no-op.
    }
}
