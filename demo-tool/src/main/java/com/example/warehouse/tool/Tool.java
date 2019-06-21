package com.example.warehouse.tool;

import com.example.warehouse.Customer;
import com.example.warehouse.WarehouseException;
import com.example.warehouse.dal.CustomerDao;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Tool {

    public static void main(String[] args) {
        Map<Customer, JSONArray> allTodos = new HashMap<>();
        try {
            CustomerDao dao = null;
            Collection<Customer> customers = dao.getCustomers();
            for (Customer customer : customers) {
                JsonNode body = Unirest.get("http://localhost:3000/todos")
                    .queryString("userId", customer.getId())
                    .asJson()
                    .getBody();
                JSONArray todos = body.getArray();
                allTodos.put(customer, todos);
            }
        } catch (WarehouseException | UnirestException ex) {
            System.err.printf("Problem during execution: %s%n", ex.getMessage());
        }

        allTodos.entrySet()
            .stream()
            .sorted((a, b) -> Integer.compare(b.getValue().length(), a.getValue().length()))
            .limit(3L)
            .collect(Collectors.toList())
            .forEach(e -> System.out.printf("%s - %s (%s)%n",
                e.getKey().getId(),
                e.getKey().getName(),
                e.getValue().length()));
    }
}
