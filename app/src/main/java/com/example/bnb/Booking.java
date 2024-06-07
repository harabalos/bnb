package com.example.bnb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bookingId;
    private String userId;
    private Date startDate;
    private Date endDate;

    public Booking(String bookingId, String userId, Date startDate, Date endDate) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public JSONObject toJSON() throws JSONException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("bookingId", this.bookingId);
        jsonObj.put("userId", this.userId);
        jsonObj.put("startDate", sdf.format(this.startDate));
        jsonObj.put("endDate", sdf.format(this.endDate));
        return jsonObj;
    }

    public static Booking fromJson(JSONObject jsonObject) throws JSONException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String bookingId = jsonObject.getString("bookingId");
            String userId = jsonObject.getString("userId");
            Date startDate = sdf.parse(jsonObject.getString("startDate"));
            Date endDate = sdf.parse(jsonObject.getString("endDate"));
            return new Booking(bookingId, userId, startDate, endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
//Otan kaneis booking na ftiaxnete to object kai na benei mesa sto json
//Otan kaneis booking na emfanizontai sto viewbookings tou kathena san view
//Na borei na afisei asteri kai to asteri na ginetai mo kai na benei sto json
//Otan peftei enas worker na ton swzei allos
//Na ftiaxw tin mapreduce swsta