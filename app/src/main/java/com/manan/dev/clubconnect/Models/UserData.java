package com.manan.dev.clubconnect.Models;

/**
 * Created by shubhamsharma on 21/11/17.
 */

public class UserData {
    String userPhoneNo, userBranch, userCourse, userRollNo;
    long userGraduationYear;

    public UserData() {
    }

    public UserData(String userPhoneNo, String userBranch, String userCourse, String userRollNo, long userGraduationYear) {
        this.userPhoneNo = userPhoneNo;
        this.userBranch = userBranch;
        this.userCourse = userCourse;
        this.userRollNo = userRollNo;
        this.userGraduationYear = userGraduationYear;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
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
}
