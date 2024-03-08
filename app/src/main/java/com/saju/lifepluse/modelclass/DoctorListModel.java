package com.saju.lifepluse.modelclass;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class DoctorListModel implements Parcelable {

    String catId, doc_hpAddressBangla, doc_hpAddressEnglish, doc_hpnameBangla, doc_hpnameEnglish, doc_qlEnglish, doc_spBangla, doc_spEnglish, docnameBangla, docnameEnglish, doc_image;

    String friday, saturday, sunday, monday, tuesday, wednesday, thursday;

    public DoctorListModel() {
    }

    public DoctorListModel(String catId, String doc_hpAddressBangla, String doc_hpAddressEnglish, String doc_hpnameBangla, String doc_hpnameEnglish, String doc_qlEnglish, String doc_spBangla, String doc_spEnglish, String docnameBangla, String docnameEnglish, String doc_image, String friday, String saturday, String sunday, String monday, String tuesday, String wednesday, String thursday) {
        this.catId = catId;
        this.doc_hpAddressBangla = doc_hpAddressBangla;
        this.doc_hpAddressEnglish = doc_hpAddressEnglish;
        this.doc_hpnameBangla = doc_hpnameBangla;
        this.doc_hpnameEnglish = doc_hpnameEnglish;
        this.doc_qlEnglish = doc_qlEnglish;
        this.doc_spBangla = doc_spBangla;
        this.doc_spEnglish = doc_spEnglish;
        this.docnameBangla = docnameBangla;
        this.docnameEnglish = docnameEnglish;
        this.doc_image = doc_image;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
    }

    protected DoctorListModel(Parcel in) {
        catId = in.readString();
        doc_hpAddressBangla = in.readString();
        doc_hpAddressEnglish = in.readString();
        doc_hpnameBangla = in.readString();
        doc_hpnameEnglish = in.readString();
        doc_qlEnglish = in.readString();
        doc_spBangla = in.readString();
        doc_spEnglish = in.readString();
        docnameBangla = in.readString();
        docnameEnglish = in.readString();
        doc_image = in.readString();
        friday = in.readString();
        saturday = in.readString();
        sunday = in.readString();
        monday = in.readString();
        tuesday = in.readString();
        wednesday = in.readString();
        thursday = in.readString();
    }

    public static final Creator<DoctorListModel> CREATOR = new Creator<DoctorListModel>() {
        @Override
        public DoctorListModel createFromParcel(Parcel in) {
            return new DoctorListModel(in);
        }

        @Override
        public DoctorListModel[] newArray(int size) {
            return new DoctorListModel[size];
        }
    };

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getDoc_hpAddressBangla() {
        return doc_hpAddressBangla;
    }

    public void setDoc_hpAddressBangla(String doc_hpAddressBangla) {
        this.doc_hpAddressBangla = doc_hpAddressBangla;
    }

    public String getDoc_hpAddressEnglish() {
        return doc_hpAddressEnglish;
    }

    public void setDoc_hpAddressEnglish(String doc_hpAddressEnglish) {
        this.doc_hpAddressEnglish = doc_hpAddressEnglish;
    }

    public String getDoc_hpnameBangla() {
        return doc_hpnameBangla;
    }

    public void setDoc_hpnameBangla(String doc_hpnameBangla) {
        this.doc_hpnameBangla = doc_hpnameBangla;
    }

    public String getDoc_hpnameEnglish() {
        return doc_hpnameEnglish;
    }

    public void setDoc_hpnameEnglish(String doc_hpnameEnglish) {
        this.doc_hpnameEnglish = doc_hpnameEnglish;
    }

    public String getDoc_qlEnglish() {
        return doc_qlEnglish;
    }

    public void setDoc_qlEnglish(String doc_qlEnglish) {
        this.doc_qlEnglish = doc_qlEnglish;
    }

    public String getDoc_spBangla() {
        return doc_spBangla;
    }

    public void setDoc_spBangla(String doc_spBangla) {
        this.doc_spBangla = doc_spBangla;
    }

    public String getDoc_spEnglish() {
        return doc_spEnglish;
    }

    public void setDoc_spEnglish(String doc_spEnglish) {
        this.doc_spEnglish = doc_spEnglish;
    }

    public String getDocnameBangla() {
        return docnameBangla;
    }

    public void setDocnameBangla(String docnameBangla) {
        this.docnameBangla = docnameBangla;
    }

    public String getDocnameEnglish() {
        return docnameEnglish;
    }

    public void setDocnameEnglish(String docnameEnglish) {
        this.docnameEnglish = docnameEnglish;
    }

    public String getDoc_image() {
        return doc_image;
    }

    public void setDoc_image(String doc_image) {
        this.doc_image = doc_image;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public String getSaturday() {
        return saturday;
    }

    public void setSaturday(String saturday) {
        this.saturday = saturday;
    }

    public String getSunday() {
        return sunday;
    }

    public void setSunday(String sunday) {
        this.sunday = sunday;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getTuesday() {
        return tuesday;
    }

    public void setTuesday(String tuesday) {
        this.tuesday = tuesday;
    }

    public String getWednesday() {
        return wednesday;
    }

    public void setWednesday(String wednesday) {
        this.wednesday = wednesday;
    }

    public String getThursday() {
        return thursday;
    }

    public void setThursday(String thursday) {
        this.thursday = thursday;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(catId);
        dest.writeString(doc_hpAddressBangla);
        dest.writeString(doc_hpAddressEnglish);
        dest.writeString(doc_hpnameBangla);
        dest.writeString(doc_hpnameEnglish);
        dest.writeString(doc_qlEnglish);
        dest.writeString(doc_spBangla);
        dest.writeString(doc_spEnglish);
        dest.writeString(docnameBangla);
        dest.writeString(docnameEnglish);
        dest.writeString(doc_image);
        dest.writeString(friday);
        dest.writeString(saturday);
        dest.writeString(sunday);
        dest.writeString(monday);
        dest.writeString(tuesday);
        dest.writeString(wednesday);
        dest.writeString(thursday);
    }
}
