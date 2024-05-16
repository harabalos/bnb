package com.example.bnb;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.json.JSONException;


// Accommodation class to model the data and behavior of an accommodation entity
public class Accommodation implements Serializable {
    // serialVersionUID is used to ensure that a loaded class corresponds exactly to a serialized object
    private static final long serialVersionUID = 1L;

    // Fields to store the accommodation's details
    private String name; // Name of the accommodation
    private String location; // Location of the accommodation
    private int capacity; // Number of guests it can accommodate
    private ArrayList<Date> availableDates; // Dates when the accommodation is available
    private double pricePerNight; // Price per night
    private float rating; // Average rating from reviews, range 1-5
    private String imagePath; // File path to an image of the accommodation

    // Constructor to initialize an Accommodation object with all its details
    public Accommodation(String name, String location, int capacity, ArrayList<Date> availableDates,
                         double pricePerNight, float rating, String imagePath) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.availableDates = availableDates;
        this.pricePerNight = pricePerNight;
        this.rating = rating;
        this.imagePath = imagePath;
    }

    // Default constructor for the case when an Accommodation is created without any initial data
    public Accommodation() {
    }

    // Method to convert the Accommodation object's data into a JSONObject for serialization
    public JSONObject toJSON() throws JSONException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name", this.name);
        jsonObj.put("location", this.location);
        jsonObj.put("capacity", this.capacity);
        jsonObj.put("pricePerNight", this.pricePerNight);
        jsonObj.put("rating", this.rating);
        jsonObj.put("imagePath", this.imagePath);

        JSONArray datesArray = new JSONArray();
        for (Date date : this.availableDates) {
            datesArray.put(sdf.format(date)); // Format each date into the specified date format
        }
        jsonObj.put("availableDates", datesArray);

        return jsonObj;
    }

    // Static method to create an Accommodation object from a JSONObject
    public static Accommodation fromJson(JSONObject jsonObject) throws JSONException {
        Accommodation accommodation = new Accommodation();
        accommodation.name = jsonObject.getString("name");
        accommodation.location = jsonObject.getString("location");
        accommodation.capacity = jsonObject.getInt("capacity");
        accommodation.pricePerNight = jsonObject.getDouble("pricePerNight");
        accommodation.rating = (float) jsonObject.getDouble("rating");
        accommodation.imagePath = jsonObject.getString("imagePath");

        // Parsing the availableDates which are expected to be an array of date strings
        JSONArray datesArray = jsonObject.getJSONArray("availableDates");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        accommodation.availableDates = new ArrayList<>();
        for (int i = 0; i < datesArray.length(); i++) {
            try {
                accommodation.availableDates.add(dateFormat.parse(datesArray.getString(i)));
            } catch (ParseException e) {
                e.printStackTrace(); // Handle the error if date parsing fails
            }
        }

        return accommodation;
    }


    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ArrayList<Date> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(ArrayList<Date> availableDates) {
        this.availableDates = availableDates;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    // Additional methods like toString() for easy printing/logging can be added here
}