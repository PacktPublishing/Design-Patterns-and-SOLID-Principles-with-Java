package com.example.warehouse;

import static java.lang.String.format;

public class Util {

    private static final int MIN_PORT = 0;
    private static final int MAX_PORT = 65535;

    public static int getPort() {
        String value = System.getenv("PORT");
        if (value == null || value.isBlank()) {
            throw newIllegalPort(value);
        }
        int port;
        try {
            port = Integer.valueOf(value);
        } catch (NumberFormatException ex) {
            throw newIllegalPort(value);
        }
        if (port < MIN_PORT || port > MAX_PORT) {
            throw newIllegalPort(value);
        }
        return port;
    }

    private static IllegalStateException newIllegalPort(String value) {
        throw new IllegalStateException(format("Illegal port: %s. It must be between %s and %s.", value, MIN_PORT, MAX_PORT));
    }

    private Util() {
    }
}
