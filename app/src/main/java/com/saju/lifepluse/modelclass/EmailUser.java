package com.saju.lifepluse.modelclass;

import com.google.firebase.Timestamp;

public class EmailUser {

    private String name, email, password, uid;
    private Timestamp createdTimestamp;
    boolean isform_filled;
    boolean isform_notfilled;

    boolean isorgadd ;
    boolean isorgnotadd ;
    boolean isfindbbankadd;
    boolean isfindbbanknotadd;
    boolean ishospitaladd;
    boolean ishospitalnotadd;
    boolean ispharmacyadd ;
    boolean ispharmacynotadd;
    boolean isambulanceadd;
    boolean isambulancenotadd;


    public EmailUser(String name, String email, String password, String uid, Timestamp createdTimestamp, boolean isform_filled, boolean isform_notfilled, boolean isorgadd, boolean isorgnotadd, boolean isfindbbankadd, boolean isfindbbanknotadd, boolean ishospitaladd, boolean ishospitalnotadd, boolean ispharmacyadd, boolean ispharmacynotadd, boolean isambulanceadd, boolean isambulancenotadd, boolean b) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.uid = uid;
        this.createdTimestamp = createdTimestamp;
        this.isform_filled = isform_filled;
        this.isform_notfilled = isform_notfilled;
        this.isorgadd = isorgadd;
        this.isorgnotadd = isorgnotadd;
        this.isfindbbankadd = isfindbbankadd;
        this.isfindbbanknotadd = isfindbbanknotadd;
        this.ishospitaladd = ishospitaladd;
        this.ishospitalnotadd = ishospitalnotadd;
        this.ispharmacyadd = ispharmacyadd;
        this.ispharmacynotadd = ispharmacynotadd;
        this.isambulanceadd = isambulanceadd;
        this.isambulancenotadd = isambulancenotadd;
    }

    public EmailUser() {
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

    public boolean isIshospitaladd() {
        return ishospitaladd;
    }

    public void setIshospitaladd(boolean ishospitaladd) {
        this.ishospitaladd = ishospitaladd;
    }

    public boolean isIshospitalnotadd() {
        return ishospitalnotadd;
    }

    public void setIshospitalnotadd(boolean ishospitalnotadd) {
        this.ishospitalnotadd = ishospitalnotadd;
    }

    public boolean isIspharmacyadd() {
        return ispharmacyadd;
    }

    public void setIspharmacyadd(boolean ispharmacyadd) {
        this.ispharmacyadd = ispharmacyadd;
    }

    public boolean isIspharmacynotadd() {
        return ispharmacynotadd;
    }

    public void setIspharmacynotadd(boolean ispharmacynotadd) {
        this.ispharmacynotadd = ispharmacynotadd;
    }

    public boolean isIsambulanceadd() {
        return isambulanceadd;
    }

    public void setIsambulanceadd(boolean isambulanceadd) {
        this.isambulanceadd = isambulanceadd;
    }

    public boolean isIsambulancenotadd() {
        return isambulancenotadd;
    }

    public void setIsambulancenotadd(boolean isambulancenotadd) {
        this.isambulancenotadd = isambulancenotadd;
    }
}
