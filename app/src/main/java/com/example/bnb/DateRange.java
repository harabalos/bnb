package com.example.bnb;

import java.io.Serializable;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;

public class DateRange implements Serializable {
    private Date startDate;
    private Date endDate;

    public DateRange(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("startDate", sdf.format(startDate));
        jsonObject.put("endDate", sdf.format(endDate));
        return jsonObject;
    }

    public static DateRange fromJSON(JSONObject jsonObject) throws JSONException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(jsonObject.getString("startDate"));
            endDate = sdf.parse(jsonObject.getString("endDate"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DateRange(startDate, endDate);
    }
}
