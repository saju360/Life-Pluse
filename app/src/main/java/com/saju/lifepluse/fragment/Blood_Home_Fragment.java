package com.saju.lifepluse.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.BloodDonerList;
import com.saju.lifepluse.activity.BloodNeed_Post;
import com.saju.lifepluse.activity.Blood_Donation_Registration;
import com.saju.lifepluse.activity.Blood_Organization_Home;
import com.saju.lifepluse.activity.DrawerLayout;
import com.saju.lifepluse.activity.SignIn;
import com.saju.lifepluse.adapter.BloodHomeAdapter;
import com.saju.lifepluse.modelclass.BloodNeedPostModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.Random;

import cn.iwgang.countdownview.CountdownView;

public class Blood_Home_Fragment extends Fragment {

    TextView locationTextView, bloodtype_status_tv;
    ImageButton signup_bloodBtn;
    CountdownView countdownview;
    MaterialCardView postfor_blood_btn, donateNowBtn, userbloodaccountstatus, findDoner_Btn, bloodorganazitionBtn, bloodbankBtn;
    String currentLocation, selecteddivision;
    RecyclerView bloodRecyclear;
    AutoCompleteTextView division;
    TextInputLayout selectDivisionLayout;
    String[] divisiondata;
    ArrayList<BarEntry> entries = new ArrayList<>();
    BloodHomeAdapter adapter;
    FirebaseFirestore db;
    Handler handler;

