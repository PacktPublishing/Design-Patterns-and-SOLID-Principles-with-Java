package com.example.warehouse.delivery;

import com.example.warehouse.WarehouseException;

public final class ReportDeliveryException extends WarehouseException {

    public ReportDeliveryException(String message) {
        super(message);
    }

    public ReportDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
