package com.example.bnb;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class WorkerServer {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, Accommodation> accommodationsMap; //thread safe

    public WorkerServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            accommodationsMap = new ConcurrentHashMap<>();
            System.out.println("Worker Server is listening on port " + port);
        } catch (IOException e) {
            System.err.println("Server could not start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //method that starts listening for connections
    public void listen() {
        try {
            //endless loop to accept new connections
            while (true) {
                Socket socket = serverSocket.accept(); // Wait and accept a connection
                new WorkerHandler(socket).start(); // Start a new thread to handle the connection
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class WorkerHandler extends Thread {
        private Socket socket;

        public WorkerHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            ObjectOutputStream output = null;
            ObjectInputStream input = null;
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush();
                input = new ObjectInputStream(socket.getInputStream());

                Accommodation accommodation = (Accommodation) input.readObject();
                System.out.println("Accommodation received: " + accommodation.getName());
                processAccommodation(accommodation);

                output.writeObject("Accommodation processed successfully");
                output.flush();
            } catch (IOException e) {
                System.err.println("IO error in worker handler: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found in worker handler: " + e.getMessage());
            } finally {
                closeSocket();
                closeQuietly(output);
                closeQuietly(input);
            }
        }

        private void closeQuietly(Closeable resource) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (IOException e) {
                    System.err.println("Failed to close resource quietly: " + e.getMessage());
                }
            }
        }

        private void closeSocket() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error when closing the socket: " + e.getMessage());
            }
        }

        private void processAccommodation(Accommodation accommodation) {
            accommodationsMap.put(accommodation.getName(), accommodation);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Syntax: java WorkerServer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        WorkerServer server = new WorkerServer(port);
        server.listen();
    }
}
