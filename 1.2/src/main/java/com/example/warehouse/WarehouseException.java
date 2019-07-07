package com.example.warehouse;

public final class WarehouseException extends Exception {

    WarehouseException(String message) {
        super(message);
    }

    WarehouseException(String message, Throwable cause) {
        super(message, cause);
    }
}
