package com.saju.lifepluse.modelclass;

public class BloodNeedPostModel {


    String bloodNeed, bloodgroup, date, bloodQty, hospital, division, manualDivision, reference, contact, currentTime, documentId;

    public BloodNeedPostModel() {
    }

    public BloodNeedPostModel(String bloodNeed, String bloodgroup, String date, String bloodQty, String hospital, String division, String manualDivision, String reference, String contact, String currentTime, String documentId) {
        this.bloodNeed = bloodNeed;
        this.bloodgroup = bloodgroup;
        this.date = date;
        this.bloodQty = bloodQty;
        this.hospital = hospital;
        this.division = division;
        this.manualDivision = manualDivision;
        this.reference = reference;
        this.contact = contact;
        this.currentTime = currentTime;
        this.documentId = documentId;
    }

    public String getBloodNeed() {
        return bloodNeed;
    }

    public void setBloodNeed(String bloodNeed) {
        this.bloodNeed = bloodNeed;
    }

    public String getBloodgroup() {
        return bloodgroup;
    }

    public void setBloodgroup(String bloodgroup) {
        this.bloodgroup = bloodgroup;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBloodQty() {
        return bloodQty;
    }

    public void setBloodQty(String bloodQty) {
        this.bloodQty = bloodQty;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getManualDivision() {
        return manualDivision;
    }

    public void setManualDivision(String manualDivision) {
        this.manualDivision = manualDivision;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
