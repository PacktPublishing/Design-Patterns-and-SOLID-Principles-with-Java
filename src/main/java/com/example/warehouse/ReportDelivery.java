package com.example.warehouse;

import com.example.warehouse.export.ExportType;

public interface ReportDelivery {

    String getName();

    void deliver(Report.Type reportType, ExportType exportType, byte[] bytes) throws ReportDeliveryException;
}
