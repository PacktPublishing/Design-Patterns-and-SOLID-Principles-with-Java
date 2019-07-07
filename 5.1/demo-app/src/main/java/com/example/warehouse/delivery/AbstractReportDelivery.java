package com.example.warehouse.delivery;

import com.example.warehouse.Report;
import com.example.warehouse.export.ExportType;

public abstract class AbstractReportDelivery implements ReportDelivery {

    private final String name;

    private long tempMillis;

    protected AbstractReportDelivery(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final void deliver(Report.Type reportType, ExportType exportType, byte[] bytes) throws ReportDeliveryException {
        beforeDoDeliver();
        doDeliver(reportType, exportType, bytes);
        afterDoDeliver();
    }

    protected void beforeDoDeliver() {
        tempMillis = System.currentTimeMillis();
    }

    protected abstract void doDeliver(Report.Type reportType, ExportType exportType, byte[] bytes) throws ReportDeliveryException;

    protected void afterDoDeliver() {
        long tookMillis = System.currentTimeMillis() - tempMillis;
        System.out.printf("Report delivery took: %sms.%n", tookMillis);
    }
}
