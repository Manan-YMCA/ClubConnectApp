package com.manan.dev.clubconnect.Models;

import java.util.ArrayList;

/**
 * Created by shubhamsharma on 17/11/17.
 */

public class SectionDataModel {
    private String headerTitle;
    private ArrayList<Event> allItemsInSection;


    public SectionDataModel() {

    }
    public SectionDataModel(String headerTitle, ArrayList<Event> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<Event> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<Event> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }

}
