package com.manan.dev.clubconnect.Models;

/**
 * Created by yatindhingra on 14/01/18.
 */

public class ClubMember {
    private String UserId;

    public ClubMember() {
    }

    private Boolean isCoordinator;

    public ClubMember(String userId, Boolean isCoordinator) {
        UserId = userId;
        this.isCoordinator = isCoordinator;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public Boolean getCoordinator() {
        return isCoordinator;
    }

    public void setCoordinator(Boolean coordinator) {
        this.isCoordinator = coordinator;
    }
}
