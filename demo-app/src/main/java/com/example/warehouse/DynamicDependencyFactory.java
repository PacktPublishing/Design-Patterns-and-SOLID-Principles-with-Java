package com.example.warehouse;

import com.example.warehouse.export.ExportType;
import com.example.warehouse.export.Exporter;
import com.example.warehouse.plot.ChartPlotter;
import com.example.warehouse.plot.ChartType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DynamicDependencyFactory implements DependencyFactory {

    private static final String RESOURCE_NAME = "/META-INF/factories/" + DependencyFactory.class.getName();

    private final DependencyFactory dependencyFactory;

    public DynamicDependencyFactory() {
        try (InputStream is = getClass().getResourceAsStream(RESOURCE_NAME)) {
            if (is == null) {
                dependencyFactory = new TrialDependencyFactory();
            } else {
                String className = new String(is.readAllBytes()).strip();
                dependencyFactory = (DependencyFactory) Class.forName(className)
                    .getDeclaredConstructor()
                    .newInstance();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Reading from a classpath resource, cannot fail.", ex);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException("Unknown or otherwise illegal class name used.", ex);
        }
    }

    @Override
    public Exporter newExporter(Report report, ExportType type, OutputStream out) {
        return dependencyFactory.newExporter(report, type, out);
    }

    @Override
    public ChartPlotter newPlotter(Report.Type reportType, ChartType chartType) {
        return dependencyFactory.newPlotter(reportType, chartType);
    }
}
