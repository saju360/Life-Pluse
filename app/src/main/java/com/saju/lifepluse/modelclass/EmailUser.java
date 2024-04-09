package com.saju.lifepluse.modelclass;

import com.google.firebase.Timestamp;

public class EmailUser {

    private String name, email, password, uid;
    private Timestamp createdTimestamp;
    boolean isform_filled;
    boolean isform_notfilled;

    public EmailUser() {
    }

    public EmailUser(String name, String email, String password, String uid, Timestamp createdTimestamp, boolean isform_filled, boolean isform_notfilled) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.createdTimestamp = createdTimestamp;
        this.isform_filled = isform_filled;
        this.isform_notfilled = isform_notfilled;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
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
