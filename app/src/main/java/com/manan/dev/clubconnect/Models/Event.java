package com.manan.dev.clubconnect.Models;

import java.util.ArrayList;

/**
 * Created by naman on 11/4/2017.
 */

public class Event {
    public String eventName,eventDesc,eventVenue;
    public ArrayList<String> coordinatorID;
    public Photos photoID;
    public ArrayList<TimeInterval> days;

    public Event() {
        photoID = new Photos();
    }

    public Event(String eventName, String eventDesc, String eventVenue, ArrayList<String> coordinatorID, Photos photoID, ArrayList<TimeInterval> days) {
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.eventVenue = eventVenue;
        this.coordinatorID = coordinatorID;
        this.photoID = photoID;
        this.days = days;
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

    public String getEventVenue() {
        return eventVenue;
    }

    public void setEventVenue(String eventVenue) {
        this.eventVenue = eventVenue;
    }

    public ArrayList<String> getCoordinatorID() {
        return coordinatorID;
    }

    public void setCoordinatorID(ArrayList<String> coordinatorID) {
        this.coordinatorID = coordinatorID;
    }

    public Photos getPhotoID() {
        return photoID;
    }

    public void setPhotoID(Photos photoID) {
        this.photoID = photoID;
    }

    public ArrayList<TimeInterval> getDays() {
        return days;
    }

    public void setDays(ArrayList<TimeInterval> days) {
        this.days = days;
    }
}
