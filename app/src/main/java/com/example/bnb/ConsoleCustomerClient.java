package com.example.bnb;
import java.io.*;
import java.net.*;
import java.util.Scanner;

// The ConsoleCustomerClient provides a command-line interface for a customer to interact with the booking system.
public class ConsoleCustomerClient {
    private String host; // Server host address
    private int port; // Server port number

    // Constructor to set the server host and port for the client
    public ConsoleCustomerClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Method to start the client interface
    public void start() {
        // Establishing connection with the server and setting up streams for communication
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) { // Scanner to read user input from the console

            String userInput; // Stores the commands input by the user

            // Providing instructions to the user
            System.out.println("Enter 'search' to search for accommodations, 'book' to book an accommodation, or 'exit' to quit:");

            // The loop continues to process user commands until 'exit' is entered
            while (!(userInput = scanner.nextLine()).equals("exit")) {
                switch (userInput) {
                    case "search":
                        // Collect search criteria from the user
                        System.out.println("Enter area:");
                        String area = scanner.nextLine();
                        System.out.println("Enter dates (e.g., 2022-01-01 to 2022-01-15):");
                        String dates = scanner.nextLine();
                        System.out.println("Enter number of guests:");
                        int numberOfGuests = scanner.nextInt(); scanner.nextLine(); // Scanner trick to consume the entire line
                        System.out.println("Enter maximum price per night:");
                        double maxPrice = scanner.nextDouble(); scanner.nextLine(); // Scanner trick to consume the entire line
                        System.out.println("Enter minimum stars (1-5):");
                        int minStars = scanner.nextInt(); scanner.nextLine(); // Scanner trick to consume the entire line

                        // Create a SearchCriteria object (assumed to exist) and send it to the server
                        SearchCriteria criteria = new SearchCriteria(area, dates, numberOfGuests, maxPrice, minStars);
                        out.writeObject(criteria);

                        // Receive and display the search results from the server
                        String searchResults = (String) in.readObject();
                        System.out.println("Search Results: " + searchResults);
                        break;
                    case "book":
                        // Prompt the user for booking information
                        System.out.println("Enter the name of the accommodation you want to book:");
                        String accommodationName = scanner.nextLine();
                        out.writeObject(accommodationName); // Send the accommodation name for booking to the server

                        // Receive and display the booking response from the server
                        String bookingResponse = (String) in.readObject();
                        System.out.println("Booking Response: " + bookingResponse);
                        break;
                    default:
                        // Handle unknown commands
                        System.out.println("Unknown command.");
                        break;
                }
                // Re-prompt the user for the next command
                System.out.println("Enter 'search' to search for accommodations, 'book' to book an accommodation, or 'exit' to quit:");
            }
        } catch (UnknownHostException ex) {
            // Catch and handle exceptions related to the server host being unknown
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            // Catch and handle exceptions related to input/output and class not found
            System.out.println("Error: " + ex.getMessage());
        }
    }

    // Main method to run the client
    public static void main(String[] args) {
        // Create a client instance and start the client interface
        ConsoleCustomerClient client = new ConsoleCustomerClient("192.168.2.94", 4321);
        client.start();
    }
}
