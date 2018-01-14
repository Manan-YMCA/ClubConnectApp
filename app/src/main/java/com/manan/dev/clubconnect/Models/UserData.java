package com.manan.dev.clubconnect.Models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shubhamsharma on 21/11/17.
 */

public class UserData {
    String userPhoneNo, userBranch, userCourse, userRollNo, photoID, name, emailId;
    Map<String, String> going, bookmarked;
    ArrayList<String> pendingClubs, myClubs;
    Long userGraduationYear;

    @Exclude
    public String UID;
    @Exclude
    public String tempData;

    public UserData() {
    }

    public UserData(String userPhoneNo, String userBranch, String userCourse, String userRollNo, String photoID, String name, String emailId, Map<String, String> going, Map<String, String> bookmarked, ArrayList<String> pendingClubs, ArrayList<String> myClubs, Long userGraduationYear) {

        this.userPhoneNo = userPhoneNo;
        this.userBranch = userBranch;
        this.userCourse = userCourse;
        this.userRollNo = userRollNo;
        this.photoID = photoID;
        this.name = name;
        this.emailId = emailId;
        this.bookmarked = bookmarked;
        this.going = going;
        this.pendingClubs = pendingClubs;
        this.myClubs = myClubs;
        this.userGraduationYear = userGraduationYear;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public Map<String, String> getGoing() {
        return going;
    }

    public void setGoing(Map<String, String> going) {
        this.going = going;
    }

    public Map<String, String> getBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(Map<String, String> bookmarked) {
        this.bookmarked = bookmarked;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
    }

    public String getUserBranch() {
        return userBranch;
    }

    public void setUserBranch(String userBranch) {
        this.userBranch = userBranch;
    }

    public String getUserCourse() {
        return userCourse;
    }

    public void setUserCourse(String userCourse) {
        this.userCourse = userCourse;
    }

    public String getUserRollNo() {
        return userRollNo;
    }

    public void setUserRollNo(String userRollNo) {
        this.userRollNo = userRollNo;
    }

    public Long getUserGraduationYear() {
        return userGraduationYear;
    }

    public void setUserGraduationYear(long userGraduationYear) {
        this.userGraduationYear = new Long(userGraduationYear);
    }

    public ArrayList<String> getPendingClubs() {
        return pendingClubs;
    }

    public void setPendingClubs(ArrayList<String> pendingClubs) {
        this.pendingClubs = pendingClubs;
    }

    public ArrayList<String> getMyClubs() {
        return myClubs;
    }

    public void setMyClubs(ArrayList<String> myClubs) {
        this.myClubs = myClubs;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("photoID", photoID);
        result.put("name", name);
        result.put("emailId", emailId);
        result.put("pendingClubs", pendingClubs);
        result.put("bookmarked", bookmarked);
        result.put("going",going);
        result.put("userBranch", userBranch);
        result.put("userCourse", userCourse);
        result.put("userGraduationYear", userGraduationYear);
        result.put("userPhoneNo", userPhoneNo);
        result.put("userRollNo", userRollNo);
        result.put("myClubs", myClubs);
        return  result;
    }
}
