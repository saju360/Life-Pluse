package com.saju.lifepluse.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.HealthtipsAdapter;
import com.saju.lifepluse.modelclass.HealthtipsModel;

import java.util.ArrayList;


public class Health_tips extends Fragment {


    RecyclerView healthtips_recyclearview;
    HealthtipsAdapter  healthtipsAdapter;
    ArrayList<HealthtipsModel> healthtipsListData;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_health_tips, container, false);

        // Set the status bar title
        if (getActivity() != null) {
            // Access the ActionBar from the hosting activity
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Health Tips");
        }

        healthtipsListData = new ArrayList<>();

        healthtips_recyclearview = view.findViewById(R.id.healthtips_recyclearview);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        db = FirebaseFirestore.getInstance();


        healthtipsAdapter = new HealthtipsAdapter(getActivity(), healthtipsListData);
        healthtips_recyclearview.setLayoutManager(new LinearLayoutManager(getActivity()));
        healthtips_recyclearview.setAdapter(healthtipsAdapter);
        healthtipsAdapter.notifyDataSetChanged();
        healthDataretrive();


        swipeRefreshLayout.setOnRefreshListener(() -> {

            healthDataretrive();
            // Call your data retrieval method here
            healthtipsAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false); // Call this when the refresh is complete
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);








        return view;
    }

    private void healthDataretrive() {

        healthtipsListData.clear();

        db.collection("Healthtips").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {


                if (task.isSuccessful()){
                    for (int i = 0; i < task.getResult().size(); i++) {
                        HealthtipsModel healthtipsModel = task.getResult().getDocuments().get(i).toObject(HealthtipsModel.class);
                        healthtipsListData.add(healthtipsModel);

                        Log.d("task", "onComplete: " + healthtipsModel.getTips_title());
                }
                    healthtipsAdapter.notifyDataSetChanged();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("task", "onComplete: " + e.toString());
            }
        });

    }
}