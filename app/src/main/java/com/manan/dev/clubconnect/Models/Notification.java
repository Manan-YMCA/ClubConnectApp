package com.manan.dev.clubconnect.Models;

/**
 * Created by shubhamsharma on 09/12/17.
 */

public class Notification {

    String title, desc, photoId;
    String eventId; //for future compatibility
public Notification() {

    }
    public  Notification(String title, String desc, String photoId,String eventId)
    { this.title=title;
    this.desc=desc;
    this.photoId=photoId;
    this.eventId=eventId;

    }
    public String getTitle()
    {
        return title;
    }
    public String getPhotoId()
    {
        return photoId;

    }
    public String getDesc()
    {
        return desc;

    }

    public String getEventId() {
        return eventId;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
