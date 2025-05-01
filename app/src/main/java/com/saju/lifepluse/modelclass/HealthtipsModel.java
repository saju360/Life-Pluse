package com.saju.lifepluse.modelclass;

public class HealthtipsModel {

    String cover_imageurl;
    String tips_title;
    String tips_desc;


    public HealthtipsModel() {
    }

    public HealthtipsModel(String cover_imageurl, String tips_title, String tips_desc) {
        this.cover_imageurl = cover_imageurl;
        this.tips_title = tips_title;
        this.tips_desc = tips_desc;
    }

    public String getCover_imageurl() {
        return cover_imageurl;
    }

    public void setCover_imageurl(String cover_imageurl) {
        this.cover_imageurl = cover_imageurl;
    }

    public String getTips_title() {
        return tips_title;
    }

    public void setTips_title(String tips_title) {
        this.tips_title = tips_title;
    }

    public String getTips_desc() {
        return tips_desc;
    }

    public void setTips_desc(String tips_desc) {
        this.tips_desc = tips_desc;
    }
}
