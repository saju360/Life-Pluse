package com.saju.lifepluse.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.HPRecyclearAdapter;
import com.saju.lifepluse.modelclass.DoctorSpecialityModel;
import com.saju.lifepluse.modelclass.HospitalModel;

import java.util.ArrayList;
import java.util.List;

public class Hospital extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;
    HPRecyclearAdapter adapter;
    private ArrayList<HospitalModel> hpitalListData;
    MaterialToolbar materialToolbar;
    EditText searchEditText;
    private ArrayList<HospitalModel> originalDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);
        materialToolbar = findViewById(R.id.toolbarId);

        setSupportActionBar(materialToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back);
        }


        recyclerView = findViewById(R.id.recyclearViewId);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchEditText = findViewById(R.id.searchEditText);

        hpitalListData  = new ArrayList<>();
        db = FirebaseFirestore.getInstance();


        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        HPDataRetrive();

        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Call your data retrieval method here
            HPDataRetrive();
            swipeRefreshLayout.setRefreshing(false); // Call this when the refresh is complete
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the status bar color programmatically
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));
        }

        searchEditText.setHint("Search by hospital name...");

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

    }

    private void HPDataRetrive() {
        String catId = getIntent().getStringExtra("catId");

        hpitalListData.clear();
        db.collection("Category")
                .document(catId)
                .collection("Hospital_list").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        hpitalListData.clear();
                        List<HospitalModel> data = value.toObjects(HospitalModel.class);
                        hpitalListData.addAll(data);
                        originalDataList = new ArrayList<>(hpitalListData);
                        adapter = new HPRecyclearAdapter(Hospital.this, hpitalListData);
                        recyclerView.setAdapter(adapter);


                    }
                });

    }

    private void filterData(String query) {
        ArrayList<HospitalModel> filteredList = new ArrayList<>();

        for (HospitalModel model : originalDataList) {
            String hospitalName;
            if (isBanglaLanguage()) {
                hospitalName = model.getHp_bangla().toLowerCase();
            } else {
                hospitalName = model.getHp_english().toLowerCase();
            }

            if (hospitalName.contains(query.toLowerCase())) {
                filteredList.add(model);
            }
        }

        adapter.filterList(filteredList);
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

    private boolean isBanglaLanguage() {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        return language.equals("bn");
    }


}
