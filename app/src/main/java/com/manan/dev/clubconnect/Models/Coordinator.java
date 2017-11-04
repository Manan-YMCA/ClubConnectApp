package com.manan.dev.clubconnect.Models;

/**
 * Created by naman on 11/4/2017.
 */

public class Coordinator {
    String name,email,phone,photoLink;

    public Coordinator(String name, String email, String phone, String photoLink) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.photoLink = photoLink;
    }

    public Coordinator(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }
}
