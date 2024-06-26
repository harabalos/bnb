package com.example.bnb;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class Accommodation implements Serializable {
    private static final long serialVersionUID = 1L;


    private String name;
    private String location;
    private int capacity;
    private ArrayList<Date> availableStartDates;
    private ArrayList<Date> availableEndDates;
    private double pricePerNight;
    private float rating;
    private String imagePath;
    private ArrayList<Booking> bookings;
    private String managerId;
    private int numberOfReviews;

    public Accommodation(String name, String location, int capacity,ArrayList<Date> availableStartDates, ArrayList<Date> availableEndDates,
                         double pricePerNight, float rating, String imagePath, ArrayList<Booking> bookings, String managerId,int numberOfReviews) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.availableStartDates = availableStartDates;
        this.availableEndDates = availableEndDates;
        this.pricePerNight = pricePerNight;
        this.rating = rating;
        this.imagePath = imagePath;
        this.bookings = bookings;
        this.managerId = managerId;
        this.numberOfReviews = numberOfReviews;
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
        jsonObj.put("numberOfReviews", this.numberOfReviews);

        JSONArray startDatesArray = new JSONArray();
        for (Date date : this.availableStartDates) {
            startDatesArray.put(sdf.format(date));
        }
        jsonObj.put("availableStartDates", startDatesArray);

        JSONArray endDatesArray = new JSONArray();
        for (Date date : this.availableEndDates) {
            endDatesArray.put(sdf.format(date));
        }
        jsonObj.put("availableEndDates", endDatesArray);

        JSONArray bookingsArray = new JSONArray();
        for (Booking booking : this.bookings) {
            bookingsArray.put(booking.toJSON());
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
        accommodation.numberOfReviews = jsonObject.getInt("numberOfReviews");

        JSONArray startDatesArray = jsonObject.getJSONArray("availableStartDates");
        JSONArray endDatesArray = jsonObject.getJSONArray("availableEndDates");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        accommodation.availableStartDates = new ArrayList<>();
        accommodation.availableEndDates = new ArrayList<>();
        try {
            for (int i = 0; i < startDatesArray.length(); i++) {
                accommodation.availableStartDates.add(dateFormat.parse(startDatesArray.getString(i)));
                accommodation.availableEndDates.add(dateFormat.parse(endDatesArray.getString(i)));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray bookingsArray = jsonObject.getJSONArray("bookings");
        accommodation.bookings = new ArrayList<>();
        for (int i = 0; i < bookingsArray.length(); i++) {
            JSONObject bookingObj = bookingsArray.getJSONObject(i);
            Booking booking = Booking.fromJson(bookingObj);
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

    public ArrayList<Date> getAvailableStartDates() {
        return availableStartDates;
    }

    public void setAvailableStartDates(ArrayList<Date> availableStartDates) {
        this.availableStartDates = availableStartDates;
    }

    public ArrayList<Date> getAvailableEndDates() {
        return availableEndDates;
    }

    public void setAvailableEndDates(ArrayList<Date> availableEndDates) {
        this.availableEndDates = availableEndDates;
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

    public ArrayList<Booking> getBookings(){return this.bookings;}

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }


}