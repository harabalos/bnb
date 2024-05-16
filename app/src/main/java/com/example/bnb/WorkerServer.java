package com.example.bnb;
import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

// WorkerServer class that listens for connections and processes Accommodation objects
public class WorkerServer {
    private ServerSocket serverSocket; // The server socket to listen for incoming connections
    private ConcurrentHashMap<String, Accommodation> accommodationsMap; // Thread-safe map to store accommodation data

    // Constructor that starts the server on the given port
    public WorkerServer(int port) {
        try {
            serverSocket = new ServerSocket(port); // Attempt to open a server socket on the specified port
            accommodationsMap = new ConcurrentHashMap<>(); // Initialize the ConcurrentHashMap
            System.out.println("Worker Server is listening on port " + port);
        } catch (IOException e) {
            // If an I/O error occurs when opening the socket
            System.err.println("Server could not start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method that starts listening for connections
    public void listen() {
        try {
            // Endless loop to accept new connections
            while (true) {
                Socket socket = serverSocket.accept(); // Wait and accept a connection
                new WorkerHandler(socket).start(); // Start a new thread to handle the connection
            }
        } catch (IOException e) {
            // Handle exceptions during listen/accept operations
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inner class that handles operations for each connected client
    private class WorkerHandler extends Thread {
        private Socket socket; // Socket representing the client connection

        // Constructor that assigns the client socket to this thread
        public WorkerHandler(Socket socket) {
            this.socket = socket;
        }

        // The run method is invoked when the thread starts
        public void run() {
            ObjectOutputStream output = null;
            ObjectInputStream input = null;
            try {
                // Set up streams for sending and receiving data
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush(); // Flush the stream to ensure the header is sent
                input = new ObjectInputStream(socket.getInputStream());

                // Read the Accommodation object from the input stream
                Accommodation accommodation = (Accommodation) input.readObject();
                // Log the reception of the accommodation
                System.out.println("Accommodation received: " + accommodation.getName());
                // Process the accommodation (add it to the map)
                processAccommodation(accommodation);

                // Send an acknowledgment back to the sender
                output.writeObject("Accommodation processed successfully");
                output.flush(); // Ensure the message is sent
            } catch (IOException e) {
                // Handle IO exceptions that could occur during network communication
                System.err.println("IO error in worker handler: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                // Handle errors when casting the received object to an Accommodation
                System.err.println("Class not found in worker handler: " + e.getMessage());
            } finally {
                // Clean up resources, closing socket and streams
                closeSocket();
                closeQuietly(output);
                closeQuietly(input);
            }
        }

        // Helper method to close any Closeable resource quietly
        private void closeQuietly(Closeable resource) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (IOException e) {
                    // If an IO exception occurs during close, log it
                    System.err.println("Failed to close resource quietly: " + e.getMessage());
                }
            }
        }

        // Helper method to close the socket
        private void closeSocket() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // If an IO exception occurs during socket close, log it
                System.err.println("Error when closing the socket: " + e.getMessage());
            }
        }

        // Method to process the received Accommodation object and store it
        private void processAccommodation(Accommodation accommodation) {
            // Store the accommodation in the map using its name as the key
            accommodationsMap.put(accommodation.getName(), accommodation);
            // Further processing logic can be implemented here
        }
    }

    // Main method to start the WorkerServer
    public static void main(String[] args) {
        // Check for the correct number of arguments
        if (args.length < 1) {
            System.out.println("Syntax: java WorkerServer <port>");
            return;
        }
        // Parse the port number and start the server
        int port = Integer.parseInt(args[0]);
        WorkerServer server = new WorkerServer(port);
        server.listen(); // Call the listen method to start accepting connections
    }
}
