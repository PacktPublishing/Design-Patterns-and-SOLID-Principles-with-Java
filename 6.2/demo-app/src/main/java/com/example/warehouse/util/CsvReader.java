package com.example.warehouse.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public final class CsvReader {

    private static final String DEFAULT_SEPARATOR = ",";

    private final Scanner scanner;
    private final String separator;

    public CsvReader(InputStream is) {
        this(is, DEFAULT_SEPARATOR);
    }

    private CsvReader(InputStream is, String separator) {
        this.scanner = new Scanner(is);
        this.separator = separator;
    }

    public boolean hasNextRow() {
        return scanner.hasNextLine();
    }

    public List<String> nextRow() {
        String line = scanner.nextLine();
        if (line.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.asList(line.split(separator));
    }
}
