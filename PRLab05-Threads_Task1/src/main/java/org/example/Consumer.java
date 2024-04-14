package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Consumer {
    final ExecutorService executor;
    Warehouse warehouse;

    public Consumer(Warehouse warehouse, int threads_number) {
        this.warehouse = warehouse;
        this.executor = Executors.newFixedThreadPool(threads_number);
    }

    void run() {
        while (!warehouse.checkProductionStop()) {
            executor.submit(this::consume);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }


        }
        executor.shutdown();
        try {
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                System.out.println("[C] | Consumer stopped");
            } else {
                System.out.println("[C]| Forcing consumer to stop");
                executor.shutdownNow();

            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    void consume() {
        if (warehouse.checkProductionStop()) {
            return;
        }
        int consume_time = Math.abs(ThreadLocalRandom.current().nextInt() % 6) + 1;
        int id = (Math.abs(ThreadLocalRandom.current().nextInt()) % warehouse.getCapacity()) + 1;
        int size = (Math.abs(ThreadLocalRandom.current().nextInt()) % warehouse.getCapacity()) + 1;

        try {
            TimeUnit.SECONDS.sleep(consume_time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        if (warehouse.checkProductionStop()) {
            return;
        }

        System.out.println("[C] | Consumed item: {" + id + "," + warehouse.getProduct(id, size) + "} | Time: " + consume_time);
        System.out.println();
    }
}
