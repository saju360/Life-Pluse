package com.saju.lifepluse.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.saju.lifepluse.R;

public class Blood_Organization_Post extends AppCompatActivity {


    EditText orgname_Ed, orgaddress_Ed, orgphone_Ed, orgemail_Ed, orgfb_Ed, orginsta_Ed, orgyout_Ed, orgweb_Ed;
    Button orgcancelButton, orgAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_organization_post);
        statusbarcolor();
        initial();


        orgAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validatycheck();

            }
        });


    }

    private void validatycheck() {
        String orgname = orgname_Ed.getText().toString();
        String orgaddress = orgaddress_Ed.getText().toString();
        String orgphone = orgphone_Ed.getText().toString();
        String orgemail = orgemail_Ed.getText().toString();
        String orgfblink = orgfb_Ed.getText().toString();
        String orginstalink = orginsta_Ed.getText().toString();
        String orgyoutlink = orgyout_Ed.getText().toString();
        String orgweblink = orgweb_Ed.getText().toString();

        if (TextUtils.isEmpty(orgname) || TextUtils.isEmpty(orgaddress) || TextUtils.isEmpty(orgphone) ||
                TextUtils.isEmpty(orgemail) || (TextUtils.isEmpty(orgfblink) && TextUtils.isEmpty(orginstalink) &&
                TextUtils.isEmpty(orgyoutlink) && TextUtils.isEmpty(orgweblink))) {
            Toast.makeText(getApplicationContext(), "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Function to validate URLs
        boolean isValidURL = true;

        // Facebook link validation
        if (!TextUtils.isEmpty(orgfblink)) {
            if (!(orgfblink.startsWith("https://www.facebook.com") || orgfblink.startsWith("www.facebook.com") || orgfblink.startsWith("https://facebook.com"))) {
                isValidURL = false;
                Toast.makeText(getApplicationContext(), "Facebook link should start with 'https://www.facebook.com', 'www.facebook.com', or 'https://facebook.com'", Toast.LENGTH_SHORT).show();
            }
        }

        // Instagram link validation
        if (!TextUtils.isEmpty(orginstalink)) {
            if (!(orginstalink.startsWith("https://www.instagram.com") || orginstalink.startsWith("www.instagram.com") || orginstalink.startsWith("https://instagram.com"))) {
                isValidURL = false;
                Toast.makeText(getApplicationContext(), "Instagram link should start with 'https://www.instagram.com', 'www.instagram.com', or 'https://instagram.com'", Toast.LENGTH_SHORT).show();
            }
        }

        // YouTube link validation
        if (!TextUtils.isEmpty(orgyoutlink)) {
            if (!(orgyoutlink.startsWith("https://www.youtube.com") || orgyoutlink.startsWith("www.youtube.com") || orgyoutlink.startsWith("https://youtube.com"))) {
                isValidURL = false;
                Toast.makeText(getApplicationContext(), "YouTube link should start with 'https://www.youtube.com', 'www.youtube.com', or 'https://youtube.com'", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isValidURL) {
            return;
        }

        // Proceed with further actions if all validations pass
    }



    private void initial() {

        orgname_Ed = findViewById(R.id.orgname_Ed);
        orgaddress_Ed = findViewById(R.id.orgaddress_Ed);
        orgphone_Ed = findViewById(R.id.orgphone_Ed);
        orgemail_Ed = findViewById(R.id.orgemail_Ed);
        orgfb_Ed = findViewById(R.id.orgfb_Ed);
        orginsta_Ed = findViewById(R.id.orginsta_Ed);
        orgyout_Ed = findViewById(R.id.orgyout_Ed);
        orgweb_Ed = findViewById(R.id.orgweb_Ed);
        orgcancelButton = findViewById(R.id.org_cancelButton);
        orgAddButton = findViewById(R.id.org_AddButton);


    }


    private void statusbarcolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }
    }
}