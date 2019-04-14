package com.example;

import com.example.cli.Cli;
import com.example.warehouse.Warehouse;
import com.example.warehouse.dal.*;
import com.example.web.Web;

import java.util.List;

public class Main {

    public static final int CLIENT_ID = 2;

    public static void main(String[] args) {
        List<String> arguments = List.of(args);

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
}
