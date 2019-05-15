package com.example.warehouse;

public interface ReportGeneration {

    Report generateReport(Report.Type type) throws WarehouseException;
}
