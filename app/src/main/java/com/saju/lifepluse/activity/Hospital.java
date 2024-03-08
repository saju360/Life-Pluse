package com.saju.lifepluse.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.HPRecyclearAdapter;
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

        hpitalListData  = new ArrayList<>();
        db = FirebaseFirestore.getInstance();


        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        HPDataRetrive();

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
                        adapter = new HPRecyclearAdapter(Hospital.this, hpitalListData);
                        recyclerView.setAdapter(adapter);


                    }
                });

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
