package com.saju.lifepluse.modelclass;

public class DoctorSpecialityModel {

    String catId, sp_image, sp_bangla, sp_english;

    public DoctorSpecialityModel() {
    }

    public DoctorSpecialityModel(String catId, String sp_image, String sp_bangla, String sp_bangla1) {
        this.catId = catId;
        this.sp_image = sp_image;
        this.sp_bangla = sp_bangla;
        this.sp_english = sp_english;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getSp_image() {
        return sp_image;
    }

    public void setSp_image(String sp_image) {
        this.sp_image = sp_image;
    }

    public String getSp_bangla() {
        return sp_bangla;
    }

    public void setSp_bangla(String sp_bangla) {
        this.sp_bangla = sp_bangla;
    }

    public String getSp_english() {
        return sp_english;
    }

    public void setSp_english(String sp_english) {
        this.sp_english = sp_english;
    }
}
