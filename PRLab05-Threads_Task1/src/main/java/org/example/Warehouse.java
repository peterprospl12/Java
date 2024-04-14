package org.example;

import java.util.HashMap;
import java.util.Map;

public class Warehouse {
    private final Map<Integer, Integer> products;
    private final int max_capacity;
    private volatile boolean stop_production = false;

    Warehouse(int max_capacity) {
        this.products = new HashMap<>();
        this.max_capacity = max_capacity;
    }

    public synchronized void addProduct(int id, int value) {
        products.put(id, products.getOrDefault(id, 0) + value);
    }


    public synchronized int getProduct(int id, int value) {
        int size = products.getOrDefault(id, 0);
        int new_value;
        if (size == 0) {
            System.out.println("[W] The product: {" + id + "} is sold out!");
            return 0;
        } else if (value >= size) {
            new_value = 0;
        } else {
            new_value = size - value;
        }
        products.replace(id, new_value);
        System.out.println("[W] New value of product {" + id + "} is " + new_value);
        return Math.min(size, value);
    }

    public void stopProduction() {
        stop_production = true;
    }

    public boolean checkProductionStop() {
        return stop_production;
    }

    public int getCapacity() {
        return max_capacity;
    }
}
