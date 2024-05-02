package com.saju.lifepluse.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.saju.lifepluse.R;
import com.saju.lifepluse.utils.FirebaseUtil;

public class Blood_Organization_Home extends AppCompatActivity {

    TextView locationTextView;
    LottieAnimationView addorganizationAnimationId;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView bloodRecyclearId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_organization_home);
        locationTextView = findViewById(R.id.locationTextView);
        addorganizationAnimationId = findViewById(R.id.addorganizationAnimationId);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        bloodRecyclearId = findViewById(R.id.bloodRecyclearId);

        locationRetrive();
        statusbarcolor();




        addorganizationAnimationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(), Blood_Organization_Post.class));


            }
        });


    }


    public void locationRetrive(){
        if (DrawerLayout.locationText != null) {
            locationTextView.setText(DrawerLayout.locationText);
        } else {
            locationTextView.setText("Location not available");
            // Handle the case where locationText is null
        }
    }

    private void statusbarcolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }
    }
}