package com.saju.lifepluse.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    FirebaseFirestore db;
    ArrayList<BloodDonerRequestModel> allDataList;
    DonerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_doner_list);

        statusbar();
        Init();
        DataRetrive();


    }

    private void Init() {

        donerList_swipeRefreshLayout = findViewById(R.id.donerlist_swipeRefreshLayout);
        donerList_Recyclearview = findViewById(R.id.donerlistRecyclearId);
        donerList_Recyclearview.setLayoutManager(new LinearLayoutManager(this));
        allDataList = new ArrayList<>();
        adapter = new DonerListAdapter(BloodDonerList.this, allDataList);
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
                            // Handle task unsuccessful
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
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
                            if (document.exists()) {
                                // Convert the document to BloodDonerRequestModel and add to allDataList
                                BloodDonerRequestModel model = document.toObject(BloodDonerRequestModel.class);
                                allDataList.add(model);


                                // Log the size of the list
                                Log.d("DataRetrieved", "Size of allDataList: " + allDataList.size());

                                // Notify adapter about the data change
                                adapter.notifyDataSetChanged();
                            } else {
                                Log.d("Firestore", "No such document");
                            }
                        } else {
                            // Handle task unsuccessful
                            Log.e("Firestore", "Error getting document: ", task.getException());
                        }
                    }
                });
    }
}