package com.example.bnb;

import java.io.Serializable;
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
}
//Ta bookings stis imeromines na benoun kala sta json kai na ginetai update sto view
//Otan kaneis booking na ftiaxnete to object kai na benei mesa sto json
//Otan kaneis booking na emfanizontai sto viewbookings tou kathena san view
//Na borei na afisei asteri kai to asteri na ginetai mo kai na benei sto json
//Otan peftei enas worker na ton swzei allos
//Na ftiaxw tin mapreduce swsta