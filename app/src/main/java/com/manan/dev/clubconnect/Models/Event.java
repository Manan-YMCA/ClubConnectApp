package com.manan.dev.clubconnect.Models;

import android.support.annotation.NonNull;

import java.util.ArrayList;

/**
 * Created by naman on 11/4/2017.
 */

public class Event implements Comparable<Event>{
    public String eventName,eventDesc,eventVenue;
    public ArrayList<String> coordinatorID;
    public ArrayList<String> attendees;
    public Photos photoID;
    public String clubName, eventId;
    public ArrayList<TimeInterval> days;
    public Boolean isPrivate;

    public Event() {
        photoID = new Photos();
    }

    public Event(String eventName, String eventDesc, String eventVenue, ArrayList<String> coordinatorID, ArrayList<String> attendees, Photos photoID, String clubName, String eventId, ArrayList<TimeInterval> days, Boolean eventType) {
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.eventVenue = eventVenue;
        this.coordinatorID = coordinatorID;
        this.attendees = attendees;
        this.photoID = photoID;
        this.clubName = clubName;
        this.eventId = eventId;
        this.days = days;
        this.isPrivate = eventType;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
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

    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
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

    @Override
    public int compareTo(@NonNull Event o) {
        if((this.getDays().get(0).getStartTime()+this.getDays().get(0).getDate()) > (o.getDays().get(0).getStartTime()+o.getDays().get(0).getDate()))
            return 1;
        else
            return -1;
    }
}
