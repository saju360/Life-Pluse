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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.BloodDonerList;
import com.saju.lifepluse.activity.BloodNeed_Post;
import com.saju.lifepluse.activity.Blood_Donation_Registration;
import com.saju.lifepluse.activity.Blood_Organization_Home;
import com.saju.lifepluse.activity.DrawerLayout;
import com.saju.lifepluse.activity.Find_Blood_Bank_Home;
import com.saju.lifepluse.activity.SignIn;
import com.saju.lifepluse.adapter.BloodHomeAdapter;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;
import com.saju.lifepluse.modelclass.BloodNeedPostModel;
import com.saju.lifepluse.modelclass.BloodOrganizationAddModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cn.iwgang.countdownview.CountdownView;

public class Blood_Home_Fragment extends Fragment {

    TextView locationTextView, bloodtype_status_tv, bloodDonatstatus_tv;
    ImageButton signup_bloodBtn;
    CountdownView countdownview;
    MaterialCardView postfor_blood_btn, donateNowBtn, userbloodaccountstatus, findDoner_Btn, bloodorganazitionBtn, findbloodbankBtn;
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
    ArrayList<BloodDonerRequestModel> allDataList;
    



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
        bloodDonatstatus_tv = myview.findViewById(R.id.bloodDonatstatus_tv);
        findDoner_Btn = myview.findViewById(R.id.findDoner_Btn);
        bloodorganazitionBtn = myview.findViewById(R.id.bloodorganazitionBtn);
        findbloodbankBtn = myview.findViewById(R.id.findbloodbankBtn);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        allDataList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        String current_division = DrawerLayout.userDivision;

        adapter = new BloodHomeAdapter(getContext());
        bloodRecyclear.setLayoutManager(new LinearLayoutManager(getContext()));
        bloodRecyclear.setAdapter(adapter);

        divisiondata = getResources().getStringArray(R.array.division_data);

        ArrayAdapter<String> divionadapter = new ArrayAdapter<>(getContext(), R.layout.drop_down_item, divisiondata);
        division.setAdapter(divionadapter);

        handler = new Handler(Looper.getMainLooper());

        barChartMethod(); // Initialize chart settings
        checkuserAuth();
        DonerListDataRetrive(); // This will now handle both data retrieval and chart updates
        locationData();

