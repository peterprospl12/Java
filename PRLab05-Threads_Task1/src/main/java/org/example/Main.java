package org.example;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            int number_of_threads = 2;

            Warehouse warehouse = new Warehouse(3);
            Producer producers = new Producer(warehouse, number_of_threads);
            Consumer consumers = new Consumer(warehouse, number_of_threads);

            executor.submit(producers::run);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            executor.submit(consumers::run);
            Scanner scan = new Scanner(System.in);

            String input;
            System.out.println("[M] Waiting for input");

            do {
                input = scan.nextLine();
            } while (!input.isEmpty());
            System.out.println("[M] Stopping warehouse");

            warehouse.stopProduction();
            executor.shutdown();

            try {
                if (executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    System.out.println("[M] Closing warehouse");
                } else {
                    System.out.println("[M] Forcing to close the warehouse");
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}