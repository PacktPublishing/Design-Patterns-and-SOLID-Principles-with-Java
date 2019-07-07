package com.example.warehouse;

import com.example.warehouse.dal.*;
import com.example.warehouse.service.ExternalCustomerService;
import com.example.warehouse.service.ExternalCustomerServiceImpl;

public final class Warehouses {

    public static Warehouse newInMemoryWarehouse(int clientId) {
        ProductDao productDao = new MemoryProductDao();
        CustomerDao customerDao = new MemoryCustomerDao();
        InventoryDao inventoryDao = new MemoryInventoryDao(productDao);
        OrderDao orderDao = new MemoryOrderDao(productDao, customerDao);

        ReportGeneration reportGeneration = createReportGeneration(clientId, orderDao);

        ExternalCustomerService externalCustomerService = new ExternalCustomerServiceImpl();

        return new Warehouse(
            productDao,
            customerDao,
            inventoryDao,
            orderDao,
            reportGeneration,
            externalCustomerService);
    }

    public static Warehouse newDbWarehouse(int clientId) {
        ProductDao productDao = new DbProductDao();
        CustomerDao customerDao = new DbCustomerDao();
        InventoryDao inventoryDao = new DbInventoryDao();
        OrderDao orderDao = new DbOrderDao();

        ReportGeneration reportGeneration = createReportGeneration(clientId, orderDao);

        ExternalCustomerService externalCustomerService = new ExternalCustomerServiceImpl();

        return new Warehouse(
            productDao,
            customerDao,
            inventoryDao,
            orderDao,
            reportGeneration,
            externalCustomerService);
    }

    private static ReportGeneration createReportGeneration(int clientId, OrderDao orderDao) {
        if (clientId == 1) {
            return new DefaultReportGeneration(orderDao);
        } else if (clientId == 2) {
            return new AlternativeReportGeneration(orderDao);
        }
        throw new IllegalStateException("Unknown client ID: " + clientId);
    }

    private Warehouses() {
    }
}
