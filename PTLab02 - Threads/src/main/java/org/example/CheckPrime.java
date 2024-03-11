package org.example;

public class CheckPrime implements Runnable{
    private Thread t;
    private final int thread_number;
    private final Storage prime;

    CheckPrime(Storage storage, int thread_number) {
        this.prime = storage;
        this.thread_number = thread_number;
    }

    @Override
    public void run() {
        int toCheck;
        while (!Thread.currentThread().isInterrupted()) {
            toCheck = prime.getNumberToCheck();

            if (toCheck == -1) {
                System.out.println("Thread: " + thread_number + " has stopped");
                return;
            }

            System.out.println("Thread: " + thread_number + " started checking: " + toCheck);

            boolean isPrime = true;
            int sleepTime = 10000;

            for (int i = 2; i < toCheck / 2; i++) {
                if (toCheck % i == 0) {
                    System.out.println(toCheck + " is not prime number");
                    prime.addCheckedNumber(toCheck, false);
                    System.out.println("Thread: " + thread_number + " stopped checking: " + toCheck);

                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        System.out.println("Thread: " + thread_number + " has stopped");
                        return;
                    }

                    isPrime = false;
                    break;
                }
            }

            if (isPrime) {
                prime.addCheckedNumber(toCheck, isPrime);
                System.out.println(toCheck + " is prime number");
                System.out.println("Thread: " + thread_number + " stopped checking: " + toCheck);

                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.out.println("Thread: " + thread_number + " has stopped");
                    return;
                }
            }
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    public void interrupt() {
        if(t != null){
            t.interrupt();
        }
    }

}
