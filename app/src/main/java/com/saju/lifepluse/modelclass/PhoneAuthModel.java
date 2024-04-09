package com.saju.lifepluse.modelclass;

import com.google.firebase.Timestamp;

public class PhoneAuthModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;

    boolean isform_filled = false;
    boolean isform_notfilled = true;

    public PhoneAuthModel() {
    }

    public PhoneAuthModel(String phone, String username, Timestamp createdTimestamp, String userId, boolean isform_filled, boolean isform_notfilled) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.isform_filled = isform_filled;
        this.isform_notfilled = isform_notfilled;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isIsform_filled() {
        return isform_filled;
    }

    public void setIsform_filled(boolean isform_filled) {
        this.isform_filled = isform_filled;
    }

    public boolean isIsform_notfilled() {
        return isform_notfilled;
    }

    public void setIsform_notfilled(boolean isform_notfilled) {
        this.isform_notfilled = isform_notfilled;
    }
}
