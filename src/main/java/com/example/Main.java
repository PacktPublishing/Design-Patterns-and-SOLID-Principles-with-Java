package com.example;

import com.example.cli.Cli;
import com.example.warehouse.AlternativeReportGeneration;
import com.example.warehouse.DefaultReportGeneration;
import com.example.warehouse.ReportGeneration;
import com.example.warehouse.Warehouse;
import com.example.warehouse.dal.*;
import com.example.warehouse.delivery.DirectoryReportDelivery;
import com.example.warehouse.delivery.EmailReportDelivery;
import com.example.warehouse.delivery.NoReportDelivery;
import com.example.warehouse.delivery.ReportDelivery;
import com.example.web.Web;

import javax.mail.internet.AddressException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> arguments = List.of(args);

        checkClientId(arguments);
        int clientId = parseClientId(arguments.get(0));

        ProductDao productDao = new DbProductDao();
        CustomerDao customerDao = new DbCustomerDao();
        InventoryDao inventoryDao = new DbInventoryDao();
        OrderDao orderDao = new DbOrderDao();

        ReportGeneration reportGeneration;
        if (clientId == 1) {
            reportGeneration = new DefaultReportGeneration(orderDao);
        } else if (clientId == 2) {
            reportGeneration = new AlternativeReportGeneration(orderDao);
        } else {
            throw new IllegalStateException("Unknown client ID: " + clientId);
        }

        Warehouse warehouse = new Warehouse(productDao, customerDao, inventoryDao, orderDao, reportGeneration);

        List<ReportDelivery> reportDeliveries;
        try {
            reportDeliveries = createReportDeliveries(clientId);
        } catch (AddressException ex) {
            System.err.println("Wrong email address:" + ex.getMessage());
            System.exit(1);
            return;
        }

        new Web(arguments, warehouse, reportDeliveries).run();
        new Cli(arguments, warehouse, reportDeliveries).run();
        // INFO: Needed because when Cli exists the Web
        // interface's thread will keep the app hanging.
        System.exit(0);
    }

    private static List<ReportDelivery> createReportDeliveries(int clientId) throws AddressException {
        List<ReportDelivery> result = new ArrayList<>();
        result.add(new NoReportDelivery());
        if (clientId == 1) {
            result.add(new EmailReportDelivery("destination@demo.com"));
            result.add(new DirectoryReportDelivery("."));
        } else {
            result.add(new DirectoryReportDelivery("."));
        }
        return result;
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
