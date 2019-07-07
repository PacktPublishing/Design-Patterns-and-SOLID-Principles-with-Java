package com.example.warehouse.dal;

import com.example.warehouse.Product;
import com.example.warehouse.WarehouseException;
import kong.unirest.UnirestException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class RestProductDao extends AbstractRestDao implements ProductDao {

    private static final String PRODUCTS_URL = System.getenv()
        .getOrDefault("PRODUCTS_URL", "http://localhost:9090/products");

    private static Product toProduct(JSONObject c) {
        return new Product(c.getInt("id"),
            c.getString("name"),
            c.getInt("price"));
    }

    @Override
    public Collection<Product> getProducts() throws WarehouseException {
        try {
            return getArray(PRODUCTS_URL)
                .map(JSONObject.class::cast)
                .map(RestProductDao::toProduct)
                .sorted(Comparator.comparing(Product::getId))
                .collect(toList());
        } catch (UnirestException ex) {
            throw new WarehouseException("Problem while fetching products from API.", ex);
        }
    }

    @Override
    public Product getProduct(int id) throws WarehouseException {
        try {
            return toProduct(getObject(PRODUCTS_URL + "/" + id));
        } catch (UnirestException ex) {
            throw new WarehouseException(String.format("Problem while fetching product (%s) from API", id), ex);
        }
    }

    @Override
    public void addProduct(Product product) throws WarehouseException {
        try {
            postObject(PRODUCTS_URL, Map.of(
                "name", product.getName(),
                "price", product.getPrice()
            ));
        } catch (UnirestException ex) {
            throw new WarehouseException(String.format(
                "Problem while creating product (%s, %s) from API", product.getName(), product.getPrice()), ex);
        }
    }
}
