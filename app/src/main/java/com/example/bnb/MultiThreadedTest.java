package com.example.bnb;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

//class for testing the adding accommodation multithreading
public class MultiThreadedTest {
    public static void main(String[] args) {
        int numberOfClients = 10; // Number of client threads
        Thread[] clients = new Thread[numberOfClients];

        for (int i = 0; i < numberOfClients; i++) {
            clients[i] = new Thread(new ClientTask());
            clients[i].start();
        }

        for (int i = 0; i < numberOfClients; i++) {
            try {
                clients[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class ClientTask implements Runnable {
        @Override
        public void run() {
            try {
                Socket socket = new Socket("192.168.0.6", 4321);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                // Example of adding an accommodation
                Accommodation accommodation = new Accommodation("Test Accommodation", "Test Location", 2, new ArrayList<>(), new ArrayList<>(), 100, 4.5f, "test_image", new ArrayList<>(), "managerId", 0);
                out.writeObject(accommodation);

                // Example of viewing accommodations
                out.writeObject("view");
                out.writeObject("managerId");
                String response = (String) in.readObject();
                System.out.println("Response: " + response);

                in.close();
                out.close();
                socket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
