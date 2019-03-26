package com.example.warehouse.dal;

import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;
import com.example.warehouse.util.CsvReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InventoryDao {

    private static class InventoryDaoHolder {
        private static final InventoryDao INSTANCE = new InventoryDao();
    }

    private static final String DEFAULT_INVENTORY_CSV_FILE = "inventory.csv";

    public static InventoryDao getInstance() {
        return InventoryDaoHolder.INSTANCE;
    }

    private final Map<Integer, Integer> inventory;

    private InventoryDao() {
        this.inventory = new HashMap<>();
        try {
            readInventory();
        } catch (FileNotFoundException ex) {
            System.err.println("Please ensure the required CSV files are present: " + ex.getMessage());
            System.exit(1);
        } catch (WarehouseException ex) {
            System.err.println("Failed to initialize the warehouse: " + ex.getMessage());
            System.exit(2);
        }
    }

    public synchronized void updateStock(Map<Product, Integer> quantities) {
        for (var entry : quantities.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            int stock = inventory.getOrDefault(product.getId(), 0);
            if (stock == 0) {
                throw new IllegalArgumentException(String.format("Product (%s) not in stock.", product.getId()));
            }
            if (stock - quantity < 0) {
                throw new IllegalArgumentException(
                    String.format("Not enough product (%s) in stock. Available %s. Ordered %s.", product.getId(), stock, quantity));
            }
        }
        for (var entry : quantities.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            int stock = inventory.getOrDefault(product.getId(), 0);
            inventory.put(product.getId(), stock - quantity);
        }
    }

    private void readInventory() throws FileNotFoundException, WarehouseException {
        CsvReader reader = new CsvReader(new FileInputStream(DEFAULT_INVENTORY_CSV_FILE));
        while (reader.hasNextRow()) {
            List<String> row = reader.nextRow();
            if (row.isEmpty()) {
                continue;
            }
            int id;
            try {
                id = Integer.valueOf(row.get(0));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read inventory: invalid product ID in CSV, must be an integer.", ex);
            }
            if (ProductDao.getInstance().getProduct(id) == null) {
                throw new WarehouseException("Failed to read inventory: unknown product ID: " + id);
            }
            int quantity;
            try {
                quantity = Integer.valueOf(row.get(1));
            } catch (NumberFormatException ex) {
                throw new WarehouseException("Failed to read inventory: invalid quantity in CSV, must be an integer.", ex);
            }
            inventory.put(id, quantity);
        }
    }
}
