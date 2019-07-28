package com.example.web;

import com.example.backend.Backend;
import com.example.warehouse.Report;
import com.example.warehouse.Warehouse;
import com.example.warehouse.WarehouseException;
import com.example.warehouse.Warehouses;
import com.example.warehouse.export.ExportType;
import com.example.warehouse.plot.ChartType;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.servlet.SparkApplication;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public final class Web extends Backend implements Runnable, SparkApplication {

    private static final VelocityTemplateEngine VELOCITY_TEMPLATE_ENGINE = new VelocityTemplateEngine();

    private static String render(Map<String, Object> model, String templatePath) {
        return VELOCITY_TEMPLATE_ENGINE.render(new ModelAndView(new HashMap<>(model), templatePath));
    }

    @Override
    protected Warehouse getWarehouse(int clientId) {
        return Warehouses.newFrontendWarehouse(clientId);
    }

    @Override
    public void init() {
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

    @Override
    public void destroy() {
    }

    private <T extends Exception> void handleError(T t, Request req, Response res) {
        res.status(500);
        res.body(render(makeError(t, req, res), "templates/error.html.vm"));
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
            "customers", getCustomers());
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
        return render(makeExportReport(req, res), "templates/export-report.html.vm");
    }

    private Object handleCharts(Request req, Response res) {
        Map<String, Object> model = Map.of(
            "title", "Manage charts",
            "chartTypes", ChartType.values(),
            "reportTypes", Report.Type.values());
        return render(model, "templates/charts.html.vm");
    }

    private Object handleChartPlot(Request req, Response res) throws WarehouseException {
        return render(makeChartPlot(req, res), "templates/plot-report.html.vm");
    }

    private Object handleSettings(Request req, Response res) {
        Map<String, Object> model = Map.of("title", "Manage settings");
        return render(model, "templates/settings.html.vm");
    }

    private Object handleConfigureReportDelivery(Request req, Response res) {
        if ("POST".equals(req.requestMethod())) {
            doConfigureReportDelivery(req, res);
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
        doAddProduct(req, res);
        res.redirect("/products");
        return null;
    }

    private Object handleAddCustomer(Request req, Response res) {
        return doAddCustomer(req, res);
    }

    private Object handleAddOrder(Request req, Response res) throws WarehouseException {
        doAddOrder(req, res);
        res.redirect("/orders");
        return null;
    }
}
