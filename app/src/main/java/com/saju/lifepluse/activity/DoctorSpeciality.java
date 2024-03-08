package com.saju.lifepluse.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.DoctorSpAdapter;
import com.saju.lifepluse.modelclass.DoctorSpecialityModel;

import java.util.ArrayList;
import java.util.List;

public class DoctorSpeciality extends AppCompatActivity {

    MaterialToolbar materialToolbar;
    GridView doctorGridview;
    private FirebaseFirestore db;
    DoctorSpAdapter adapter;
    EditText searchEditText;
    private ArrayList<DoctorSpecialityModel> doctorspecialityDataList;
    private ArrayList<DoctorSpecialityModel> originalDataList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_speciality);

        materialToolbar = findViewById(R.id.toolbarId);
        doctorGridview = findViewById(R.id.doctorGridviewId);
        searchEditText = findViewById(R.id.searchEditText);
        db = FirebaseFirestore.getInstance();
        doctorspecialityDataList = new ArrayList<>();


        dataRetrive();

        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);



        setSupportActionBar(materialToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        }

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Perform the search operation here
                    String query = searchEditText.getText().toString();
                    filterData(query);
                    return true;
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            // Your existing TextWatcher implementation

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Filter the data based on the search query
                filterData(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

        });





        String doctorcatId = getIntent().getStringExtra("catId");


        doctorGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String catIdDS = doctorspecialityDataList.get(position).getCatId();

                Intent myIntent = new Intent(getApplicationContext(), DoctorList.class);
                myIntent.putExtra("doctorlistcatId", catIdDS);
                myIntent.putExtra("doctorcatId", doctorcatId);
                startActivity(myIntent);
            }
        });

        String catNameEnglish = getIntent().getStringExtra("catEnglishName");
        String catNameBangla = getIntent().getStringExtra("catBanglaName");


        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (language.equals("bn")) {
            materialToolbar.setTitle(catNameBangla);
        } else {
            materialToolbar.setTitle(catNameEnglish);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the status bar color programmatically
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));


        }
    }

    private void dataRetrive() {
        String catId = getIntent().getStringExtra("catId");

        doctorspecialityDataList.clear();

        db.collection("Category").document(catId).collection("Doctor_Speciality").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    doctorspecialityDataList.clear();
                    List<DoctorSpecialityModel> data = value.toObjects(DoctorSpecialityModel.class);
                    doctorspecialityDataList.addAll(data);

                    // Ensure originalDataList is initialized here
                    originalDataList = new ArrayList<>(doctorspecialityDataList);

                    adapter = new DoctorSpAdapter(getApplicationContext(), doctorspecialityDataList);
                    doctorGridview.setAdapter(adapter);
                }
            }
        });
    }




    private void filterData(String query) {
        ArrayList<DoctorSpecialityModel> filteredList = new ArrayList<>();

        for (DoctorSpecialityModel model : originalDataList) {
            String categoryName;
            if (isBanglaLanguage()) {
                categoryName = model.getSp_bangla().toLowerCase();
            } else {
                categoryName = model.getSp_english().toLowerCase();
            }

            if (categoryName.contains(query.toLowerCase())) {
                filteredList.add(model);
            }
        }

        adapter.filterList(filteredList);
    }

    private boolean isBanglaLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        return language.equals("bn");
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