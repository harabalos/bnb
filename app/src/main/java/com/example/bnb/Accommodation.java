package com.example.bnb;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.text.ParseException;

// Accommodation class to model the data and behavior of an accommodation entity
public class Accommodation implements Serializable {
    // serialVersionUID is used to ensure that a loaded class corresponds exactly to a serialized object
    private static final long serialVersionUID = 1L;


    private String name;
    private String location;
    private int capacity;
    private ArrayList<Date> availableDates;
    private double pricePerNight;
    private float rating;
    private String imagePath;
    private ArrayList<Booking> bookings;
    private String managerId;

    public Accommodation(String name, String location, int capacity, ArrayList<Date> availableDates,
                         double pricePerNight, float rating, String imagePath, ArrayList<Booking> bookings, String managerId) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.availableDates = availableDates;
        this.pricePerNight = pricePerNight;
        this.rating = rating;
        this.imagePath = imagePath;
        this.bookings = bookings;
        this.managerId = managerId;
    }


    public Accommodation(){}


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
            datesArray.put(sdf.format(date));
        }
        jsonObj.put("availableDates", datesArray);

        JSONArray bookingsArray = new JSONArray();
        for (Booking booking : this.bookings) {
            JSONObject bookingObj = new JSONObject();
            bookingObj.put("bookingId", booking.getBookingId());
            bookingObj.put("userId", booking.getUserId());
            bookingObj.put("startDate", sdf.format(booking.getStartDate()));
            bookingObj.put("endDate", sdf.format(booking.getEndDate()));
            bookingsArray.put(bookingObj);
        }
        jsonObj.put("bookings", bookingsArray);
        jsonObj.put("managerId", this.managerId);

        return jsonObj;
    }

    public static Accommodation fromJson(JSONObject jsonObject) throws JSONException {
        Accommodation accommodation = new Accommodation();
        accommodation.name = jsonObject.getString("name");
        accommodation.location = jsonObject.getString("location");
        accommodation.capacity = jsonObject.getInt("capacity");
        accommodation.pricePerNight = jsonObject.getDouble("pricePerNight");
        accommodation.rating = (float) jsonObject.getDouble("rating");
        accommodation.imagePath = jsonObject.getString("imagePath");

        JSONArray datesArray = jsonObject.getJSONArray("availableDates");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        accommodation.availableDates = new ArrayList<>();
        for (int i = 0; i < datesArray.length(); i++) {
            try {
                accommodation.availableDates.add(dateFormat.parse(datesArray.getString(i)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        JSONArray bookingsArray = jsonObject.getJSONArray("bookings");
        accommodation.bookings = new ArrayList<>();
        for (int i = 0; i < bookingsArray.length(); i++) {
            JSONObject bookingObj = bookingsArray.getJSONObject(i);
            String bookingId = bookingObj.getString("bookingId");
            String userId = bookingObj.getString("userId");
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = dateFormat.parse(bookingObj.getString("startDate"));
                endDate = dateFormat.parse(bookingObj.getString("endDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Booking booking = new Booking(bookingId, userId, startDate, endDate);
            accommodation.bookings.add(booking);
        }

        accommodation.managerId = jsonObject.getString("managerId");

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

    public String getManagerId(){
        return this.managerId;
    }


}