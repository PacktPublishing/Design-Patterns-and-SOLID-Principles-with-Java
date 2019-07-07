package com.example;

import com.example.cli.Cli;
import com.example.warehouse.*;
import com.example.warehouse.dal.*;
import com.example.web.Web;

import javax.mail.internet.AddressException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> arguments = List.of(args);

        checkClientId(arguments);
        int clientId = parseClientId(arguments.get(0));

        ProductDao productDao = new MemoryProductDao();
        CustomerDao customerDao = new DbCustomerDao();
        InventoryDao inventoryDao = new MemoryInventoryDao(productDao);
        OrderDao orderDao = new MemoryOrderDao(productDao, customerDao);

        ReportGeneration reportGeneration;
        if (clientId == 1) {
            reportGeneration = new DefaultReportGeneration(orderDao);
        } else if (clientId == 2) {
            reportGeneration = new AlternativeReportGeneration(orderDao);
        } else {
            throw new IllegalStateException("Unknown client ID: " + clientId);
        }

        Warehouse warehouse = new Warehouse(productDao, customerDao, inventoryDao, orderDao, reportGeneration);

        ReportDelivery reportDelivery;
        try {
            reportDelivery = new EmailReportDelivery("destination@demo.com");
        } catch (AddressException ex) {
            System.err.println("Wrong email address:" + ex.getMessage());
            System.exit(1);
            return;
        }

        new Web(arguments, warehouse, reportDelivery).run();
        new Cli(arguments, warehouse, reportDelivery).run();
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

    private static int parseClientId(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException ex) {
            System.err.println(String.format("Illegal client ID: %s. It must be an integer", str));
            System.exit(1);
            // INFO: never returns 0 because of the call to System.exit.
            return 0;
        }
    }
}
