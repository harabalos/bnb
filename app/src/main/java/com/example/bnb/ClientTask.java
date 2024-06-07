package com.example.bnb;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//TEST FOR SEARCHING FILTERS
public class ClientTask{
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 4321);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Example of adding an accommodation
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<Date> availableStartDates = new ArrayList<>();
            ArrayList<Date> availableEndDates = new ArrayList<>();
            availableStartDates.add(dateFormat.parse("2024-06-01"));
            availableEndDates.add(dateFormat.parse("2024-06-15"));

            Accommodation accommodation = new Accommodation("Test Accommodation", "Test Location", 2, availableStartDates, availableEndDates, 100, 4.5f, "test_image", new ArrayList<>(), "managerId", 0);

            // Log the accommodation details
            System.out.println("Adding accommodation: " + accommodation.toJSON().toString());

            out.writeObject(accommodation);

            // Read response
            String response = (String) in.readObject();
            System.out.println("Response: " + response);

            // Example of searching for accommodations
            JSONObject searchFilters = new JSONObject();
            searchFilters.put("location", "Test Location");
            searchFilters.put("startDate", "2024-06-01");
            searchFilters.put("endDate", "2024-06-15");
            searchFilters.put("capacity", 2);
            searchFilters.put("minPrice", 50);
            searchFilters.put("maxPrice", 150);
            searchFilters.put("rating", 4.0);
            out.writeObject("search");
            out.writeObject(searchFilters.toString());
            response = (String) in.readObject();
            System.out.println("Search Response: " + response);

            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
