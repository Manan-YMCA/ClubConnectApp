package com.manan.dev.clubconnect.Models;

import android.support.annotation.NonNull;

/**
 * Created by Shubham on 11/16/2017.
 */

public class TimeInterval implements Comparable<TimeInterval>{
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

    @Override
    public int compareTo(@NonNull TimeInterval ti) {
        return (int) (this.getStartTime() + this.getDate()- (ti.getStartTime()+ti.getDate()));
    }
}
