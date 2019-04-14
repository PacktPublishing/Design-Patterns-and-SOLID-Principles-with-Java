package com.example;

import com.example.cli.Cli;
import com.example.warehouse.Warehouse;
import com.example.warehouse.dal.*;
import com.example.web.Web;

import java.util.List;

public class Main {

    public static int CLIENT_ID;

    public static void main(String[] args) {
        List<String> arguments = List.of(args);

        checkClientId(arguments);
        parseClientId(arguments.get(0));

        ProductDao productDao = new MemoryProductDao();
        CustomerDao customerDao = new DbCustomerDao();
        InventoryDao inventoryDao = new MemoryInventoryDao(productDao);
        OrderDao orderDao = new MemoryOrderDao(productDao, customerDao);
        Warehouse warehouse = new Warehouse(productDao, customerDao, inventoryDao, orderDao);

        new Web(arguments, warehouse).run();
        new Cli(arguments, warehouse).run();
        // INFO: Needed because when Cli exists the Web
        // interface's thread will keep the app hanging.
        System.exit(0);
    }

    private static void checkClientId(List<String> arguments) {
        if (arguments.size() < 1) {
            System.err.println("The client ID must be specified.");
            System.exit(1);
        }
    }

    private static void parseClientId(String str) {
        try {
            CLIENT_ID = Integer.valueOf(str);
        } catch (NumberFormatException ex) {
            System.err.println(String.format("Illegal client ID: %s. It must be an integer", str));
            System.exit(1);
        }
    }
}