    private BarChart barChart;
    FirebaseUser currentUser;
    public static LottieAnimationView empty_anim;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_blood_home, container, false);

        locationTextView = myview.findViewById(R.id.locationTextView);
        bloodRecyclear = myview.findViewById(R.id.BloodNeedrecyclearview);
        division = myview.findViewById(R.id.selectdivision);
        selectDivisionLayout = myview.findViewById(R.id.selectDivisionLayout);
        barChart = myview.findViewById(R.id.bar_chart);
        signup_bloodBtn = myview.findViewById(R.id.signup_bloodBtn);
        postfor_blood_btn = myview.findViewById(R.id.postfor_blood_btn);
        donateNowBtn = myview.findViewById(R.id.donateNowBtn);
        userbloodaccountstatus = myview.findViewById(R.id.userbloodaccountstatus);
        empty_anim = myview.findViewById(R.id.empty_anim);
        countdownview = myview.findViewById(R.id.countdownview);
        bloodtype_status_tv = myview.findViewById(R.id.bloodtype_status_tv);
        findDoner_Btn = myview.findViewById(R.id.findDoner_Btn);
        bloodorganazitionBtn = myview.findViewById(R.id.bloodorganazitionBtn);
        bloodbankBtn = myview.findViewById(R.id.bloodbankBtn);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();

        String current_division = DrawerLayout.userDivision;

        adapter = new BloodHomeAdapter(getContext());
        bloodRecyclear.setLayoutManager(new LinearLayoutManager(getContext()));
        bloodRecyclear.setAdapter(adapter);

        divisiondata = getResources().getStringArray(R.array.division_data);

        ArrayAdapter<String> divionadapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, divisiondata);
        division.setAdapter(divionadapter);

        handler = new Handler(Looper.getMainLooper());

        barChartMethod();
        checkuserAuth();

        // Display current division data by default in the chart
        if (current_division != null && !current_division.isEmpty()) {
            selecteddivision = current_division;
            addEntriesForSelectedDivision();
            updateChartWithData();
        } else {
            addEntriesForDhakaDivision();
            updateChartWithData();
        }

        findDoner_Btn.setOnClickListener(v -> startActivity(new Intent(getContext(), BloodDonerList.class)));

        division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDivisionLayout.setHint("Select Your Division");
            }
        });

        bloodorganazitionBtn.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), Blood_Organization_Home.class));

        });
        postfor_blood_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BloodNeed_Post.class));
            }
        });

        // Auto-select division in AutoCompleteTextView
        if (current_division != null && !current_division.isEmpty()) {
            selectDivisionLayout.setHint(current_division);
        }

        division.setOnItemClickListener((parent, view, position, id) -> {
            selecteddivision = ((TextView) view).getText().toString();

            // Clear existing entries
            entries.clear();

            // Add entries based on the selected division
            if (selecteddivision.equals("Dhaka Division") || selecteddivision.equals("ঢাকা বিভাগ")) {
                addEntriesForDhakaDivision();
            } else if (selecteddivision.equals("Chattogram Division") || selecteddivision.equals("চট্টগ্রাম বিভাগ")) {
                addEntriesForChattogramDivision();
            }

            // Update the chart with the new data
            updateChartWithData();
        });

        if (DrawerLayout.locationText != null) {
            currentLocation = DrawerLayout.locationText;
            locationTextView.setText(currentLocation);
        } else {
            locationTextView.setText("Location not available");
        }


        signup_bloodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUser == null) {
                    startActivity(new Intent(getContext(), SignIn.class));
                } else {
                    loadFragment(new Profile_Fragment());
                }
            }
        });
        donateNowBtn.setOnClickListener(v -> {

            if (currentUser == null) {
                Toast.makeText(getContext(), "Please SignIn First", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getContext(), SignIn.class));
            } else {
                startActivity(new Intent(getContext(), Blood_Donation_Registration.class));
            }

        });


        return myview;
    }

    private void checkuserAuth() {


        if (currentUser != null) {
            form_status();
        }

    }

    void form_status() {

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot !=null){
                        boolean is_form_filled = documentSnapshot.getBoolean("isform_filled");
                        boolean is_form_notfilled = documentSnapshot.getBoolean("isform_notfilled");


                        if (is_form_filled) {
                            approval_status();
                            Log.d("form_status", "Form  Filled Yet");

                        } else if (is_form_notfilled) {
                            Log.d("form_status", "Form Not Filled Yet");
                        }
                    }else {
                        Log.d("form_status", "DocumentSnapshot is null");
                    }
                }else {
                    Log.d("form_status", "Error getting document: ", task.getException());

                }





            }
        });

    }

    private void approval_status() {

        FirebaseUtil.donerUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if (task.isSuccessful()) {

                    DocumentSnapshot snapshot = task.getResult();

                    boolean isApproved = snapshot.getBoolean("approved");
                    boolean isDeclined = snapshot.getBoolean("declined");
                    String bloodtype = snapshot.getString("bloodType");
                    if (isApproved) {

                        userbloodaccountstatus.setVisibility(View.VISIBLE);
                        bloodtype_status_tv.setText(bloodtype);


                    } else if (isDeclined) {
                        userbloodaccountstatus.setVisibility(View.GONE);

                    } else {
                        userbloodaccountstatus.setVisibility(View.GONE);

                    }


                }


            }
        });


    }

    private void barChartMethod() {

        // Customize the chart appearance
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawBorders(false);

        // Customize X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getLabels())); // Custom labels for blood groups

        // Customize Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setTextColor(Color.BLACK);

        // Customize Legend
        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.BLACK);

        // Add data to the chart
        /*ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, new float[]{40f}));
        entries.add(new BarEntry(1, new float[]{15f}));
        entries.add(new BarEntry(2, new float[]{25f}));
        entries.add(new BarEntry(3, new float[]{30f}));
        entries.add(new BarEntry(4, new float[]{10f}));
        entries.add(new BarEntry(5, new float[]{18f}));
        entries.add(new BarEntry(6, new float[]{12f}));
        entries.add(new BarEntry(7, new float[]{22f}));*/

        Log.d("Blood_Home_Fragment", "Bar chart setup completed");

        //BarDataSet dataSet = new BarDataSet(entries, "Blood Stock");
        //dataSet.setColors(getColors()); // Custom colors for each blood group
        //dataSet.setStackLabels(getLabels().toArray(new String[0])); // Custom stack labels for each blood group

        //BarData barData = new BarData(dataSet);
        //barChart.setData(barData);

        // Animation
        barChart.animateY(1500, Easing.EaseInOutExpo);
        //barChart.invalidate(); // refresh

    }

    private void updateChartWithData() {
        BarDataSet dataSet = new BarDataSet(entries, "Blood Stock");
        dataSet.setColors(getColors()); // Custom colors for each blood group
        dataSet.setStackLabels(getLabels().toArray(new String[0]));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Refresh the chart
        barChart.invalidate();
        Log.d("Blood_Home_Fragment", "Chart data updated"); // Debugging statement
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dataRetrive();
        adapter.startDataUpdate();
    }

    private void dataRetrive() {
        db.collection("Blood Need Post")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            ArrayList<BloodNeedPostModel> bloodpostData = new ArrayList<>();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                BloodNeedPostModel model = document.toObject(BloodNeedPostModel.class);
                                bloodpostData.add(model);
                            }
                            if (!bloodpostData.isEmpty()) {
                                BloodNeedPostModel randomPost = getRandomPost(bloodpostData);
                                adapter.updateData(randomPost);
                            }

                            if (bloodpostData.isEmpty()) {
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

    private BloodNeedPostModel getRandomPost(ArrayList<BloodNeedPostModel> bloodpostData) {
        Random random = new Random();
        int randomIndex = random.nextInt(bloodpostData.size());
        return bloodpostData.get(randomIndex);
    }


    //============================barchart code start frome here=====================//
    private ArrayList<String> getLabels() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("A+");
        labels.add("B+");
        labels.add("AB+");
        labels.add("O+");
        labels.add("A-");
        labels.add("B-");
        labels.add("AB-");
        labels.add("O-");
        return labels;
    }

    // Custom method to get colors for each blood group
    private ArrayList<Integer> getColors() {
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(51, 204, 255)); // Light blue
        colors.add(Color.rgb(255, 102, 102)); // Light red
        colors.add(Color.rgb(255, 204, 102)); // Light orange
        colors.add(Color.rgb(204, 255, 102)); // Light green
        colors.add(Color.rgb(255, 102, 204)); // Light pink
        colors.add(Color.rgb(255, 255, 102)); // Light yellow
        colors.add(Color.rgb(204, 102, 255)); // Light purple
        colors.add(Color.rgb(102, 255, 204)); // Light turquoise
        return colors;
    }

    private void addEntriesForSelectedDivision() {
        if (selecteddivision.equals("Dhaka Division") || selecteddivision.equals("ঢাকা বিভাগ")) {
            addEntriesForDhakaDivision();
        } else if (selecteddivision.equals("Chattogram Division") || selecteddivision.equals("চট্টগ্রাম বিভাগ")) {
            addEntriesForChattogramDivision();
        }
    }


    // Helper method to add entries for Dhaka Division
    private void addEntriesForDhakaDivision() {
        // Add entries for Dhaka Division
        entries.add(new BarEntry(0, new float[]{40f}));
        entries.add(new BarEntry(1, new float[]{15f}));
        entries.add(new BarEntry(2, new float[]{25f}));
        entries.add(new BarEntry(3, new float[]{30f}));
        entries.add(new BarEntry(4, new float[]{10f}));
        entries.add(new BarEntry(5, new float[]{18f}));
        entries.add(new BarEntry(6, new float[]{12f}));
        entries.add(new BarEntry(7, new float[]{22f}));
        // ...
    }

    // Helper method to add entries for Chattogram Division
    private void addEntriesForChattogramDivision() {
        // Add entries for Chattogram Division

        entries.add(new BarEntry(0, new float[]{140f}));
        entries.add(new BarEntry(1, new float[]{10f}));
        entries.add(new BarEntry(2, new float[]{2f}));
        entries.add(new BarEntry(3, new float[]{0f}));
        entries.add(new BarEntry(4, new float[]{50f}));
        entries.add(new BarEntry(5, new float[]{18f}));
        entries.add(new BarEntry(6, new float[]{12f}));
        entries.add(new BarEntry(7, new float[]{22f}));
        // ...
    }

    private void loadFragment(Fragment fragment) {

        FragmentManager fm = getFragmentManager();

        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }
}


