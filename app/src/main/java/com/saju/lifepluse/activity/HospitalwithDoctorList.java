package com.saju.lifepluse.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.HospitalwiseDoctorListAdapter;
import com.saju.lifepluse.modelclass.HospitalwiseDoctorListModel;

import java.util.ArrayList;
import java.util.List;

public class HospitalwithDoctorList extends AppCompatActivity {

    private MaterialToolbar materialToolbar;
    private TextView hospitalName, hospitaladdress, hospitalmobile;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerViewId;
    private ImageView hospitalLogo, hospitalfb, weblink_btn, twitter_btn, youtube_btn;

    private HospitalwiseDoctorListAdapter hospitalwithDoctorListAdapter;  // Correct adapter type
    private FirebaseFirestore db;
    private ArrayList<HospitalwiseDoctorListModel> hospitalwiseDoctorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitalwith_doctor_list);

        // Initialize Views
        hospitalName = findViewById(R.id.hospitalName);
        hospitaladdress = findViewById(R.id.hospitaladdress);
        hospitalmobile = findViewById(R.id.hospitalmobile);
        hospitalLogo = findViewById(R.id.hospital_logo);
        hospitalfb = findViewById(R.id.hospitalfb);
        weblink_btn = findViewById(R.id.weblink_btn);
        twitter_btn = findViewById(R.id.twitter_btn);
        youtube_btn = findViewById(R.id.youtube_btn);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewId = findViewById(R.id.recyclearViewId);
        materialToolbar = findViewById(R.id.toolbarId);
        db = FirebaseFirestore.getInstance();
        hospitalwiseDoctorList = new ArrayList<>();

        retrieveDataFromIntent();
        setupToolbar();
        setupRecyclerView();
        doctorDataRetrieve();

        // Swipe to Refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            doctorDataRetrieve();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    // Add a method to filter doctors by hospital name
    private void doctorDataRetrieve() {
        db.collectionGroup("Doctor_list").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e == null) {
                    hospitalwiseDoctorList = new ArrayList<>();

                    List<HospitalwiseDoctorListModel> data = queryDocumentSnapshots.toObjects(HospitalwiseDoctorListModel.class);
                    hospitalwiseDoctorList.addAll(data);

                    // Get selected hospital name based on current language setting
                    String selectedHospitalName = getSelectedHospitalName();

                    // Filter the list by selected hospital name
                    ArrayList<HospitalwiseDoctorListModel> filteredList = filterDoctorListByHospitalName(hospitalwiseDoctorList, selectedHospitalName);

                    // Initialize adapter with the filtered list
                    HospitalwiseDoctorListAdapter adapter = new HospitalwiseDoctorListAdapter(HospitalwithDoctorList.this, filteredList);
                    recyclerViewId.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerViewId.setAdapter(adapter);

                } else {
                    Log.d("data", e.getLocalizedMessage());
                }
            }
        });
    }

    // Method to retrieve the selected hospital name based on the current language
    private String getSelectedHospitalName() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");

        // Check current language and use the appropriate hospital name
        if ("bn".equals(language)) {
            return getIntent().getStringExtra("hospitalnameBangla");  // Bengali name
        } else {
            return getIntent().getStringExtra("hospitalnameEnglish");  // English name
        }
    }

    private ArrayList<HospitalwiseDoctorListModel> filterDoctorListByHospitalName(ArrayList<HospitalwiseDoctorListModel> doctorList, String hospitalName) {
        ArrayList<HospitalwiseDoctorListModel> filteredList = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");

        for (HospitalwiseDoctorListModel doctor : doctorList) {
            // Check for null values before comparing strings
            if ("bn".equals(language)) {
                String doctorHospitalNameBangla = doctor.getDoc_hpnameBangla();
                if (doctorHospitalNameBangla != null && hospitalName != null && doctorHospitalNameBangla.equals(hospitalName)) {
                    filteredList.add(doctor);
                }
            } else {
                String doctorHospitalNameEnglish = doctor.getDoc_hpnameEnglish();
                if (doctorHospitalNameEnglish != null && hospitalName != null && doctorHospitalNameEnglish.equals(hospitalName)) {
                    filteredList.add(doctor);
                }
            }

            Log.d("HospitalFilter", "Filtering by hospital name: " + hospitalName);
            Log.d("HospitalFilter", "Doctor hospital name (Bangla): " + doctor.getDoc_hpnameBangla());
            Log.d("HospitalFilter", "Doctor hospital name (English): " + doctor.getDoc_hpnameEnglish());

        }
        return filteredList;
    }



    private void setupToolbar() {
        // Set up the toolbar
        materialToolbar.setTitle("Hospital Profile");
        setSupportActionBar(materialToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        }
    }

    private void setupRecyclerView() {
        // Set up the RecyclerView
        recyclerViewId.setLayoutManager(new LinearLayoutManager(this));
        hospitalwithDoctorListAdapter = new HospitalwiseDoctorListAdapter(this, hospitalwiseDoctorList);
        recyclerViewId.setAdapter(hospitalwithDoctorListAdapter);
    }

    private void retrieveDataFromIntent() {
        // Retrieve data from Intent
        String hospitalNameEnglish = getIntent().getStringExtra("hospitalnameEnglish");
        String hospitalNameBangla = getIntent().getStringExtra("hospitalnameBangla");
        String hpaddress = getIntent().getStringExtra("hospitaladdress");
        String hpmobile = getIntent().getStringExtra("hospitalmobile");
        String hpfblink = getIntent().getStringExtra("hospitalfblink");
        String hpweblink = getIntent().getStringExtra("hospitalweblink");
        String hpttwitterlink = getIntent().getStringExtra("hospitaltwitterlink");
        String hptyoutubelink = getIntent().getStringExtra("hospitalyoutubelink");
        //String hospitalLogoUrl = getIntent().getStringExtra("hospitallogo");


        Log.d("hospital", hospitalNameEnglish + hpaddress);

        // Load hospital logo with Glide
        /*if (hospitalLogoUrl != null) {
            Glide.with(this)
                    .load(hospitalLogoUrl)
                    .placeholder(R.drawable.baseline_add_photo_alternate)
                    .into(hospitalLogo);
        }*/


        hospitalfb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hpfblink !=null && !hpfblink.isEmpty()){
                    Log.d("hpfblink", hpfblink);
                    openLinkInAppOrBrowser(hpfblink);
                }else {
                    Toast.makeText(getApplicationContext(), "Not Have Facebook Account", Toast.LENGTH_SHORT).show();
                }
            }
        });

        weblink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hpweblink !=null && !hpweblink.isEmpty()){
                    openLinkInAppOrBrowser(hpweblink);
                }else {
                    Toast.makeText(getApplicationContext(), "Not Have Website", Toast.LENGTH_SHORT).show();
                }
            }
        });
        twitter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hpttwitterlink !=null && !hpttwitterlink.isEmpty()){
                    openLinkInAppOrBrowser(hpttwitterlink);
                }else {
                    Toast.makeText(getApplicationContext(), "Not Have Twitter Account", Toast.LENGTH_SHORT).show();
                }
            }
        });

        youtube_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hptyoutubelink !=null && !hptyoutubelink.isEmpty()){
                    openLinkInAppOrBrowser(hptyoutubelink);
                }else {
                    Toast.makeText(getApplicationContext(), "Not Have Youtube Channel", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Set hospital name based on language preference
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if ("bn".equals(language) && hospitalNameBangla != null && hpaddress !=null && hpmobile !=null && hpfblink !=null) {
            hospitalName.setText(hospitalNameBangla);
            hospitaladdress.setText(hpaddress);
            hospitalmobile.setText(hpmobile);

        } else if (hospitalNameEnglish != null) {
            hospitalName.setText(hospitalNameEnglish);
            hospitaladdress.setText(hpaddress);
            hospitalmobile.setText(hpmobile);
        }




    }

    // Method to open a link in a web browser
    private void openLinkInBrowser(String url) {
        url = ensureValidUrl(url);
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line if using ApplicationContext
        try {
            startActivity(intent); // Use `this` or `HospitalwithDoctorList.this` for Activity context
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getApplicationContext(), "No application can handle this request. Please install a web browser.", Toast.LENGTH_LONG).show();
            Log.e("LinkError", "ActivityNotFoundException: " + e.getMessage());
        }
    }



    private String ensureValidUrl(String url) {
        if (url != null && !(url.startsWith("http://") || url.startsWith("https://"))) {
            return "http://" + url; // Prepend default scheme if missing
        }
        return url;
    }


    // Method to open a link in the corresponding app if installed, or fallback to the web browser
    private void openLinkInAppOrBrowser(String url) {
        url = ensureValidUrl(url);
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line if using ApplicationContext
        PackageManager packageManager = getPackageManager(); // Use `this` for Activity context
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (activities.size() > 0) {
            try {
                startActivity(intent); // Use `this` or `HospitalwithDoctorList.this`
            } catch (ActivityNotFoundException e) {
                Toast.makeText(getApplicationContext(), "No application can handle this request.", Toast.LENGTH_LONG).show();
                Log.e("LinkError", "ActivityNotFoundException: " + e.getMessage());
            }
        } else {
            openLinkInBrowser(url); // Fallback to browser
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();  // Finish activity on back button click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
