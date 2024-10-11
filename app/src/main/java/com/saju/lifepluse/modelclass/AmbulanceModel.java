package com.saju.lifepluse.modelclass;

public class AmbulanceModel {
    String ambname_eng;
    String ambname_bang;
    String ambmobile;
    String amb_address;
    String amb_fblink;
    String amb_websitelink;
    String amb_twitterlink;
    String amb_youtubelink;
    private String addedBy;

    public AmbulanceModel(String ambname_eng, String ambname_bang, String ambmobile, String amb_address, String amb_fblink, String amb_websitelink, String amb_twitterlink, String amb_youtubelink, String addedBy) {
        this.ambname_eng = ambname_eng;
        this.ambname_bang = ambname_bang;
        this.ambmobile = ambmobile;
        this.amb_address = amb_address;
        this.amb_fblink = amb_fblink;
        this.amb_websitelink = amb_websitelink;
        this.amb_twitterlink = amb_twitterlink;
        this.amb_youtubelink = amb_youtubelink;
        this.addedBy = addedBy;
    }

    public AmbulanceModel() {
    }

    public String getAmbname_eng() {
        return ambname_eng;
    }

    public void setAmbname_eng(String ambname_eng) {
        this.ambname_eng = ambname_eng;
    }

    public String getAmbname_bang() {
        return ambname_bang;
    }

    public void setAmbname_bang(String ambname_bang) {
        this.ambname_bang = ambname_bang;
    }

    public String getAmbmobile() {
        return ambmobile;
    }

    public void setAmbmobile(String ambmobile) {
        this.ambmobile = ambmobile;
    }

    public String getAmb_address() {
        return amb_address;
    }

    public void setAmb_address(String amb_address) {
        this.amb_address = amb_address;
    }

    public String getAmb_fblink() {
        return amb_fblink;
    }

    public void setAmb_fblink(String amb_fblink) {
        this.amb_fblink = amb_fblink;
    }

    public String getAmb_websitelink() {
        return amb_websitelink;
    }

    public void setAmb_websitelink(String amb_websitelink) {
        this.amb_websitelink = amb_websitelink;
    }

    public String getAmb_twitterlink() {
        return amb_twitterlink;
    }

    public void setAmb_twitterlink(String amb_twitterlink) {
        this.amb_twitterlink = amb_twitterlink;
    }

    public String getAmb_youtubelink() {
        return amb_youtubelink;
    }

    public void setAmb_youtubelink(String amb_youtubelink) {
        this.amb_youtubelink = amb_youtubelink;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
