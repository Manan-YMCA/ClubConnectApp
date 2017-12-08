package com.manan.dev.clubconnect.Models;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by shubhamsharma on 21/11/17.
 */

public class UserData {
    String userPhoneNo, userBranch, userCourse, userRollNo, photoID, name;
    Map<String, String> going, bookmarked;
    ArrayList<String> clubs;
    long userGraduationYear;

    public UserData() {
    }

    public UserData(String userPhoneNo, String userBranch, String userCourse, String userRollNo, String photoID, String name, Map<String, String> going, Map<String, String> bookmarked, ArrayList<String> clubs, long userGraduationYear) {
        this.userPhoneNo = userPhoneNo;
        this.userBranch = userBranch;
        this.userCourse = userCourse;
        this.userRollNo = userRollNo;
        this.photoID = photoID;
        this.name = name;
        this.going = going;
        this.bookmarked = bookmarked;
        this.clubs = clubs;
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

    public long getUserGraduationYear() {
        return userGraduationYear;
    }

    public void setUserGraduationYear(long userGraduationYear) {
        this.userGraduationYear = userGraduationYear;
    }

    public ArrayList<String> getClubs() {
        return clubs;
    }

    public void setClubs(ArrayList<String> clubs) {
        this.clubs = clubs;
    }
}
