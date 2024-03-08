package com.saju.lifepluse.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import com.saju.lifepluse.R;

public class SplashScreen extends AppCompatActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        preferences = this.getSharedPreferences("splash", MODE_PRIVATE);
        editor = preferences.edit();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (preferences.getBoolean("isMain", false)){
                    startActivity(new Intent(SplashScreen.this, DrawerLayout.class ));
                    finish();
                }else {
                    editor.putBoolean("isMain", true);
                    editor.apply();

                    TaskStackBuilder.create(SplashScreen.this)
                            .addNextIntentWithParentStack(new Intent(SplashScreen.this, DrawerLayout.class))
                            .addNextIntent(new Intent(SplashScreen.this, IntroActivity.class))
                            .startActivities();
                }

            }
        }, 4000);



    }
}