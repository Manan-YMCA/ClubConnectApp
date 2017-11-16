package com.manan.dev.clubconnect.Models;

import android.support.annotation.NonNull;

/**
 * Created by naman on 11/4/2017.
 */

public class Coordinator implements Comparable<Coordinator>{
    String name,email,phone, photo;

    public Coordinator() {
    } // For Firebase

    public Coordinator(String name, String email, String phone, String photoLink) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.photo = photoLink;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public int compareTo(@NonNull Coordinator coordinator) {
        return this.getName().compareTo(coordinator.getName());
    }
}
