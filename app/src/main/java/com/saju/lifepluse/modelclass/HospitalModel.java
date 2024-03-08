package com.saju.lifepluse.modelclass;

public class HospitalModel {

    String hp_catId, hp_image, hp_english, hp_bangla;

    public HospitalModel() {
    }

    public HospitalModel(String hp_catId, String hp_image, String hp_english, String hp_bangla) {
        this.hp_catId = hp_catId;
        this.hp_image = hp_image;
        this.hp_english = hp_english;
        this.hp_bangla = hp_bangla;
    }

    public String getHp_catId() {
        return hp_catId;
    }

    public void setHp_catId(String hp_catId) {
        this.hp_catId = hp_catId;
    }

    public String getHp_image() {
        return hp_image;
    }

    public void setHp_image(String hp_image) {
        this.hp_image = hp_image;
    }

    public String getHp_english() {
        return hp_english;
    }

    public void setHp_english(String hp_english) {
        this.hp_english = hp_english;
    }

    public String getHp_bangla() {
        return hp_bangla;
    }

    public void setHp_bangla(String hp_bangla) {
        this.hp_bangla = hp_bangla;
    }
}
