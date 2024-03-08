package com.saju.lifepluse;

public class CategoryModel {


    String catId, image, nameEnglish, nameBangla;

    public CategoryModel() {
    }

    public CategoryModel(String catId, String image, String nameEnglish, String nameBangla) {
        this.catId = catId;
        this.image = image;
        this.nameEnglish = nameEnglish;
        this.nameBangla = nameBangla;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNameEnglish() {
        return nameEnglish;
    }

    public void setNameEnglish(String nameEnglish) {
        this.nameEnglish = nameEnglish;
    }

    public String getNameBangla() {
        return nameBangla;
    }

    public void setNameBangla(String nameBangla) {
        this.nameBangla = nameBangla;
    }
}
