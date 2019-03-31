package com.example.web;

import com.example.warehouse.Warehouse;
import com.example.warehouse.WarehouseException;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.velocity.VelocityTemplateEngine;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static spark.Spark.*;

public class Web implements Runnable {

    private static final int PORT = 8080;

    private static final VelocityTemplateEngine VELOCITY_TEMPLATE_ENGINE = new VelocityTemplateEngine();

    private static String render(Map<String, Object> model, String templatePath) {
        return VELOCITY_TEMPLATE_ENGINE.render(new ModelAndView(new HashMap<>(model), templatePath));
    }

    private final List<String> args;
    private final Warehouse warehouse;

    public Web(List<String> args, Warehouse warehouse) {
        this.args = args;
        this.warehouse = warehouse;
    }

    public void run() {
        port(PORT);
        exception(Exception.class, this::handleError);
        get("/", this::handleRoot);
        get("/products", this::handleProducts);
        get("/customers", this::handleCustomers);
        get("/orders", this::handleOrders);
        post("/products/add", this::handleAddProduct);
        post("/customers/add", this::handleAddCustomer);
        post("/orders/add", this::handleAddOrder);
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
