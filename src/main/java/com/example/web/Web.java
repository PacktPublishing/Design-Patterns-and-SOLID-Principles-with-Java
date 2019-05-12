package com.example.web;

import com.example.Main;
import com.example.warehouse.Report;
import com.example.warehouse.Warehouse;
import com.example.warehouse.WarehouseException;
import com.example.warehouse.delivery.ReportDelivery;
import com.example.warehouse.delivery.ReportDeliveryException;
import com.example.warehouse.export.ExportType;
import com.example.warehouse.export.Exporter;
import com.example.warehouse.export.ExporterFactory;
import com.example.warehouse.plot.ChartPlotter;
import com.example.warehouse.plot.ChartType;
import com.example.warehouse.plot.ComplexChartPlotter;
import com.example.warehouse.plot.DummyChartPlotter;
import com.example.web.util.HtmlEscaperOutputStream;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Web implements Runnable {

    private static final int PORT = 8080;

    private static final VelocityTemplateEngine VELOCITY_TEMPLATE_ENGINE = new VelocityTemplateEngine();

    private static String render(Map<String, Object> model, String templatePath) {
        return VELOCITY_TEMPLATE_ENGINE.render(new ModelAndView(new HashMap<>(model), templatePath));
    }

    private final List<String> args;
    private final ExporterFactory exporterFactory;
    private final Warehouse warehouse;
    private final List<ReportDelivery> reportDeliveries;

    private ReportDelivery activeReportDelivery;

    public Web(
        List<String> args,
        ExporterFactory exporterFactory,
        Warehouse warehouse,
        List<ReportDelivery> reportDeliveries) {
        this.args = args;
        this.exporterFactory = exporterFactory;
        this.warehouse = warehouse;
        this.reportDeliveries = reportDeliveries;

        activeReportDelivery = reportDeliveries.get(0);
    }

    public void run() {
        port(PORT);
        exception(Exception.class, this::handleError);
        get("/", this::handleRoot);
        get("/products", this::handleProducts);
        get("/customers", this::handleCustomers);
        get("/orders", this::handleOrders);
        get("/reports", this::handleReports);
        get("/reports/export", this::handleExportReport);
        get("/charts", this::handleCharts);
        get("/charts/plot", this::handleChartPlot);
        get("/settings", this::handleSettings);
        get("/settings/configure-report-delivery", this::handleConfigureReportDelivery);
        post("/products/add", this::handleAddProduct);
        post("/customers/add", this::handleAddCustomer);
        post("/orders/add", this::handleAddOrder);
        post("/settings/configure-report-delivery/:choice", this::handleConfigureReportDelivery);
    }

    private <T extends Exception> void handleError(T t, Request req, Response res) {
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        Map<String, Object> model = Map.of(
            "title", "Error",
            "message", t.getMessage(),
            "stacktrace", writer.toString());
        res.body(render(model, "templates/error.html.vm"));
    }

    private Object handleRoot(Request req, Response res) {
        return render(Map.of("title", "Index"), "templates/index.html.vm");
    }

    private Object handleProducts(Request req, Response res) throws WarehouseException {
        Map<String, Object> model = Map.of(
            "title", "Manage products",
            "products", warehouse.getProducts());
        return render(model, "templates/products.html.vm");
    }

    private Object handleCustomers(Request req, Response res) throws WarehouseException {
        Map<String, Object> model = Map.of(
            "title", "Manage customers",
            "customers", warehouse.getCustomers());
        return render(model, "templates/customers.html.vm");
    }

    private Object handleOrders(Request req, Response res) throws WarehouseException {
        Map<String, Object> model = Map.of(
            "title", "Manage orders",
            "orders", warehouse.getOrders());
        return render(model, "templates/orders.html.vm");
    }

    private Object handleReports(Request req, Response res) {
        Map<String, Object> model = Map.of(
            "title", "Manage reports",
            "exportTypes", ExportType.values(),
            "reportTypes", Report.Type.values());
        return render(model, "templates/reports.html.vm");
    }

    private Object handleExportReport(Request req, Response res) throws WarehouseException {
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
        Exporter exporter = exporterFactory.newExporter(report, exportType, baos);
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
        return render(model, "templates/export-report.html.vm");
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

    private Object handleCharts(Request req, Response res) {
        Map<String, Object> model = Map.of(
            "title", "Manage charts",
            "chartTypes", ChartType.values(),
            "reportTypes", Report.Type.values());
        return render(model, "templates/charts.html.vm");
    }

    private Object handleChartPlot(Request req, Response res) throws WarehouseException {
        Report.Type reportType;
        ChartType chartType;
        try {
            reportType = Report.Type.valueOf(req.queryParams("reportType"));
            chartType = ChartType.valueOf(req.queryParams("chartType"));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Report and chart type must be specified.", ex);
        }
        Report report = warehouse.generateReport(reportType);

        ChartPlotter plotter;
        if (Main.FULL_VERSION) {
            plotter = new ComplexChartPlotter(reportType, chartType);
        } else {
            plotter = new DummyChartPlotter();
        }

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
        return render(model, "templates/plot-report.html.vm");
    }

    private Object handleSettings(Request req, Response res) {
        Map<String, Object> model = Map.of("title", "Manage settings");
        return render(model, "templates/settings.html.vm");
    }

    private Object handleConfigureReportDelivery(Request req, Response res) {
        if ("POST".equals(req.requestMethod())) {
            int choice = Integer.valueOf(req.params(":choice"));
            activeReportDelivery = reportDeliveries.get(choice - 1);
            res.redirect("/settings");
            return null;
        } else {
            Map<String, Object> model = Map.of(
                "title", "Configure report delivery",
                "reportDeliveries", reportDeliveries);
            return render(model, "templates/configure-report-delivery.html.vm");
        }
    }

    private Object handleAddProduct(Request req, Response res) throws WarehouseException {
        String name = req.queryParams("name");
        int price;
        try {
            price = Integer.valueOf(req.queryParams("price"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The product's price must be an integer.", ex);
        }
        warehouse.addProduct(name, price);
        res.redirect("/products");
        return null;
    }

    private Object handleAddCustomer(Request req, Response res) {
        throw new UnsupportedOperationException("Adding customers not yet implemented.");
    }

    private Object handleAddOrder(Request req, Response res) throws WarehouseException {
        int customerId;
        try {
            customerId = Integer.valueOf(req.queryParams("customerId"));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("The customer's ID must be an integer.", ex);
        }
        warehouse.addOrder(customerId, getQuantities(req));
        res.redirect("/orders");
        return null;
    }

    private Map<Integer, Integer> getQuantities(Request req) {
        List<String> productIds = Arrays.stream(req.queryParamsValues("productId"))
            .filter(s -> !s.isBlank())
            .collect(Collectors.toList());
        List<String> quantities = Arrays.stream(req.queryParamsValues("quantity"))
            .filter(s -> !s.isBlank())
            .collect(Collectors.toList());
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
