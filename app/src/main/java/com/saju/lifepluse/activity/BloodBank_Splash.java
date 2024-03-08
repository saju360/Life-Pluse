package com.saju.lifepluse.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.saju.lifepluse.R;



public class BloodBank_Splash extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_blood_bank_splash);


        sharedPreferences = getSharedPreferences("bloodsplash", MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            // If it's the first time, show the splash screen and set isFirstTime to false
            editor = sharedPreferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), BloodBank.class));
                    finish();
                }
            }, 1000);
        } else {
            // If it's not the first time, directly start the BloodBank activity
            startActivity(new Intent(getApplicationContext(), BloodBank.class));
            finish();
        }
    }
}
