package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    Client(String host, int port) {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             Scanner scan = new Scanner(System.in)) {

            readMessage(input);
            readMessage(input);

            String name;
            name = scan.nextLine();
            sendMessage(output, name, -1);

            readMessage(input);
            int numberOfMessages;
            while (true) {
                try {
                    numberOfMessages = Integer.parseInt(scan.nextLine());
                    break;
                } catch (NumberFormatException s) {
                    System.out.println("Incorrect value. Please enter a number.");
                }
            }

            sendMessage(output, null, numberOfMessages);

            while(numberOfMessages > 0){
                String content;
                content = scan.nextLine();
                sendMessage(output, content, 1);
                numberOfMessages--;
            }

        } catch (IOException e) {
            System.out.println("The server encountered a problem");
        }
    }

    private void readMessage(ObjectInputStream input) throws IOException {
        String message;
        try {
            message = (String) input.readObject();
            System.out.println(message);
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException(e);
        }
    }

    public static void main(String[] args){
        Client client = new Client("localhost", 50000);
    }

    private void sendMessage(ObjectOutputStream output, String message, int value) throws IOException {
        if(message != null && value != -1){
            Message mess = new Message();
            mess.setContent(message);
            mess.setNumber(value);
            try {
                output.writeObject(mess);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        else if(message != null) {
            try {
                output.writeObject(message);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }
        else if(value != -1){
            try {
                output.writeObject(value);
            } catch (IOException e) {
                throw new IOException(e);
            }
        }

    }

}
