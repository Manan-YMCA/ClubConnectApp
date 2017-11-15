package com.manan.dev.clubconnect.Models;

/**
 * Created by naman on 11/4/2017.
 */

public class Event {
    String eventName,eventDesc,eventCoordnators,photoLink;
    long startTime,endTime;

    public Event() {
    }

    public Event(String eventName, String eventDesc, String eventCoordnators, String photoLink, long startTime, long endTime) {
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.eventCoordnators = eventCoordnators;
        this.photoLink = photoLink;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventCoordnators() {
        return eventCoordnators;
    }

    public void setEventCoordnators(String eventCoordnators) {
        this.eventCoordnators = eventCoordnators;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
