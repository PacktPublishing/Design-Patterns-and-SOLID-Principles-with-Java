package com.example.warehouse;

import com.example.warehouse.dal.*;

public final class Warehouses {

    public static Warehouse newInMemoryWarehouseClient1() {
        ProductDao productDao = new MemoryProductDao();
        CustomerDao customerDao = new MemoryCustomerDao();
        InventoryDao inventoryDao = new MemoryInventoryDao(productDao);
        OrderDao orderDao = new MemoryOrderDao(productDao, customerDao);

        ReportGeneration reportGeneration = new DefaultReportGeneration(orderDao);

        return new Warehouse(productDao, customerDao, inventoryDao, orderDao, reportGeneration);
    }

    public static Warehouse newInMemoryWarehouseClient2() {
        ProductDao productDao = new MemoryProductDao();
        CustomerDao customerDao = new MemoryCustomerDao();
        InventoryDao inventoryDao = new MemoryInventoryDao(productDao);
        OrderDao orderDao = new MemoryOrderDao(productDao, customerDao);

        ReportGeneration reportGeneration = new AlternativeReportGeneration(orderDao);

        return new Warehouse(productDao, customerDao, inventoryDao, orderDao, reportGeneration);
    }

    public static Warehouse newDbWarehouseClient1() {
        ProductDao productDao = new DbProductDao();
        CustomerDao customerDao = new DbCustomerDao();
        InventoryDao inventoryDao = new DbInventoryDao();
        OrderDao orderDao = new DbOrderDao();

        ReportGeneration reportGeneration = new DefaultReportGeneration(orderDao);

        return new Warehouse(productDao, customerDao, inventoryDao, orderDao, reportGeneration);
    }

    public static Warehouse newDbWarehouseClient2() {
        ProductDao productDao = new DbProductDao();
        CustomerDao customerDao = new DbCustomerDao();
        InventoryDao inventoryDao = new DbInventoryDao();
        OrderDao orderDao = new DbOrderDao();

        ReportGeneration reportGeneration = new AlternativeReportGeneration(orderDao);

        return new Warehouse(productDao, customerDao, inventoryDao, orderDao, reportGeneration);
    }

    private Warehouses() {
    }
}
