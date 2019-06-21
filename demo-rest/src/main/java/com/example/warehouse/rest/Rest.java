package com.example.warehouse.rest;

import com.example.warehouse.dal.CustomerDao;
import com.example.warehouse.dal.DbCustomerDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.get;

public class Rest {

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    public static void main(String[] args) {
        CustomerDao dao = new DbCustomerDao();
        get("/customers", (req, res) -> dao.getCustomers(), GSON::toJson);
        get("/customers/:id", (req, res) -> dao.getCustomer(req.queryMap("id").integerValue()), GSON::toJson);
    }
}
