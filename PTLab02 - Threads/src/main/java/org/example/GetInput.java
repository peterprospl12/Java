package org.example;

import java.util.Scanner;

public class GetInput implements Runnable{
    private int input = 0;
    private Thread t;
    private final Storage storage;
    private CheckPrime[] threads;

    GetInput(Storage storage) {
        this.storage = storage;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(input != -1){
            input = scanner.nextInt();
            if(input == -1){
                break;
            }
            else if(input <= 1){
                System.out.println("Incorrect number");
                continue;
            }

            storage.addNumberToCheck(input);
        }

        for (CheckPrime thread : threads) {
            thread.interrupt();
        }

        storage.getCheckedNumbers().forEach((key, value) ->{
            System.out.println("Is " + key + " prime number? : " + value);
        });

    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }

        int cores = Runtime.getRuntime().availableProcessors();
        threads = new CheckPrime[cores];

        for(int i=0;i<cores;i++){
            threads[i] = new CheckPrime(storage, i);
            threads[i].start();
        }
    }
}
