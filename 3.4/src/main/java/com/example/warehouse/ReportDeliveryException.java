package com.example.warehouse;

public final class ReportDeliveryException extends WarehouseException {

    public ReportDeliveryException(String message) {
        super(message);
    }

    public ReportDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
