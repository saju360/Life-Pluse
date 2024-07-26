package com.saju.lifepluse.modelclass;

import com.google.firebase.Timestamp;

public class PhoneAuthModel {
    private String phone;
    private String username;
    private Timestamp createdTimestamp;
    private String userId;

    boolean isform_filled;
    boolean isform_notfilled;

    boolean isorgadd;
    boolean isorgnotadd;
    boolean isfindbbankadd ;
    boolean isfindbbanknotadd ;



    public PhoneAuthModel(String phone, String username, Timestamp createdTimestamp, String userId, boolean isform_filled, boolean isform_notfilled, boolean isorgadd, boolean isorgnotadd, boolean isfindbbankadd, boolean isfindbbanknotadd) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
        this.isform_filled = isform_filled;
        this.isform_notfilled = isform_notfilled;
        this.isorgadd = isorgadd;
        this.isorgnotadd = isorgnotadd;
        this.isfindbbankadd = isfindbbankadd;
        this.isfindbbanknotadd = isfindbbanknotadd;
    }

    public PhoneAuthModel() {
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

    public boolean isIsorgadd() {
        return isorgadd;
    }

    public void setIsorgadd(boolean isorgadd) {
        this.isorgadd = isorgadd;
    }

    public boolean isIsorgnotadd() {
        return isorgnotadd;
    }

    public void setIsorgnotadd(boolean isorgnotadd) {
        this.isorgnotadd = isorgnotadd;
    }

    public boolean isIsfindbbankadd() {
        return isfindbbankadd;
    }

    public void setIsfindbbankadd(boolean isfindbbankadd) {
        this.isfindbbankadd = isfindbbankadd;
    }

    public boolean isIsfindbbanknotadd() {
        return isfindbbanknotadd;
    }

    public void setIsfindbbanknotadd(boolean isfindbbanknotadd) {
        this.isfindbbanknotadd = isfindbbanknotadd;
    }
}
