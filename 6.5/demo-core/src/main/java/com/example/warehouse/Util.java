package com.example.warehouse;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;

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

    public static Gson newGson() {
        return new GsonBuilder()
            .setPrettyPrinting()
            .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                @Override
                public void write(JsonWriter out, LocalDate value) throws IOException {
                    out.value(value.toString());
                }

                @Override
                public LocalDate read(JsonReader in) throws IOException {
                    return LocalDate.parse(in.nextString());
                }
            })
            .create();
    }

    private static IllegalStateException newIllegalPort(String value) {
        throw new IllegalStateException(format("Illegal port: %s. It must be between %s and %s.", value, MIN_PORT, MAX_PORT));
    }

    private Util() {
    }
}
