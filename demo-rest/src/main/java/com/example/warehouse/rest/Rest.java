package com.example.warehouse.rest;

import com.example.warehouse.Customer;
import com.example.warehouse.Util;
import com.example.warehouse.dal.CustomerDao;
import com.example.warehouse.dal.DbCustomerDao;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;
import spark.servlet.SparkApplication;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.System.getenv;
import static java.util.stream.StreamSupport.stream;
import static spark.Spark.get;
import static spark.Spark.port;

public class Rest implements Runnable, SparkApplication {

    private static final String EXTERNAL_CUSTOMERS_URL = getenv()
        .getOrDefault("EXTERNAL_CUSTOMERS_URL", "http://localhost:3000/customers");

    private static final Gson GSON = new GsonBuilder()
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

    @Override
    public void run() {
        port(Util.getPort());
        init();
    }

    @Override
    public void init() {
        CustomerDao dao = new DbCustomerDao();
        get("/customers", (req, res) -> {
            Map<Integer, JSONObject> externalCustomers = fetchCustomers();
            return dao.getCustomers()
                .stream()
                .map(c -> {
                    JSONObject customer = externalCustomers.get(c.getId());
                    return mergeWithExternalCustomer(c, customer);
                })
                .collect(Collectors.toList());
        }, GSON::toJson);

        get("/customers/:id", (req, res) -> {
            int id = Integer.valueOf(req.params(":id"));
            return mergeWithExternalCustomer(dao.getCustomer(id), fetchCustomer(id));
        }, GSON::toJson);
    }

    @Override
    public void destroy() {
    }

    private static Map<Integer, JSONObject> fetchCustomers() throws UnirestException {
        return stream(Unirest.get(EXTERNAL_CUSTOMERS_URL)
            .asJson()
            .getBody()
            .getArray()
            .spliterator(), false)
            .map(JSONObject.class::cast)
            .collect(Collectors.toUnmodifiableMap(o -> o.getInt("id"), o -> o));
    }

    private static JSONObject fetchCustomer(int id) throws UnirestException {
        return Unirest.get(EXTERNAL_CUSTOMERS_URL + "/" + id)
            .asJson()
            .getBody()
            .getObject();
    }

    private static Customer mergeWithExternalCustomer(Customer customer, JSONObject externalCustomer) {
        JSONObject address = externalCustomer.getJSONObject("address");
        return new Customer(
            customer.getId(),
            customer.getName(),
            LocalDate.parse(externalCustomer.getString("date_of_birth")),
            externalCustomer.getString("company"),
            externalCustomer.getString("phone"),
            address.getString("street_address"),
            address.getString("city"),
            address.getString("state"),
            address.getInt("zip_code"));
    }
}
