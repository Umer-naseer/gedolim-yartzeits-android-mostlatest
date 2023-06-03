package com.saqibdb.YahrtzeitsOfGedolim.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EventNameWise implements Serializable {

    @SerializedName("formatted_date")
    @Expose
    private String formatted_date;
    @SerializedName("entries")
    @Expose
    private List<EventDetails> entries = null;

    public String getFormatted_date() {
        return formatted_date;
    }

    public void setFormatted_date(String formatted_date) {
        this.formatted_date = formatted_date;
    }

    public List<EventDetails> getEntries() {
        return entries;
    }

    public void setEntries(List<EventDetails> entries) {
        this.entries = entries;
    }
}
