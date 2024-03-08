package com.saju.lifepluse.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.DoctorListModel;

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
        DataRetirve();



    }

    private void DataRetirve() {

        DoctorListModel selectedDoctor = getIntent().getParcelableExtra("selectedDoctor");

        String imgurl = selectedDoctor.getDoc_image();

        Glide.with(DoctorProfileview.this).load(imgurl).placeholder(R.drawable.avatar).into(doctorImageId);

        doctorQualificationId.setText(selectedDoctor.getDoc_qlEnglish());
        visitinghourId.setText("Friday: " + selectedDoctor.getFriday() + "\n" + "Saturday: " + selectedDoctor.getSaturday() + "\n" + "Sunday: " + selectedDoctor.getSunday() + "\n" + "Monday: " + selectedDoctor.getMonday() + "\n" + "Thuesday: " + selectedDoctor.getTuesday() + "\n" + "Wednesday: " + selectedDoctor.getWednesday() + "\n" + "Thursday: " + selectedDoctor.getThursday());
        // Update views with data
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");


        if (language.equals("bn")) {

            doctorNameId.setText("নাম: " + selectedDoctor.getDocnameBangla());
            doctorSpecialityId.setText("ডক্টর বিশেষত্ত: " + selectedDoctor.getDoc_spBangla());
            hospitalNameId.setText("বর্তমান কর্মরত: " + selectedDoctor.getDoc_hpnameBangla());
            hospitalContactId.setText("হসপিতালের ঠিকানা: " + selectedDoctor.getDoc_hpAddressBangla());
        } else {
            doctorNameId.setText("Name: " + selectedDoctor.getDocnameEnglish());
            doctorSpecialityId.setText("Doctor Speaciality: " + selectedDoctor.getDoc_spEnglish());
            hospitalNameId.setText("Present Work: " + selectedDoctor.getDoc_hpnameEnglish());
            hospitalContactId.setText("Hospital Address: " + selectedDoctor.getDoc_hpAddressEnglish());

        }


    }

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