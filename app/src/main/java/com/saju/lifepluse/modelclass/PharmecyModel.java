package com.saju.lifepluse.modelclass;

public class PharmecyModel {
    String phname_eng;
    String phname_bang;
    String phregno;
    String phmobile;
    String ph_address;
    String ph_fblink;
    String ph_websitelink;
    String ph_twitterlink;
    String ph_youtubelink;
    private String addedBy;


    public PharmecyModel(String phname_eng, String phname_bang, String phregno, String phmobile, String ph_address, String ph_fblink, String ph_websitelink, String ph_twitterlink, String ph_youtubelink, String addedBy) {
        this.phname_eng = phname_eng;
        this.phname_bang = phname_bang;
        this.phregno = phregno;
        this.phmobile = phmobile;
        this.ph_address = ph_address;
        this.ph_fblink = ph_fblink;
        this.ph_websitelink = ph_websitelink;
        this.ph_twitterlink = ph_twitterlink;
        this.ph_youtubelink = ph_youtubelink;
        this.addedBy = addedBy;
    }

    public PharmecyModel() {
    }

    public String getPhname_eng() {
        return phname_eng;
    }

    public void setPhname_eng(String phname_eng) {
        this.phname_eng = phname_eng;
    }

    public String getPhname_bang() {
        return phname_bang;
    }

    public void setPhname_bang(String phname_bang) {
        this.phname_bang = phname_bang;
    }

    public String getPhregno() {
        return phregno;
    }

    public void setPhregno(String phregno) {
        this.phregno = phregno;
    }

    public String getPhmobile() {
        return phmobile;
    }

    public void setPhmobile(String phmobile) {
        this.phmobile = phmobile;
    }

    public String getPh_address() {
        return ph_address;
    }

    public void setPh_address(String ph_address) {
        this.ph_address = ph_address;
    }

    public String getPh_fblink() {
        return ph_fblink;
    }

    public void setPh_fblink(String ph_fblink) {
        this.ph_fblink = ph_fblink;
    }

    public String getPh_websitelink() {
        return ph_websitelink;
    }

    public void setPh_websitelink(String ph_websitelink) {
        this.ph_websitelink = ph_websitelink;
    }

    public String getPh_twitterlink() {
        return ph_twitterlink;
    }

    public void setPh_twitterlink(String ph_twitterlink) {
        this.ph_twitterlink = ph_twitterlink;
    }

    public String getPh_youtubelink() {
        return ph_youtubelink;
    }

    public void setPh_youtubelink(String ph_youtubelink) {
        this.ph_youtubelink = ph_youtubelink;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