        // Display current division data by default in the chart
        if (current_division != null && !current_division.isEmpty()) {
            selecteddivision = current_division;

            updateChartWithData();
        } else {

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
        findbloodbankBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Find_Blood_Bank_Home.class));
            }
        });
        postfor_blood_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BloodNeed_Post.class));
            }
        });


        // Modified division selection handler
        division.setOnItemClickListener((parent, view, position, id) -> {
            selecteddivision = ((TextView) view).getText().toString();
            selectDivisionLayout.setHint(selecteddivision);

            // Clear old data and refresh chart
            allDataList.clear();
            entries.clear();
            DonerListDataRetrive(); // Refresh data for new division
        });

        // Set initial division
        if (current_division != null && !current_division.isEmpty()) {
            selecteddivision = current_division;
            division.setText(selecteddivision, false);
            updateChartWithData();
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

    private void locationData() {


        if (DrawerLayout.locationText != null) {
            currentLocation = DrawerLayout.locationText;
            locationTextView.setText(currentLocation);
        } else {
            locationTextView.setText("Location not available");
        }

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

                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot != null) {
                        boolean is_form_filled = documentSnapshot.getBoolean("isform_filled");
                        boolean is_form_notfilled = documentSnapshot.getBoolean("isform_notfilled");


                        if (is_form_filled) {
                            approval_status();
                            Log.d("form_status", "Form  Filled Yet");

                        } else if (is_form_notfilled) {
                            Log.d("form_status", "Form Not Filled Yet");
                        }
                    } else {
                        Log.d("form_status", "DocumentSnapshot is null");
                    }
                } else {
                    Log.d("form_status", "Error getting document: ", task.getException());

                }


            }
        });

    }

    private void approval_status() {
        FirebaseUtil.donerUserDetails("bloodDoner").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    boolean isApproved = snapshot.getBoolean("approved");
                    boolean isDeclined = snapshot.getBoolean("declined");
                    String bloodtype = snapshot.getString("bloodType");
                    String lastdonateDate = snapshot.getString("donateType");

                    if (isApproved) {
                        userbloodaccountstatus.setVisibility(View.VISIBLE);
                        bloodtype_status_tv.setText(bloodtype);

                        if (lastdonateDate.contains("First time donate")) {
                            bloodDonatstatus_tv.setText("Donate Now");
                            countdownview.setVisibility(View.GONE);
                        } else {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                Date lastDonate = dateFormat.parse(lastdonateDate);
                                Calendar endDateCal = Calendar.getInstance();
                                endDateCal.setTime(lastDonate);
                                endDateCal.add(Calendar.DAY_OF_YEAR, 120); // Add 120 days
                                Date endDate = endDateCal.getTime();
                                Date currentDate = new Date();

                                long remainingMillis = endDate.getTime() - currentDate.getTime();

                                if (remainingMillis <= 0) {
                                    bloodDonatstatus_tv.setText("Donate Now");
                                    countdownview.setVisibility(View.GONE);
                                } else {
                                    countdownview.setVisibility(View.VISIBLE);
                                    long daysRemaining = TimeUnit.MILLISECONDS.toDays(remainingMillis);
                                    bloodDonatstatus_tv.setText("You can donate in " + daysRemaining + " days");
                                    countdownview.start(remainingMillis); // Start with exact remaining time
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                                bloodDonatstatus_tv.setText("Error parsing date");
                            }
                        }
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
        barChart.setFitBars(true); // Add this to make bars fit nicely
        barChart.setExtraBottomOffset(10f); // Add some padding at bottom

        // Customize X-axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getLabels()));
        xAxis.setLabelCount(getLabels().size());
        xAxis.setLabelRotationAngle(-45);

        // Customize Y-axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisLineColor(Color.BLACK);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setAxisMinimum(0f); // Start at 0
        leftAxis.setGranularity(1f); // Interval of 1

        // Disable right axis
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        // Customize Legend
        Legend legend = barChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(10f);
        legend.setTextSize(12f);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
    }

    private void updateChartWithData() {
        if (!isAdded() || entries == null || entries.isEmpty()) {
            return;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Blood Donors Available");
        dataSet.setColors(getColors());
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setHighlightEnabled(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.8f); // Set custom bar width
        barChart.setData(barData);

        // Refresh the chart with animation
        barChart.animateY(1000, Easing.EaseInOutQuad);
        barChart.invalidate();
    }



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


    //if (!isAdded()) return new int[0];
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


    private void loadFragment(Fragment fragment) {

        FragmentManager fm = getFragmentManager();

        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }


    //==========================================================

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

    private void DonerListDataRetrive() {
        if (!isAdded() || getActivity() == null) return;

        allDataList.clear();
        entries.clear();

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && isAdded()) {
                        // Initialize counters for each blood type
                        int[] bloodTypeCounts = new int[8];

                        for (QueryDocumentSnapshot userDoc : task.getResult()) {
                            String userId = userDoc.getId();
                            retrieveBloodDonorRequests(userId, bloodTypeCounts);
                        }
                    } else {
                        Log.e("Firestore", "Error getting users: ", task.getException());
                    }
                });
    }

    private void retrieveBloodDonorRequests(String userId, int[] bloodTypeCounts) {
        db.collection("users")
                .document(userId)
                .collection("bloodDoner")
                .whereEqualTo("district", selecteddivision)
                .whereEqualTo("approved", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot donorDoc : task.getResult()) {
                            String bloodType = donorDoc.getString("bloodType");

                            // Update counts based on blood type
                            switch (bloodType) {
                                case "A Positive": bloodTypeCounts[0]++; break;
                                case "B Positive": bloodTypeCounts[1]++; break;
                                case "AB Positive": bloodTypeCounts[2]++; break;
                                case "O Positive": bloodTypeCounts[3]++; break;
                                case "A Negative": bloodTypeCounts[4]++; break;
                                case "B Negative": bloodTypeCounts[5]++; break;
                                case "AB Negative": bloodTypeCounts[6]++; break;
                                case "O Negative": bloodTypeCounts[7]++; break;
                            }
                        }

                        // Update chart entries after processing all documents
                        entries.clear();
                        for (int i = 0; i < bloodTypeCounts.length; i++) {
                            entries.add(new BarEntry(i, bloodTypeCounts[i]));
                        }

                        // Update UI only if fragment is attached
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                entries.clear();
                                for (int i = 0; i < bloodTypeCounts.length; i++) {
                                    entries.add(new BarEntry(i, bloodTypeCounts[i]));
                                }
                                updateChartWithData();
                                adapter.notifyDataSetChanged();
                            });
                        }
                    } else {
                        Log.e("Firestore", "Error getting donor data: ", task.getException());
                    }
                });
    }






}


