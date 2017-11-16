package com.manan.dev.clubconnect.Models;

/**
 * Created by Shubham on 11/16/2017.
 */

public class TimeInterval {
    long date;
    long startTime, endTime;

    public TimeInterval(){}

    public TimeInterval(long date, long startTime, long endTime) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTime() {
        return startTime;
    }
}
