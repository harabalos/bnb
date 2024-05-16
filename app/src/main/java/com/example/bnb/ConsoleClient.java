package com.example.bnb;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;

// This class is responsible for connecting to the server and performing actions such as adding accommodations.
public class ConsoleClient {
    // Server details for establishing the connection
    private String host;
    private int port;

    // Constructor initializes the client with server host and port
    public ConsoleClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Starts the client to interact with the server
    public void start() {
        // Try-with-resources to ensure proper closure of resources
        try (Socket socket = new Socket(host, port); // Establish a socket connection to the server
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); // Output stream to send data to the server
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream()); // Input stream to receive data from the server
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) { // Reader to get user input from the console

            String userInput; // Variable to hold user input

            // Prompt the user for input
            System.out.println("Enter 'add' to add a new accommodation, or 'exit' to quit:");
            while (!(userInput = consoleInput.readLine()).equals("exit")) { // Process user commands until 'exit' command is received
                if ("add".equals(userInput)) { // If user enters 'add', collect accommodation data
                    // Collecting accommodation details from user
                    System.out.println("Enter accommodation name:");
                    String name = consoleInput.readLine();

                    System.out.println("Enter location:");
                    String location = consoleInput.readLine();

                    System.out.println("Enter capacity (number of guests):");
                    int capacity = Integer.parseInt(consoleInput.readLine());

                    // Placeholder for dates, since it's not implemented yet
                    ArrayList<Date> availableDates = new ArrayList<>();

                    System.out.println("Enter price per night:");
                    double pricePerNight = Double.parseDouble(consoleInput.readLine());

                    System.out.println("Enter rating (1-5):");
                    float rating = Float.parseFloat(consoleInput.readLine());

                    System.out.println("Enter image path:");
                    String imagePath = consoleInput.readLine();

                    // Create an Accommodation object with the provided details
                    Accommodation accommodation = new Accommodation(name, location, capacity, availableDates, pricePerNight, rating, imagePath);
                    out.writeObject(accommodation); // Send the Accommodation object to the server

                    // Wait for a response from the server and print it
                    String response = (String) in.readObject();
                    System.out.println("Server response: " + response);
                } else {
                    System.out.println("Unknown command."); // Handle unknown commands
                }
                // Prompt for the next command
                System.out.println("Enter 'add' to add a new accommodation, or 'exit' to quit:");
            }

        } catch (UnknownHostException ex) { // Handle errors related to unknown host
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) { // Handle IO errors or errors related to class not being found
            System.out.println("Error: " + ex.getMessage());
        } catch (NumberFormatException ex) { // Handle errors related to number format
            System.out.println("Input error: Please make sure you enter valid numbers for capacity, price, and rating.");
        }
    }

    // Main method to start the client application
    public static void main(String[] args) {
        ConsoleClient client = new ConsoleClient("192.168.2.94", 4321); // Create a client instance with the specified server details
        client.start(); // Start the client
    }
}
