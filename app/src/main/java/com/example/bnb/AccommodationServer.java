package com.example.bnb;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class AccommodationServer {
    // List to store all accommodations loaded from the JSON file
    private static List<Accommodation> accommodationsList = new ArrayList<>();
    // Predefined addresses for worker servers
    private static List<String> workerAddresses = List.of(
            "192.168.2.94:5001",
            "192.168.2.94:5002",
            "192.168.2.94:5003"
    );

    // Entry point for the server
    public static void main(String[] args) throws JSONException {
        // Load accommodations from JSON file into memory
        loadAccommodationsFromJson();
        int port = 4321; // The port number the server will listen on
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            // Main server loop to accept and handle client connections
            while (true) {
                Socket socket = serverSocket.accept(); // Accept a new client connection
                System.out.println("New client connected");
                new AccommodationHandler(socket).start(); // Handle the connection in a separate thread
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace(); // Print stack trace for troubleshooting
        }
    }

    // Method to load accommodations from a JSON file
    private static void loadAccommodationsFromJson() throws JSONException {
        // Try to read the accommodations.json file
        try (FileReader reader = new FileReader("accommodations.json")) {
            // Parse the JSON data into an array
            JSONArray accommodationsArray = new JSONArray(new JSONTokener(reader));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Date format for parsing

            // Iterate over each JSON object in the array and create Accommodation instances
            for (int i = 0; i < accommodationsArray.length(); i++) {
                JSONObject accommodationObject = accommodationsArray.getJSONObject(i);
                // Extract details from each JSON object
                String name = accommodationObject.getString("name");
                String location = accommodationObject.getString("location");
                int capacity = accommodationObject.getInt("capacity");
                double pricePerNight = accommodationObject.getDouble("pricePerNight");
                float rating = (float) accommodationObject.getDouble("rating");
                String imagePath = accommodationObject.getString("imagePath");

                // Parse the dates
                JSONArray jsonAvailableDates = accommodationObject.getJSONArray("availableDates");
                ArrayList<Date> availableDates = new ArrayList<>();
                for (int j = 0; j < jsonAvailableDates.length(); j++) {
                    try {
                        Date date = sdf.parse(jsonAvailableDates.getString(j));
                        availableDates.add(date);
                    } catch (ParseException e) {
                        // Handle the error if date parsing fails
                        System.err.println("Error parsing the date: " + e.getMessage());
                    }
                }

                // Create a new Accommodation object and add it to the list
                Accommodation accommodation = new Accommodation(
                        name,
                        location,
                        capacity,
                        availableDates,
                        pricePerNight,
                        rating,
                        imagePath
                );
                accommodationsList.add(accommodation);
            }
        } catch (FileNotFoundException e) {
            // If the file is not found, log an error and start with an empty list
            System.err.println("The JSON file was not found, starting with an empty list.");
        } catch (IOException e) {
            // If an IO error occurs when reading the file, log the error
            System.err.println("Error reading the JSON file: " + e.getMessage());
        }
    }

    // Inner class that handles client connections
    private static class AccommodationHandler extends Thread {
        private Socket socket;

        // Constructor that takes the client socket
        public AccommodationHandler(Socket socket) {
            this.socket = socket;
        }

        // Method where the thread's execution begins
        public void run() {
            ObjectOutputStream output = null;
            ObjectInputStream input = null;
            try {
                // Set up output and input streams for communication with the client
                output = new ObjectOutputStream(socket.getOutputStream());
                output.flush(); // Flush the stream to send the stream header
                input = new ObjectInputStream(socket.getInputStream());

                // Continuously listen for objects sent from the client
                while (true) {
                    Object obj = input.readObject(); // Read an object from the client

                    // If the received object is an Accommodation, process it
                    if (obj instanceof Accommodation) {
                        Accommodation accommodation = (Accommodation) obj;
                        // Synchronize on the shared accommodationsList
                        synchronized (accommodationsList) {
                            accommodationsList.add(accommodation); // Add new accommodation
                            sendToWorker(accommodation); // Send to worker server for processing
                            saveAccommodationsToJson(); // Save the updated list to JSON file
                        }
                        // Log the received accommodation and send a confirmation to the client
                        System.out.println("Received and added accommodation: " + accommodation.getName());
                        output.writeObject("Accommodation " + accommodation.getName() + " added successfully");
                        output.flush(); // Flush the stream to ensure the message is sent
                    }
                }
            } catch (IOException | ClassNotFoundException | JSONException ex) {
                // Handle exceptions related to IO and class not found
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            } finally {
                // In the finally block, close all resources
                try {
                    if (input != null) input.close();
                    if (output != null) output.close();
                    if (socket != null && !socket.isClosed()) socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket or streams: " + e.getMessage());
                }
            }
        }

        // Method to send an Accommodation to a worker server based on a hash function
        private void sendToWorker(Accommodation accommodation) {
            // Calculate the index of the worker server from the hash of the accommodation name
            int workerIndex = Math.abs(accommodation.getName().hashCode()) % workerAddresses.size();
            String workerAddress = workerAddresses.get(workerIndex);
            String[] parts = workerAddress.split(":"); // Split the address into IP and port

            Socket workerSocket = null;
            ObjectOutputStream toWorker = null;
            ObjectInputStream fromWorker = null;

            try {
                // Establish connection to the worker server
                workerSocket = new Socket(parts[0], Integer.parseInt(parts[1]));
                toWorker = new ObjectOutputStream(workerSocket.getOutputStream());
                toWorker.writeObject(accommodation); // Send the accommodation to the worker
                toWorker.flush(); // Flush the stream

                // Receive acknowledgment from the worker server
                fromWorker = new ObjectInputStream(workerSocket.getInputStream());
                String ack = (String) fromWorker.readObject(); // Read the acknowledgment
                System.out.println("Acknowledgment from worker: " + ack); // Log acknowledgment

            } catch (IOException e) {
                // Handle IO exceptions during communication with worker
                System.err.println("IO Error when communicating with worker: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                // Handle exceptions related to class not found during acknowledgment read
                System.err.println("Class not found error when reading acknowledgment: " + e.getMessage());
            } finally {
                // Close resources safely in the finally block
                closeResource(fromWorker);
                closeResource(toWorker);
                closeResource(workerSocket);
            }
        }

        // Method to close Closeable resources such as streams and sockets
        private void closeResource(Closeable resource) {
            if (resource != null) {
                try {
                    resource.close(); // Attempt to close the resource
                } catch (IOException e) {
                    System.err.println("Failed to close resource: " + e.getMessage()); // Log failure
                }
            }
        }

        // Method to save the current state of accommodations to a JSON file
        private void saveAccommodationsToJson() throws JSONException {
            // Convert accommodationsList to a JSONArray
            JSONArray accommodationsArray = new JSONArray();
            for (Accommodation accommodation : accommodationsList) {
                accommodationsArray.put(accommodation.toJSON()); // Add each accommodation as a JSON object
            }

            // Write the JSONArray to the file
            try (FileWriter file = new FileWriter("/Users/orfanidisandreas/Downloads/bnb/accommodations.json")) {
                file.write(accommodationsArray.toString(4)); // Write JSON with indentation
            } catch (IOException e) {
                // Handle exceptions related to file writing
                e.printStackTrace();
            }
        }
    }
}