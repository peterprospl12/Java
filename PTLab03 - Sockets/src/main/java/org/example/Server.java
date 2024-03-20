package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class Server implements Runnable{

    private final ServerSocket serverSocket;
    private final Thread customerService;
    private final LinkedList<Socket> sockets;
    private final LinkedList<Socket> activeSockets;

    private final LinkedList<Thread> threads;

    Server(int port)
    {
        try {
            this.serverSocket = new ServerSocket(port);
            this.sockets = new LinkedList<>();
            this.threads = new LinkedList<>();
            this.activeSockets = new LinkedList<>();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Thread serverService = new Thread(this, "ServerService");
        System.out.println("Starting the server");
        this.customerService = new Thread(this, "CustomerService");
        serverService.start();
        this.customerService.start();

        try {
            serverService.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(!threads.isEmpty()){
            for (Thread thread : threads) {
                thread.interrupt();
            }
        }
        if(!sockets.isEmpty()){
            for(Socket socket : sockets){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if(!activeSockets.isEmpty()){
            for(Socket socket : activeSockets){
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server has been stopped");

    }

    public static void main(String[] args){
        Server server = new Server(50000);

    }

    @Override
    public void run() {
        if(Thread.currentThread().getName().equals("ServerService")) {
            computeServerService();
        } else if (Thread.currentThread().getName().equals("CustomerService")) {
            computeCustomerService();

        } else{
            computeClient();
        }
    }

    private void computeCustomerService() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                sockets.add(serverSocket.accept());
                threads.add(new Thread(this));
                threads.getLast().start();

            } catch(IOException e) {
                return;
            }
        }
    }

    private void computeServerService(){
        Scanner scan = new Scanner(System.in);
        while(!Thread.currentThread().isInterrupted()){
            String input = scan.nextLine();
            switch (input) {
                case "p" -> System.out.println(serverSocket.toString());
                case "stop" -> {
                    System.out.println("Stopping the server");
                    Thread.currentThread().interrupt();
                    customerService.interrupt();
                }
                case "threads" -> System.out.println(threads.toString());
            }
        }

    }

    private void computeClient(){
        System.out.println("Starting new " + Thread.currentThread().getName());
        Socket clientSocket = getClient();
        if(clientSocket == null){
            System.out.println("There's no free sockets");
            Thread.currentThread().interrupt();
            removeFinishedThreads();
            return;
        }
        String name = "";
        try (ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())){
            System.out.println("Client accepted");


            out.writeObject("Connected to server [Ready]");
            out.writeObject("Write your name");
            try {
                 name = (String) in.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }
            out.writeObject("Hello, " + name + " ! How many messages do you want to send?");

            int numberOfMessages = -1;
            try {
                numberOfMessages = (Integer) in.readObject();
                System.out.println("[" + name + "] has joined with " + numberOfMessages + " messages!" );
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            Message mess;
            while(numberOfMessages > 0){
                try{
                    mess = (Message) in.readObject();
                    System.out.println("[" + name + "]: " + mess.getContent() + " [Left: " + --numberOfMessages + "]");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }
            }
            System.out.println("[" + name + "]: has left");

            Thread.currentThread().interrupt();
            removeFinishedThreads();
            activeSockets.remove(clientSocket);

        } catch (SocketException e) {
            System.out.println("Server has been shut down, stopping client service.");

        } catch (IOException e) {
            System.out.println("[" + name + "]: quit the server");
        }
        finally {
            try {
                clientSocket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Thread.currentThread().interrupt();
            removeFinishedThreads();
            activeSockets.remove(clientSocket);
        }


    }

    private synchronized Socket getClient() {
        while (sockets.isEmpty()) {
            try {
                wait();
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
                return null;
            }
        }

        Socket client = sockets.getFirst();
        sockets.removeFirst();
        activeSockets.add(client);
        notifyAll();
        return client;

    }

    public void removeFinishedThreads() {
        threads.removeIf(thread -> !thread.isAlive() || thread.isInterrupted());
    }



}
