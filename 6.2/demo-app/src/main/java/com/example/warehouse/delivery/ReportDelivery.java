package com.example.warehouse.delivery;

import com.example.warehouse.Report;
import com.example.warehouse.export.ExportType;

public interface ReportDelivery {

    String getName();

    void deliver(Report.Type reportType, ExportType exportType, byte[] bytes) throws ReportDeliveryException;
}
