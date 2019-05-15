package com.example.warehouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WarehouseTest {

    private Warehouse warehouse;

    @BeforeEach
    void beforeEach() {
        warehouse = Warehouses.newInMemoryWarehouse(1);
    }

    @Test
    void canAddNewProduct() throws Exception {
        // given
        Collection<Product> productsBefore = warehouse.getProducts();

        // and
        String name = "test";
        int price = 123;

        // when
        warehouse.addProduct(name, price);

        // then
        Collection<Product> productsAfter = warehouse.getProducts();
        assertEquals(productsBefore.size() + 1, productsAfter.size());
    }

    @Test
    void cantAddProductWithNegativePrice() {
        // given
        String name = "test";
        int price = -123;

        // when/then
        assertThrows(IllegalArgumentException.class, () -> warehouse.addProduct(name, price));
    }

    @Test
    void canPlaceCorrectOrder() throws Exception {
        // given
        Collection<Order> ordersBefore = warehouse.getOrders();
        int customerId = 12;
        var quantities = Map.of(2, 1);

        // when
        warehouse.addOrder(customerId, quantities);

        // then
        Collection<Order> ordersAfter = warehouse.getOrders();
        assertEquals(ordersBefore.size() + 1, ordersAfter.size());
    }

    @Test
    void cantOrderForNonExistentCustomer() {
        // given
        int customerId = 666;
        var quantities = Map.of(2, 1);

        // when
        assertThrows(IllegalArgumentException.class, () -> warehouse.addOrder(customerId, quantities));
    }

    @Test
    void cantOrderMoreThanAvailableStock() {
        // given
        int customerId = 12;
        var quantities = Map.of(2, 3);

        // when
        assertThrows(IllegalArgumentException.class, () -> warehouse.addOrder(customerId, quantities));
    }
}
