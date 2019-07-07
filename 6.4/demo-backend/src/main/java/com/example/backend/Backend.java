package com.example.backend;

import com.example.App;
import com.example.backend.util.HtmlEscaperOutputStream;
import com.example.warehouse.*;
import com.example.warehouse.delivery.ReportDeliveryException;
import com.example.warehouse.export.ExportType;
import com.example.warehouse.export.Exporter;
import com.example.warehouse.plot.ChartPlotter;
import com.example.warehouse.plot.ChartType;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static spark.Spark.*;

public class Backend extends App implements Runnable, SparkApplication {

    private static final String CONTENT_TYPE = "application/json";

    private static final Gson GSON = Util.newGson();

    @Override
    protected Warehouse getWarehouse(int clientId) {
        return Warehouses.newBackendWarehouse(clientId);
    }

    @Override
    public final void run() {
        port(Util.getPort());
        init();
    }

    @Override
    public void init() {
        before((req, res) -> res.type(CONTENT_TYPE));

        notFound((req, res) -> {
            res.status(404);
            return null;
        });

        exception(Exception.class, (t, req, res) -> {
            res.status(500);
            res.body(GSON.toJson(makeError(t, req, res)));
        });

        get("/products", (req, res) -> warehouse.getProducts(), GSON::toJson);
        get("/products/:id", (req, res) -> warehouse.getProduct(Integer.valueOf(req.params(":id"))), GSON::toJson);
        post("/products", this::doAddProduct, GSON::toJson);

        get("/customers", (req, res) -> warehouse.getCustomers(), GSON::toJson);
        get("/customers/:id", (req, res) -> warehouse.getCustomer(Integer.valueOf(req.params(":id"))), GSON::toJson);
        post("/customers", this::doAddCustomer, GSON::toJson);

        get("/orders", (req, res) -> warehouse.getOrders(), GSON::toJson);
        get("/orders/:id", (req, res) -> warehouse.getOrder(Integer.valueOf(req.params(":id"))), GSON::toJson);
        post("/orders", this::doAddOrder, GSON::toJson);

        get("/reports/export", this::makeExportReport, GSON::toJson);

        get("/charts/plot", this::makeChartPlot, GSON::toJson);
        post("/settings/configure-report-delivery/:choice", this::doConfigureReportDelivery, GSON::toJson);
    }

    @Override
    public void destroy() {
    }

    protected <T extends Exception> Map<String, Object> makeError(T t, Request req, Response res) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        Map<String, Object> map = new HashMap<>();
        map.put("title", "Error");
        map.put("stacktrace", writer.toString());
        if (t.getMessage() != null) {
            map.put("message", t.getMessage());
        }
        return map;
    }

    protected Map<String, Object> makeExportReport(Request req, Response res) throws WarehouseException {
        Report.Type reportType;
        ExportType exportType;
        try {
            reportType = Report.Type.valueOf(req.queryParams("reportType"));
            exportType = ExportType.valueOf(req.queryParams("exportType"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Report and export type must be specified.", ex);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Report report = warehouse.generateReport(reportType);
        Exporter exporter = dependencyFactory.newExporter(report, exportType, baos);
        exporter.export();

        String error = null;
        try {
            activeReportDelivery.deliver(reportType, exportType, baos.toByteArray());
        } catch (ReportDeliveryException ex) {
            error = ex.getMessage();
            System.err.println(ex.getMessage());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", String.format("%s %s export", reportType.getDisplayName(), exportType));
        model.put("error", error);
        model.put("export", exportToString(exportType, baos));
        return model;
    }

    private String exportToString(ExportType exportType, ByteArrayOutputStream baos) {
        // INFO: after refactoring the Cli and Web classes to rely on an ExportFactory in order
        // to share the same exporter instantiation logic between the two classes the way HTML
        // export result is presented needed to be moved *after* the export happened, otherwise
        // this could have been done only by code modification that would disrupt the flow of the
        // companion videos narrative, hence *this* code.
        if (exportType == ExportType.HTML) {
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            try {
                baos.writeTo(new HtmlEscaperOutputStream(temp));
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
            return temp.toString();
        }
        return baos.toString();
    }

    protected Map<String, Object> makeChartPlot(Request req, Response res) throws WarehouseException {
        Report.Type reportType;
        ChartType chartType;
        try {
            reportType = Report.Type.valueOf(req.queryParams("reportType"));
            chartType = ChartType.valueOf(req.queryParams("chartType"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Report and chart type must be specified.", ex);
        }
        Report report = warehouse.generateReport(reportType);

        ChartPlotter plotter = dependencyFactory.newPlotter(reportType, chartType);

        String error = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            OutputStream out = Base64.getEncoder().wrap(baos);
            plotter.plot(report, out);
        } catch (IOException ex) {
            error = ex.getMessage();
            System.err.println(ex.getMessage());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("title", String.format("%s %s chart", reportType.getDisplayName(), chartType));
        model.put("error", error);
        model.put("data", baos.toString());
        return model;
    }

    protected final Object doConfigureReportDelivery(Request req, Response res) {
        int choice = Integer.valueOf(req.params(":choice"));
        activeReportDelivery = reportDeliveries.get(choice - 1);
        return null;
    }

    protected final Object doAddProduct(Request req, Response res) throws WarehouseException {
        String name = req.queryParams("name");
        int price;
        try {
            price = Integer.valueOf(req.queryParams("price"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The product's price must be an integer.", ex);
        }
        warehouse.addProduct(name, price);
        return null;
    }

    protected final Object doAddCustomer(Request req, Response res) {
        throw new UnsupportedOperationException("Adding customers not yet implemented.");
    }

    protected final Object doAddOrder(Request req, Response res) throws WarehouseException {
        int customerId;
        try {
            customerId = Integer.valueOf(req.queryParams("customerId"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The customer's ID must be an integer.", ex);
        }
        warehouse.addOrder(customerId, getQuantities(req));
        return null;
    }

    private Map<Integer, Integer> getQuantities(Request req) {
        List<String> productIds = Arrays.stream(req.queryParamsValues("productId"))
            .filter(s -> !s.isBlank())
            .collect(toList());
        List<String> quantities = Arrays.stream(req.queryParamsValues("quantity"))
            .filter(s -> !s.isBlank())
            .collect(toList());
        if (productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Missing product ID or quantity.");
        }
        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < productIds.size(); i++) {
            int productId;
            int quantity;
            try {
                productId = Integer.valueOf(productIds.get(i));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("The product's ID must be an integer.", ex);
            }
            try {
                quantity = Integer.valueOf(quantities.get(i));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("The quantity must be an integer.", ex);
            }
            result.put(productId, quantity);
        }
        if (result.size() != productIds.size()) {
            throw new IllegalArgumentException("Duplicate product ID found.");
        }
        return result;
    }
}
