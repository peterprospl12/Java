package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Long.parseLong;

public class Main {
    public static void main(String[] args) {
        int threads_size = 4;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threads_size);
        try (ExecutorService executor = Executors.newFixedThreadPool(threads_size)) {
            CachingPrimeChecker cachingPrimeChecker = new CachingPrimeChecker();
            String input = "";
            Scanner scanner = new Scanner(System.in);
            long[] numbers_to_check = new long[4];
            while (true) {
                for (int i = 0; i < 4; i++) {
                    input = scanner.nextLine();
                    if (input.equals("end")) {
                        break;
                    }
                    numbers_to_check[i] = parseLong(input);
                }
                if (input.equals("end")) {
                    break;
                }
                for (Long number : numbers_to_check) {
                    executor.submit(() -> {
                        boolean is_prime = cachingPrimeChecker.isPrime(number);
                        try {
                            cyclicBarrier.await();
                        } catch (InterruptedException | BrokenBarrierException e) {
                            throw new RuntimeException(e);
                        }
                        if(is_prime) {
                            System.out.println(number + " is prime!");
                        }
                        else {
                            System.out.println(number + " is not prime!");
                        }

                    });
                }
            }
            executor.shutdown();
            if(executor.awaitTermination(3, TimeUnit.SECONDS)) {
                System.out.println("Stopping the program");
            }
            else {
                System.out.println("Force stopping the program");
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CachingPrimeChecker {
        private final ReentrantLock lock = new ReentrantLock(true);
        // Value : | -1 = checkingByAnotherThread | 0 = notPrime | 1 = Prime |
        private final Map<Long, Integer> cache = new HashMap<>();


        public boolean isPrime(final long x) {
            int check_value = -1;
            while (check_value == -1) {
                lock.lock();
                try {
                    if (!cache.containsKey(x)){
                        cache.put(x, -1);
                        check_value = 2;
                    }
                    else {
                        check_value = cache.get(x);
                    }
                } finally {
                    lock.unlock();
                }
                if (check_value == -1) {
                    try {
                        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (check_value == 0){
                return false;
            }
            else if(check_value == 1){
                return true;
            }
            else {
                boolean is_prime = computeIfIsPrime(x);
                lock.lock();
                try {
                    cache.put(x, is_prime ? 1 : 0);
                }
                finally {
                    lock.unlock();
                }
                return is_prime;
            }


        }

        private boolean computeIfIsPrime(long x) {
            final String currentThreadName = Thread.currentThread().getName();
            System.out.printf("\t[%s] Running computation for: %d%n", currentThreadName, x);
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (x < 2) {
                return false;
            }
            for (long i = 2; i * i <= x; i++) {
                if (x % i == 0) {
                    return false;
                }
            }
            return true;
        }
    }
}
