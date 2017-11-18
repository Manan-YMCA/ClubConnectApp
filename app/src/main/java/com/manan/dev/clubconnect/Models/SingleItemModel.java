package com.manan.dev.clubconnect.Models;

/**
 * Created by shubhamsharma on 17/11/17.
 */

public class SingleItemModel {
    private String eventName;
    private String imageUrl;
    private String clubName;
    private String eventId;
    private long eventDate;
    private long eventTime;

    public SingleItemModel(String eventId, String eventName, String imageUrl, String clubName, long eventDate, long eventTime) {
        this.eventName = eventName;
        this.imageUrl = imageUrl;
        this.clubName = clubName;
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
    }

    public SingleItemModel() {
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getEventDate() {
        return eventDate;
    }

    public void setEventDate(Long eventDate) {
        this.eventDate = eventDate;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}
