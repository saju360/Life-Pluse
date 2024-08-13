package com.saju.lifepluse.fragment;


import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.BloodNeed_Post;
import com.saju.lifepluse.activity.DrawerLayout;
import com.saju.lifepluse.adapter.BloodNeedPostREcyclearview;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;
import com.saju.lifepluse.modelclass.BloodNeedPostModel;

import java.util.ArrayList;


public class BloodNeed_Fragment extends Fragment {

    RecyclerView bloodRecyclearId;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView addpostAnimationId, empty_anim;
    TextView locationTextView, blooddonate_motivation;
    ImageView filterIcon;
    ArrayList<BloodNeedPostModel> BloodpostData;
    ArrayList<BloodNeedPostModel> originalDataList;

    String userDivision;
    FirebaseFirestore db;
    CollectionReference bloodPostsCollection;

    private static final String PREF_KEY_PREFIX = "notified_post_";

    private SharedPreferences sharedPreferences;
    private ArrayList<String> selectedBloodGroups = new ArrayList<>();
    BloodNeedPostREcyclearview adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View myview = inflater.inflate(R.layout.fragment_blood_need_, container, false);

        bloodRecyclearId = myview.findViewById(R.id.bloodRecyclearId);
        swipeRefreshLayout = myview.findViewById(R.id.swipeRefreshLayout);
        locationTextView = myview.findViewById(R.id.locationTextView);
        blooddonate_motivation = myview.findViewById(R.id.blooddonate_motivation);
        addpostAnimationId = myview.findViewById(R.id.addpostAnimationId);
        empty_anim = myview.findViewById(R.id.empty_anim);
        filterIcon = myview.findViewById(R.id.filterIcon);

        db = FirebaseFirestore.getInstance();

        bloodPostsCollection = db.collection("Blood Need Post");

        sharedPreferences = getActivity().getSharedPreferences("bloodpost", Context.MODE_PRIVATE);


        userDivision = DrawerLayout.userDivision;

        blooddonate_motivation.setSelected(true);


        locationRetrive();
        dataRetrive();
        //listenForNewBloodPosts();


        addpostAnimationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), BloodNeed_Post.class));

            }
        });

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBloodGroupFilterDialog();
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataRetrive();
                // Call your data retrieval method here

                selectedBloodGroups.clear();
                swipeRefreshLayout.setRefreshing(false); // Call this when the refresh is complete
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);


        return myview;
    }//=============================oncreate end here================//


    private void locationRetrive() {

        if (DrawerLayout.locationText != null) {
            locationTextView.setText(DrawerLayout.locationText);
        } else {
            locationTextView.setText("Location not available");
            // Handle the case where locationText is null
        }
    }

    private void dataRetrive() {
        BloodpostData = new ArrayList<>();
        originalDataList = new ArrayList<>();

        db.collection("Blood Need Post")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            BloodpostData.clear();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                BloodNeedPostModel model = document.toObject(BloodNeedPostModel.class);
                                BloodpostData.add(model);
                                originalDataList.add(model);
                            }


                            // Update RecyclerView adapter after retrieving all documents
                            prioritizePostsByDivision(BloodpostData, userDivision);
                            bloodRecyclearId.setLayoutManager(new LinearLayoutManager(getContext()));
                            adapter = new BloodNeedPostREcyclearview(getContext(),BloodpostData, originalDataList);
                            bloodRecyclearId.setAdapter(adapter);

                            if (BloodpostData.isEmpty()) {
                                empty_anim.setVisibility(View.VISIBLE);
                            } else {
                                empty_anim.setVisibility(View.GONE);
                            }


                        } else {
                            // If there's no data, show empty animation
                            empty_anim.setVisibility(View.VISIBLE);
                        }


                    }

                });
    }


    private void prioritizePostsByDivision(ArrayList<BloodNeedPostModel> bloodpostData, String userDivision) {
        ArrayList<BloodNeedPostModel> matchedPosts = new ArrayList<>();
        ArrayList<BloodNeedPostModel> otherPosts = new ArrayList<>();

        // Iterate through bloodpostData
        for (BloodNeedPostModel post : bloodpostData) {
            if (post.getDivision() != null && post.getDivision().equals(userDivision)) {
                // If the post's division matches user's division, add it to matchedPosts
                matchedPosts.add(post);
            } else {
                // Otherwise, add it to otherPosts
                otherPosts.add(post);
            }
        }

        // Clear bloodpostData and add matched posts followed by other posts
        bloodpostData.clear();
        bloodpostData.addAll(matchedPosts);
        bloodpostData.addAll(otherPosts);
    }


    private void showBloodGroupFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
            bloodRecyclearId.setVisibility(View.GONE);
            empty_anim.setVisibility(View.VISIBLE);
        } else {
            bloodRecyclearId.setVisibility(View.VISIBLE);
            empty_anim.setVisibility(View.GONE);
        }
    }

}