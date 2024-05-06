package com.saju.lifepluse.modelclass;

import com.google.firebase.Timestamp;

public class BloodOrganizationAddModel {

    String uid;
    String orgname_Ed, orgaddress_Ed, orgphone_Ed, orgemail_Ed, orgfb_Ed, orginsta_Ed, orgyout_Ed, orgweb_Ed;
    Timestamp addedtime;


    public BloodOrganizationAddModel() {
    }

    public BloodOrganizationAddModel(String uid, String orgname_Ed, String orgaddress_Ed, String orgphone_Ed, String orgemail_Ed, String orgfb_Ed, String orginsta_Ed, String orgyout_Ed, String orgweb_Ed, Timestamp addedtime) {
        this.uid = uid;
        this.orgname_Ed = orgname_Ed;
        this.orgaddress_Ed = orgaddress_Ed;
        this.orgphone_Ed = orgphone_Ed;
        this.orgemail_Ed = orgemail_Ed;
        this.orgfb_Ed = orgfb_Ed;
        this.orginsta_Ed = orginsta_Ed;
        this.orgyout_Ed = orgyout_Ed;
        this.orgweb_Ed = orgweb_Ed;
        this.addedtime = addedtime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrgname_Ed() {
        return orgname_Ed;
    }

    public void setOrgname_Ed(String orgname_Ed) {
        this.orgname_Ed = orgname_Ed;
    }

    public String getOrgaddress_Ed() {
        return orgaddress_Ed;
    }

    public void setOrgaddress_Ed(String orgaddress_Ed) {
        this.orgaddress_Ed = orgaddress_Ed;
    }

    public String getOrgphone_Ed() {
        return orgphone_Ed;
    }

    public void setOrgphone_Ed(String orgphone_Ed) {
        this.orgphone_Ed = orgphone_Ed;
    }

    public String getOrgemail_Ed() {
        return orgemail_Ed;
    }

    public void setOrgemail_Ed(String orgemail_Ed) {
        this.orgemail_Ed = orgemail_Ed;
    }

    public String getOrgfb_Ed() {
        return orgfb_Ed;
    }

    public void setOrgfb_Ed(String orgfb_Ed) {
        this.orgfb_Ed = orgfb_Ed;
    }

    public String getOrginsta_Ed() {
        return orginsta_Ed;
    }

    public void setOrginsta_Ed(String orginsta_Ed) {
        this.orginsta_Ed = orginsta_Ed;
    }

    public String getOrgyout_Ed() {
        return orgyout_Ed;
    }

    public void setOrgyout_Ed(String orgyout_Ed) {
        this.orgyout_Ed = orgyout_Ed;
    }

    public String getOrgweb_Ed() {
        return orgweb_Ed;
    }

    public void setOrgweb_Ed(String orgweb_Ed) {
        this.orgweb_Ed = orgweb_Ed;
    }

    public Timestamp getAddedtime() {
        return addedtime;
    }

    public void setAddedtime(Timestamp addedtime) {
        this.addedtime = addedtime;
    }
}
