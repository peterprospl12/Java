package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Producer {
    private final ExecutorService executor;
    private final Warehouse warehouse;

    public Producer(Warehouse warehouse, int threads_number) {
        this.warehouse = warehouse;
        this.executor = Executors.newFixedThreadPool(threads_number);
    }

    void run() {
        while (!warehouse.checkProductionStop()) {
            executor.submit(this::produce);
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
                System.out.println("[P] | Producer stopped");
            } else {
                System.out.println("[P]| Forcing producer to stop");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    void produce() {
        if (warehouse.checkProductionStop()) {
            return;
        }

        int production_time = Math.abs(ThreadLocalRandom.current().nextInt() % 6) + 1;
        int id = (Math.abs(ThreadLocalRandom.current().nextInt()) % warehouse.getCapacity()) + 1;
        int size = (Math.abs(ThreadLocalRandom.current().nextInt()) % warehouse.getCapacity()) + 2;

        try {
            TimeUnit.SECONDS.sleep(production_time);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        if (warehouse.checkProductionStop()) {
            return;
        }
        warehouse.addProduct(id, size);

        System.out.println("[P] | Produced item: {" + id + "," + size + "} | Time: " + production_time);
        System.out.println();

    }
}
