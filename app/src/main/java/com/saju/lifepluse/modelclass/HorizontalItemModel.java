package com.saju.lifepluse.modelclass;

public class HorizontalItemModel {

    String catId, sp_bangla, sp_english;
    private int backgroundColor; // Add background color field
    private int textColor;
    private boolean isSelected;

    public HorizontalItemModel() {
    }

    public HorizontalItemModel(String catId, String sp_bangla, String sp_english) {
        this.catId = catId;
        this.sp_bangla = sp_bangla;
        this.sp_english = sp_english;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
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

    // Setter for isSelected
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Getter for isSelected
    public boolean isSelected() {
        return isSelected;
    }

    // Setter for background color
    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    // Getter for background color
    public int getBackgroundColor() {
        return backgroundColor;
    }

    // Setter for text color
    public void setTextColor(int color) {
        textColor = color;
    }

    // Getter for text color
    public int getTextColor() {
        return textColor;
    }
}
