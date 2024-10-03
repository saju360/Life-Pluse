package com.saju.lifepluse.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.DoctorListModel;
import com.saju.lifepluse.modelclass.HospitalwiseDoctorListModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorProfileview extends AppCompatActivity {

    MaterialToolbar materialToolbar;
    CircleImageView doctorImageId;
    TextView doctorNameId, doctorSpecialityId, doctorQualificationId, hospitalNameId, hospitalContactId, visitinghourId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profileview);

        initId();
        setupToolbar();
        DataRetrieve();



    }

    private void DataRetrieve() {
        String doctorType = getIntent().getStringExtra("selectedDoctorType");

        if ("Hospitalwise".equals(doctorType)) {
            HospitalwiseDoctorListModel selectedDoctor = getIntent().getParcelableExtra("selectedDoctor");

            if (selectedDoctor != null) {
                String imgurl = selectedDoctor.getDoc_image();
                Glide.with(DoctorProfileview.this).load(imgurl).placeholder(R.drawable.avatar).into(doctorImageId);

                doctorQualificationId.setText(selectedDoctor.getDoc_qlEnglish());
                visitinghourId.setText(
                        "Friday: " + (selectedDoctor.getFriday() != null ? selectedDoctor.getFriday() : "N/A") + "\n" +
                                "Saturday: " + (selectedDoctor.getSaturday() != null ? selectedDoctor.getSaturday() : "N/A") + "\n" +
                                "Sunday: " + (selectedDoctor.getSunday() != null ? selectedDoctor.getSunday() : "N/A") + "\n" +
                                "Monday: " + (selectedDoctor.getMonday() != null ? selectedDoctor.getMonday() : "N/A") + "\n" +
                                "Tuesday: " + (selectedDoctor.getTuesday() != null ? selectedDoctor.getTuesday() : "N/A") + "\n" +
                                "Wednesday: " + (selectedDoctor.getWednesday() != null ? selectedDoctor.getWednesday() : "N/A") + "\n" +
                                "Thursday: " + (selectedDoctor.getThursday() != null ? selectedDoctor.getThursday() : "N/A")
                );

                SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                String language = prefs.getString("My_Lang", "");

                if ("bn".equals(language)) {
                    doctorNameId.setText("নাম: " + (selectedDoctor.getDocnameBangla() != null ? selectedDoctor.getDocnameBangla() : "N/A"));
                    doctorSpecialityId.setText("ডক্টর বিশেষত্ত: " + (selectedDoctor.getDoc_spBangla() != null ? selectedDoctor.getDoc_spBangla() : "N/A"));
                    hospitalNameId.setText("বর্তমান কর্মরত: " + (selectedDoctor.getDoc_hpnameBangla() != null ? selectedDoctor.getDoc_hpnameBangla() : "N/A"));
                    hospitalContactId.setText("হসপিতালের ঠিকানা: " + (selectedDoctor.getDoc_hpAddressBangla() != null ? selectedDoctor.getDoc_hpAddressBangla() : "N/A"));
                } else {
                    doctorNameId.setText("Name: " + (selectedDoctor.getDocnameEnglish() != null ? selectedDoctor.getDocnameEnglish() : "N/A"));
                    doctorSpecialityId.setText("Doctor Speciality: " + (selectedDoctor.getDoc_spEnglish() != null ? selectedDoctor.getDoc_spEnglish() : "N/A"));
                    hospitalNameId.setText("Present Work: " + (selectedDoctor.getDoc_hpnameEnglish() != null ? selectedDoctor.getDoc_hpnameEnglish() : "N/A"));
                    hospitalContactId.setText("Hospital Address: " + (selectedDoctor.getDoc_hpAddressEnglish() != null ? selectedDoctor.getDoc_hpAddressEnglish() : "N/A"));
                }
            }
        } else if ("DoctorList".equals(doctorType)) {
            DoctorListModel selectedDoctor = getIntent().getParcelableExtra("selectedDoctor");

            if (selectedDoctor != null) {
                String imgurl = selectedDoctor.getDoc_image();
                Glide.with(DoctorProfileview.this).load(imgurl).placeholder(R.drawable.avatar).into(doctorImageId);

                doctorQualificationId.setText(selectedDoctor.getDoc_qlEnglish());
                visitinghourId.setText(
                        "Friday: " + (selectedDoctor.getFriday() != null ? selectedDoctor.getFriday() : "N/A") + "\n" +
                                "Saturday: " + (selectedDoctor.getSaturday() != null ? selectedDoctor.getSaturday() : "N/A") + "\n" +
                                "Sunday: " + (selectedDoctor.getSunday() != null ? selectedDoctor.getSunday() : "N/A") + "\n" +
                                "Monday: " + (selectedDoctor.getMonday() != null ? selectedDoctor.getMonday() : "N/A") + "\n" +
                                "Tuesday: " + (selectedDoctor.getTuesday() != null ? selectedDoctor.getTuesday() : "N/A") + "\n" +
                                "Wednesday: " + (selectedDoctor.getWednesday() != null ? selectedDoctor.getWednesday() : "N/A") + "\n" +
                                "Thursday: " + (selectedDoctor.getThursday() != null ? selectedDoctor.getThursday() : "N/A")
                );

                SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                String language = prefs.getString("My_Lang", "");

                if ("bn".equals(language)) {
                    doctorNameId.setText("নাম: " + (selectedDoctor.getDocnameBangla() != null ? selectedDoctor.getDocnameBangla() : "N/A"));
                    doctorSpecialityId.setText("ডক্টর বিশেষত্ত: " + (selectedDoctor.getDoc_spBangla() != null ? selectedDoctor.getDoc_spBangla() : "N/A"));
                    hospitalNameId.setText("বর্তমান কর্মরত: " + (selectedDoctor.getDoc_hpnameBangla() != null ? selectedDoctor.getDoc_hpnameBangla() : "N/A"));
                    hospitalContactId.setText("হসপিতালের ঠিকানা: " + (selectedDoctor.getDoc_hpAddressBangla() != null ? selectedDoctor.getDoc_hpAddressBangla() : "N/A"));
                } else {
                    doctorNameId.setText("Name: " + (selectedDoctor.getDocnameEnglish() != null ? selectedDoctor.getDocnameEnglish() : "N/A"));
                    doctorSpecialityId.setText("Doctor Speciality: " + (selectedDoctor.getDoc_spEnglish() != null ? selectedDoctor.getDoc_spEnglish() : "N/A"));
                    hospitalNameId.setText("Present Work: " + (selectedDoctor.getDoc_hpnameEnglish() != null ? selectedDoctor.getDoc_hpnameEnglish() : "N/A"));
                    hospitalContactId.setText("Hospital Address: " + (selectedDoctor.getDoc_hpAddressEnglish() != null ? selectedDoctor.getDoc_hpAddressEnglish() : "N/A"));
                }
            }
        }}



    private void initId() {
        materialToolbar = findViewById(R.id.toolbarId);
        doctorImageId = findViewById(R.id.doctorImageId);
        doctorNameId = findViewById(R.id.doctorNameId);
        doctorSpecialityId = findViewById(R.id.doctorSpecialityId);
        doctorQualificationId = findViewById(R.id.doctorQualificationId);
        hospitalNameId = findViewById(R.id.hospitalNameId);
        hospitalContactId = findViewById(R.id.hospitalContactId);
        visitinghourId = findViewById(R.id.visitinghourId);


    }

    private void setupToolbar() {
        setSupportActionBar(materialToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
            getSupportActionBar().setTitle("Doctor Profile");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}