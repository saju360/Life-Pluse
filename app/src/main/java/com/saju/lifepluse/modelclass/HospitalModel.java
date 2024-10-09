package com.saju.lifepluse.modelclass;

public class HospitalModel {
    String hpname_eng;
    String hpname_bang;
    String hpmobile;
    String hp_address;
    String hp_fblink;
    String hp_websitelink;
    String hp_twitterlink;
    String hp_youtubelink;
    private String addedBy;


    public HospitalModel(String hpname_eng, String hpname_bang, String hpmobile, String hp_address, String hp_fblink, String hp_websitelink, String hp_twitterlink, String hp_youtubelink, String addedBy) {
        this.hpname_eng = hpname_eng;
        this.hpname_bang = hpname_bang;
        this.hpmobile = hpmobile;
        this.hp_address = hp_address;
        this.hp_fblink = hp_fblink;
        this.hp_websitelink = hp_websitelink;
        this.hp_twitterlink = hp_twitterlink;
        this.hp_youtubelink = hp_youtubelink;
        this.addedBy = addedBy;
    }

    public HospitalModel() {
    }

    public String getHpname_eng() {
        return hpname_eng;
    }

    public void setHpname_eng(String hpname_eng) {
        this.hpname_eng = hpname_eng;
    }

    public String getHpname_bang() {
        return hpname_bang;
    }

    public void setHpname_bang(String hpname_bang) {
        this.hpname_bang = hpname_bang;
    }

    public String getHpmobile() {
        return hpmobile;
    }

    public void setHpmobile(String hpmobile) {
        this.hpmobile = hpmobile;
    }

    public String getHp_address() {
        return hp_address;
    }

    public void setHp_address(String hp_address) {
        this.hp_address = hp_address;
    }

    public String getHp_fblink() {
        return hp_fblink;
    }

    public void setHp_fblink(String hp_fblink) {
        this.hp_fblink = hp_fblink;
    }

    public String getHp_websitelink() {
        return hp_websitelink;
    }

    public void setHp_websitelink(String hp_websitelink) {
        this.hp_websitelink = hp_websitelink;
    }

    public String getHp_twitterlink() {
        return hp_twitterlink;
    }

    public void setHp_twitterlink(String hp_twitterlink) {
        this.hp_twitterlink = hp_twitterlink;
    }

    public String getHp_youtubelink() {
        return hp_youtubelink;
    }

    public void setHp_youtubelink(String hp_youtubelink) {
        this.hp_youtubelink = hp_youtubelink;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
