package com.manan.dev.clubconnect.Models;

/**
 * Created by Shubham on 11/15/2017.
 */

public class UserSingleEventList {
    String eventPoster,eventDate,eventTime;

    public UserSingleEventList(String eventPoster, String eventDate, String eventTime) {
        this.eventPoster = eventPoster;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    public String getEventPoster() {
        return eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
