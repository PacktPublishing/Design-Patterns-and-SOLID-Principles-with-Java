package com.example.warehouse.delivery;

public abstract class AbstractReportDelivery implements ReportDelivery {

    private final String name;

    protected AbstractReportDelivery(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return name;
    }
}
