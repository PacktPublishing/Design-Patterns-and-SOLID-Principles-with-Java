package com.example.warehouse;

import com.example.warehouse.export.ExportType;

public interface ReportDelivery {

    void deliver(Report.Type reportType, ExportType exportType, byte[] bytes);
}
