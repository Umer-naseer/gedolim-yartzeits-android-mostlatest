package com.saqibdb.YahrtzeitsOfGedolim.model;

/**
 * Created on 13-06-2018, 12:48:47 PM.
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HebrewDateModel {

    @SerializedName("gy")
    public Integer gy;

    @SerializedName("gm")
    public Integer gm;

    @SerializedName("gd")
    public Integer gd;

    @SerializedName("hy")
    public Integer hy;

    @SerializedName("hm")
    public String hm;

    @SerializedName("hd")
    public Integer hd;

    @SerializedName("hebrew")
    public String hebrew;

    @SerializedName("events")
    public List<String> events = null;

    public  int hm_;

    public int getHm_() {
        return hm_;
    }

    public void setHm_(int hm_) {
        this.hm_ = hm_;
    }

    public Integer getGy() {
        return gy;
    }

    public void setGy(Integer gy) {
        this.gy = gy;
    }

    public Integer getGm() {
        return gm;
    }

    public void setGm(Integer gm) {
        this.gm = gm;
    }

    public Integer getGd() {
        return gd;
    }

    public void setGd(Integer gd) {
        this.gd = gd;
    }

    public Integer getHy() {
        return hy;
    }

    public void setHy(Integer hy) {
        this.hy = hy;
    }

    public String getHm() {
        return hm.trim();
    }

    public void setHm(String hm) {
        this.hm = hm;
    }

    public Integer getHd() {
        return hd;
    }

    public void setHd(Integer hd) {
        this.hd = hd;
    }

    public String getHebrew() {
        return hebrew;
    }

    public void setHebrew(String hebrew) {
        this.hebrew = hebrew;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

}