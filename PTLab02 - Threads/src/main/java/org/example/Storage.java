package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Storage {
    private final Stack<Integer> numbers_to_check;
    private final Map<Integer, Boolean> checked_numbers;

    Storage(){
        numbers_to_check = new Stack<>();
        checked_numbers = new HashMap<>();
    }

    public synchronized void addNumberToCheck(int number) {
        numbers_to_check.push(number);
        notifyAll();
    }


    public synchronized int getNumberToCheck() {
        while (numbers_to_check.isEmpty()) {
            try {
                wait();
            }
            catch (InterruptedException e){
                return -1;
            }
        }
        int toCheck = numbers_to_check.peek();
        numbers_to_check.pop();
        notifyAll();
        return toCheck;
    }

    public synchronized void addCheckedNumber(int number, boolean is_prime){
        checked_numbers.put(number, is_prime);
    }

    Map<Integer, Boolean> getCheckedNumbers() {
        return checked_numbers;
    }
}
