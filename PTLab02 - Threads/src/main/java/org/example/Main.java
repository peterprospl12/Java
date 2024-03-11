package org.example;

public class Main   {

    public static void main(String[] args) {
        Storage storage = new Storage();
        GetInput getInput = new GetInput(storage);
        getInput.start();
    }
}