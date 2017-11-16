package com.manan.dev.clubconnect.Models;

import java.util.ArrayList;

/**
 * Created by Shubham on 11/16/2017.
 */

public class Photos {
    public ArrayList<String> posters, afterEvent;

    public Photos() {
    }

    public Photos(ArrayList<String> posters, ArrayList<String> afterEvent) {
        this.posters = posters;
        this.afterEvent = afterEvent;
    }

    public ArrayList<String> getPosters() {
        return posters;
    }

    public void setPosters(ArrayList<String> posters) {
        this.posters = posters;
    }

    public ArrayList<String> getAfterEvent() {
        return afterEvent;
    }

    public void setAfterEvent(ArrayList<String> afterEvent) {
        this.afterEvent = afterEvent;
    }
}
