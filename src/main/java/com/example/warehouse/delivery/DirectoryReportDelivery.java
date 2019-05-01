package com.example.warehouse.delivery;

import com.example.warehouse.Report;
import com.example.warehouse.export.ExportType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DirectoryReportDelivery extends AbstractReportDelivery {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");

    private final Path dir;

    public DirectoryReportDelivery(String directoryPath) {
        super("File-based report delivery");
        this.dir = Paths.get(directoryPath);
    }

    @Override
    public void deliver(Report.Type reportType, ExportType exportType, byte[] bytes) throws ReportDeliveryException {
        String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String name = reportType.getDisplayName();
        String extension = exportType.getFileExtension();
        String filename = String.format("%s-%s.%s", timestamp, name, extension);
        try {
            Files.write(dir.resolve(filename), bytes);
        } catch (IOException ex) {
            throw new ReportDeliveryException("Problem while delivering report to directory.", ex);
        }
    }
}
