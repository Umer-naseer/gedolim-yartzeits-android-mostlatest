package com.saqibdb.YahrtzeitsOfGedolim.model;

import java.util.List;

public class DayWiseEventDetail {

    private Integer eventDay;
    private List<EventDetails> eventByMeDetails = null;

    public Integer getEventDay() {
        return eventDay;
    }

    public void setEventDay(Integer eventDay) {
        this.eventDay = eventDay;
    }

    public List<EventDetails> getEventByMeDetails() {
        return eventByMeDetails;
    }

    public void setEventByMeDetails(List<EventDetails> eventByMeDetails) {
        this.eventByMeDetails = eventByMeDetails;
    }
}
