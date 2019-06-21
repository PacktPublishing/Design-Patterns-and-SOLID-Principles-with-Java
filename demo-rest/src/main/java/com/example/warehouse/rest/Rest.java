package com.example.warehouse.rest;

import static spark.Spark.get;

public class Rest {

    public static void main(String[] args) {
        get("/customers", (req, res) -> {
            throw new UnsupportedOperationException("Not yet implemented");
        });
        get("/customers/:id", (req, res) -> {
            throw new UnsupportedOperationException("Not yet implemented");
        });
    }
}
