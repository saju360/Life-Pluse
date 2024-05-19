package com.saju.lifepluse.modelclass;

import com.google.firebase.Timestamp;

public class BloodDonerRequestModel {

    String  uid, name, address, identity, mobile, dateOfBirth, district, nationality, gender, bloodType, maritalStatus, identificationType, selectedorg, selectedmanualorg, donateType;

    Timestamp createdtime;
    boolean isApproved;
    boolean isDeclined;

    public BloodDonerRequestModel() {
    }

    public BloodDonerRequestModel(String uid, String name, String address, String identity, String mobile, String dateOfBirth, String district, String nationality, String gender, String bloodType, String maritalStatus, String identificationType, String selectedorg, String selectedmanualorg, String donateType, Timestamp createdtime, boolean isApproved, boolean isDeclined) {
        this.uid = uid;
        this.name = name;
        this.address = address;
        this.identity = identity;
        this.mobile = mobile;
        this.dateOfBirth = dateOfBirth;
        this.district = district;
        this.nationality = nationality;
        this.gender = gender;
        this.bloodType = bloodType;
        this.maritalStatus = maritalStatus;
        this.identificationType = identificationType;
        this.selectedorg = selectedorg;
        this.selectedmanualorg = selectedmanualorg;
        this.donateType = donateType;
        this.createdtime = createdtime;
        this.isApproved = isApproved;
        this.isDeclined = isDeclined;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getSelectedorg() {
        return selectedorg;
    }

    public void setSelectedorg(String selectedorg) {
        this.selectedorg = selectedorg;
    }

    public String getSelectedmanualorg() {
        return selectedmanualorg;
    }

    public void setSelectedmanualorg(String selectedmanualorg) {
        this.selectedmanualorg = selectedmanualorg;
    }

    public String getDonateType() {
        return donateType;
    }

    public void setDonateType(String donateType) {
        this.donateType = donateType;
    }

    public Timestamp getCreatedtime() {
        return createdtime;
    }

    public void setCreatedtime(Timestamp createdtime) {
        this.createdtime = createdtime;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isDeclined() {
        return isDeclined;
    }

    public void setDeclined(boolean declined) {
        isDeclined = declined;
    }
}
