package com.saju.lifepluse.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.DonerListAdapter;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;

import java.util.ArrayList;

public class BloodDonerList extends AppCompatActivity {

    SwipeRefreshLayout donerList_swipeRefreshLayout;
    RecyclerView donerList_Recyclearview;
    ImageView button_back, filter_button;
    FirebaseFirestore db;
    ArrayList<BloodDonerRequestModel> allDataList;
    ArrayList<BloodDonerRequestModel> originalDataList;
    private ArrayList<String> selectedBloodGroups = new ArrayList<>();

    DonerListAdapter adapter;
    LottieAnimationView empty_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_doner_list);

        statusbar();
        Init();
        DataRetrive();

        donerList_swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataRetrive();
                selectedBloodGroups.clear();
                donerList_swipeRefreshLayout.setRefreshing(false);
            }
        });

        donerList_swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BloodDonerList.this, BloodBank.class));
                finish();
            }
        });

        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBloodGroupFilterDialog();
            }
        });
    }

    private void Init() {
        donerList_swipeRefreshLayout = findViewById(R.id.donerlist_swipeRefreshLayout);
        donerList_Recyclearview = findViewById(R.id.donerlistRecyclearId);
        empty_anim = findViewById(R.id.empty_anim);
        button_back = findViewById(R.id.button_back);
        filter_button = findViewById(R.id.filter_button);

        donerList_Recyclearview.setLayoutManager(new LinearLayoutManager(this));
        allDataList = new ArrayList<>();
        originalDataList = new ArrayList<>();
        adapter = new DonerListAdapter(BloodDonerList.this, allDataList, originalDataList);
        donerList_Recyclearview.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
    }

    private void statusbar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }
    }

    private void DataRetrive() {
        allDataList.clear(); // Clear previous data
        originalDataList.clear(); // Also clear original data list to avoid duplication
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getId();
                                retrieveBloodDonorRequests(userId);
                                Log.d("userId", userId);
                            }
                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                        donerList_swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void retrieveBloodDonorRequests(String userId) {
        db.collection("users")
                .document(userId)
                .collection("bloodDoner")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists() && document.getBoolean("approved")) {
                                BloodDonerRequestModel model = document.toObject(BloodDonerRequestModel.class);

                                if (model != null) {
                                    // Add to both allDataList and originalDataList
                                    allDataList.add(model);
                                    originalDataList.add(model);
                                }

                                Log.d("DataRetrieved", "Size of originalDataList: " + originalDataList.size());
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d("Firestore", "No such document");
                            }

                            updateUIVisibility();

                        } else {
                            Log.e("Firestore", "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    private void showBloodGroupFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] bloodGroups = {"A Positive", "A Negative", "B Positive", "B Negative", "O Positive", "O Negative", "AB Positive", "AB Negative"};
        boolean[] checkedItems = new boolean[bloodGroups.length];

        // Initialize checkedItems based on the previous selection
        for (int i = 0; i < bloodGroups.length; i++) {
            checkedItems[i] = selectedBloodGroups.contains(bloodGroups[i]);
        }

        builder.setTitle("Filter by Blood Group")
                .setMultiChoiceItems(bloodGroups, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // Update the checkedItems array based on user interaction
                        checkedItems[which] = isChecked;
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear the previous selection and update with the new one
                        selectedBloodGroups.clear();
                        for (int i = 0; i < bloodGroups.length; i++) {
                            if (checkedItems[i]) {
                                selectedBloodGroups.add(bloodGroups[i]);
                            }
                        }
                        adapter.filterByBloodGroups(selectedBloodGroups);
                        updateUIVisibility();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBloodGroups.clear(); // Clear all selections
                        adapter.resetFilter(); // Reset the filter
                        updateUIVisibility();
                    }
                });

        builder.create().show();
    }



    private void updateUIVisibility() {
        if (adapter.getItemCount() == 0) {
            donerList_Recyclearview.setVisibility(View.GONE);
            empty_anim.setVisibility(View.VISIBLE);
        } else {
            donerList_Recyclearview.setVisibility(View.VISIBLE);
            empty_anim.setVisibility(View.GONE);
        }
    }


}
