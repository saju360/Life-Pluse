package com.saju.lifepluse.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.fragment.BloodNeed_Fragment;
import com.saju.lifepluse.fragment.Blood_Home_Fragment;
import com.saju.lifepluse.fragment.Profile_Fragment;

public class BloodBank extends AppCompatActivity {

    MeowBottomNavigation meowBottomNavigation;
    FirebaseFirestore db;
    private static final String PREF_TOTAL_COUNT = "pref_total_count";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private long previousCount = 0;
    private long currentvalue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_bank);

        sharedPreferences = getSharedPreferences("bloodcount", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        previousCount = sharedPreferences.getLong(PREF_TOTAL_COUNT, 0);

        Log.d("previouscount", String.valueOf(previousCount));

        meowBottomNavigation = findViewById(R.id.bottomNav);
        db = FirebaseFirestore.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }

        bloodNeedDataCountRetrive();

        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.baseline_profile));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.blood_bg_icon));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.notification_icon));

        // Initialize count to 0 initially
        meowBottomNavigation.setCount(3, "");

        meowBottomNavigation.setOnShowListener(item -> {
            Fragment fragment = null;
            switch (item.getId()) {
                case 1:
                    fragment = new Profile_Fragment();
                    break;
                case 2:
                    fragment = new Blood_Home_Fragment();
                    break;
                case 3:
                    fragment = new BloodNeed_Fragment();
                    break;
            }
            loadFragmentManager(fragment);
        });

        meowBottomNavigation.show(2, true);

        meowBottomNavigation.setOnClickMenuListener(item -> {
            // Handle bottom navigation item click
            if (item.getId() == 3) {
                // Retrieve the current count
                bloodNeedDataCountRetrive();
            }
        });

        meowBottomNavigation.setOnReselectListener(item -> {
            // Handle bottom navigation item reselection
        });
    }

    private void bloodNeedDataCountRetrive() {
        db.collection("Blood Need Post")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the query snapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            // Get the number of documents
                            long totalCount = querySnapshot.size();
                            // Retrieve the previous count from SharedPreferences
                            long previousCount = sharedPreferences.getLong(PREF_TOTAL_COUNT, 0);
                            if (totalCount > previousCount) {
                                // Update the stored count in SharedPreferences
                                editor.putLong(PREF_TOTAL_COUNT, totalCount).apply();
                                // Update the count in MeowBottomNavigation
                                currentvalue = totalCount-previousCount;

                                meowBottomNavigation.setCount(3, String.valueOf(currentvalue));
                            }
                            Log.d("Firestore", "Total number of documents: " + totalCount);
                        }
                    } else {
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void loadFragmentManager(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.framelayout, fragment)
                .commit();
    }

}
