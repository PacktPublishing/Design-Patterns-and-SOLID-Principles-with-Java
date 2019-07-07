package com.example.warehouse;

import com.example.warehouse.dal.*;

public final class Warehouses {

    public static Warehouse newInMemoryWarehouse(int clientId) {
        ProductDao productDao = new MemoryProductDao();
        CustomerDao customerDao = new MemoryCustomerDao();
        InventoryDao inventoryDao = new MemoryInventoryDao(productDao);
        OrderDao orderDao = new MemoryOrderDao(productDao, customerDao);

        ReportGeneration reportGeneration = createReportGeneration(clientId, orderDao);

        return new Warehouse(
            productDao,
            customerDao,
            inventoryDao,
            orderDao,
            reportGeneration);
    }

    public static Warehouse newDbWarehouse(int clientId) {
        ProductDao productDao = new DbProductDao();
        CustomerDao customerDao = new RestCustomerDao();
        InventoryDao inventoryDao = new DbInventoryDao();
        OrderDao orderDao = new DbOrderDao(customerDao);

        ReportGeneration reportGeneration = createReportGeneration(clientId, orderDao);

        return new Warehouse(
            productDao,
            customerDao,
            inventoryDao,
            orderDao,
            reportGeneration);
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
