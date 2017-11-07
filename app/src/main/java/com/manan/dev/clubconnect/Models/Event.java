package com.manan.dev.clubconnect.Models;

/**
 * Created by naman on 11/4/2017.
 */

public class Event {
    String eventName,eventDesc,eventCoordnators;
    long startTime,endTime;

    public Event() {
    }

    public Event(String eventName, String eventDesc, String eventCoordinators, long startTime, long endTime) {
        this.eventName = eventName;
        this.eventDesc = eventDesc;
        this.eventCoordnators = eventCoordinators;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public String getEventCoordinators() {
        return eventCoordnators;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

}
