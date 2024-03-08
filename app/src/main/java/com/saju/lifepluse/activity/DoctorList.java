package com.saju.lifepluse.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.DoctorListAdapter;
import com.saju.lifepluse.adapter.HorizontalAdapter;
import com.saju.lifepluse.modelclass.DoctorListModel;
import com.saju.lifepluse.modelclass.DoctorSpecialityModel;
import com.saju.lifepluse.modelclass.HorizontalItemModel;

import java.util.ArrayList;
import java.util.List;

public class DoctorList extends AppCompatActivity implements HorizontalAdapter.OnItemClickListener {

    private MaterialToolbar materialToolbar;
    private RecyclerView horizontalRecyclerView, doctorRecyclearviewId;
    private ImageView filterIcon;
    private EditText searchEditText;
    private HorizontalAdapter horizontalAdapter;
    private DoctorListAdapter doctorListAdapter;

    private FirebaseFirestore db;

    private ArrayList<DoctorListModel> doctorlList;
    private ArrayList<DoctorListModel> originalDataList;

    private ArrayList<HorizontalItemModel> doctorspecialityDataList;

    private List<String> hospitalList;
    private List<String> specialityList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        doctorspecialityDataList = new ArrayList<>();
        doctorlList = new ArrayList<>();

        initId();
        setupToolbar();
        fetchDoctorSpecialties();
        doctorDataRetrieve();

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterBottomSheet();
            }
        });
    }

    private void doctorDataRetrieve() {
        String doctorcatId = getIntent().getStringExtra("doctorcatId");
        String doctorspcatId = getIntent().getStringExtra("doctorlistcatId");

        db.collection("Category").document(doctorcatId).collection("Doctor_Speciality").document(doctorspcatId).collection("Doctor_list").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    doctorlList.clear();
                    originalDataList = new ArrayList<>();

                    List<DoctorListModel> data = value.toObjects(DoctorListModel.class);
                    doctorlList.addAll(data);
                    originalDataList.addAll(data);

                    extractHospitalAndSpeciality();

                    doctorListAdapter = new DoctorListAdapter(DoctorList.this, doctorlList);
                    doctorRecyclearviewId.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    doctorRecyclearviewId.setAdapter(doctorListAdapter);
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterDoctorList(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void filterDoctorList(String query) {
        ArrayList<DoctorListModel> filteredList = new ArrayList<>();

        for (DoctorListModel model : originalDataList) {
            String doctorName;
            SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String language = prefs.getString("My_Lang", "");

            if (language.equals("bn")) {
                doctorName = model.getDocnameBangla();
            } else {
                doctorName = model.getDocnameEnglish();
            }

            if (doctorName.contains(query.toLowerCase())) {
                filteredList.add(model);
            }
        }

        doctorListAdapter.filterList(filteredList);
    }

    private void initId() {
        materialToolbar = findViewById(R.id.toolbarId);
        horizontalRecyclerView = findViewById(R.id.horizontalRecyclerView);
        doctorRecyclearviewId = findViewById(R.id.doctorRecyclearviewId);
        filterIcon = findViewById(R.id.filterIcon);
        searchEditText = findViewById(R.id.searchEditText);
        db = FirebaseFirestore.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(materialToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));
        }
    }

    private void fetchDoctorSpecialties() {
        String doctorcatId = getIntent().getStringExtra("doctorcatId");
        String doctorspcatId = getIntent().getStringExtra("doctorlistcatId");

        db.collection("Category").document(doctorcatId).collection("Doctor_Speciality")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            doctorspecialityDataList.clear();
                            List<HorizontalItemModel> data = value.toObjects(HorizontalItemModel.class);
                            doctorspecialityDataList.addAll(data);

                            for (int i = 0; i < doctorspecialityDataList.size(); i++) {
                                HorizontalItemModel item = doctorspecialityDataList.get(i);
                                if (item.getCatId().equals(doctorspcatId)) {
                                    clearPreviousSelections();

                                    SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
                                    String language = prefs.getString("My_Lang", "");
                                    if (language.equals("bn")) {
                                        materialToolbar.setTitle(item.getSp_bangla());
                                    } else {
                                        materialToolbar.setTitle(item.getSp_english());
                                    }

                                    doctorspecialityDataList.remove(i);
                                    doctorspecialityDataList.add(0, item);
                                    item.setSelected(true);
                                    break;
                                }
                            }
                            updateHorizontalRecyclerView();
                        }
                    }
                });
    }

    private void clearPreviousSelections() {
        for (HorizontalItemModel item : doctorspecialityDataList) {
            if (item.isSelected()) {
                item.setSelected(false);
                item.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        }
    }


    private void updateHorizontalRecyclerView() {
        horizontalAdapter = new HorizontalAdapter(getApplicationContext(), doctorspecialityDataList);
        horizontalAdapter.setOnItemClickListener(DoctorList.this);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(DoctorList.this, LinearLayoutManager.HORIZONTAL, false);
        horizontalRecyclerView.setLayoutManager(horizontalLayoutManager);
        horizontalRecyclerView.setAdapter(horizontalAdapter);
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

    @Override
    public void onItemClick(HorizontalItemModel item) {
        String doctorcatId = getIntent().getStringExtra("doctorcatId");
        String clickedItemId = item.getCatId();

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (language.equals("bn")) {
            materialToolbar.setTitle(item.getSp_bangla());
        } else {
            materialToolbar.setTitle(item.getSp_english());
        }

        db.collection("Category").document(doctorcatId).collection("Doctor_Speciality").document(clickedItemId).collection("Doctor_list").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    doctorlList.clear();
                    List<DoctorListModel> data = value.toObjects(DoctorListModel.class);
                    doctorlList.addAll(data);

                    doctorListAdapter = new DoctorListAdapter(DoctorList.this, doctorlList);
                    doctorRecyclearviewId.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    doctorRecyclearviewId.setAdapter(doctorListAdapter);
                }
            }
        });
    }

    private void showFilterBottomSheet() {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(bottomSheetView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) AutoCompleteTextView hospitalautocomplete = bottomSheetView.findViewById(R.id.selecthospital);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) AutoCompleteTextView specialityautocomplete = bottomSheetView.findViewById(R.id.selecetspecialityId);

        ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item, getHospitalList());
        ArrayAdapter<String> specialityAdapter = new ArrayAdapter<>(this, R.layout.drop_down_item, getSpecialityList());

        hospitalautocomplete.setAdapter(hospitalAdapter);
        specialityautocomplete.setAdapter(specialityAdapter);

        Button searchButton = bottomSheetView.findViewById(R.id.searchButton);
        Button cancelButton = bottomSheetView.findViewById(R.id.cancelButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedHospital = hospitalautocomplete.getText().toString();
                String selectedSpeciality = specialityautocomplete.getText().toString();

                filterDoctors(selectedHospital, selectedSpeciality);
                bottomSheetDialog.dismiss();
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void filterDoctors(String selectedHospital, String selectedSpeciality) {
        ArrayList<DoctorListModel> filteredList = new ArrayList<>();
        for (DoctorListModel model : originalDataList) {
            if (model.getDoc_hpnameEnglish().equals(selectedHospital) && model.getDoc_spEnglish().equals(selectedSpeciality)) {
                filteredList.add(model);
            } else if (model.getDoc_hpnameEnglish().equals(selectedHospital) || model.getDoc_spEnglish().equals(selectedSpeciality)) {
                if (!filteredList.contains(model)) {
                    filteredList.add(model);
                }
            }
        }

        doctorListAdapter.filterList(filteredList);
    }

    private void extractHospitalAndSpeciality() {
        hospitalList = new ArrayList<>();
        specialityList = new ArrayList<>();

        for (DoctorListModel model : originalDataList) {
            if (!hospitalList.contains(model.getDoc_hpnameEnglish())) {
                hospitalList.add(model.getDoc_hpnameEnglish());
            }

            if (!specialityList.contains(model.getDoc_spEnglish())) {
                specialityList.add(model.getDoc_spEnglish());
            }
        }
    }

    private List<String> getHospitalList() {
        return hospitalList;
    }

    private List<String> getSpecialityList() {
        return specialityList;
    }
}
