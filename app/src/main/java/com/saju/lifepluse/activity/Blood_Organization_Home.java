package com.saju.lifepluse.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.Blood_Organization_Adapter;
import com.saju.lifepluse.adapter.DonerListAdapter;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;
import com.saju.lifepluse.modelclass.BloodOrganizationAddModel;
import com.saju.lifepluse.modelclass.HospitalModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;

public class Blood_Organization_Home extends AppCompatActivity {

    TextView locationTextView;
    LottieAnimationView addorganizationAnimationId, empty_anim;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView bloodOrgRecyclearId;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    Blood_Organization_Adapter adapter;
    ArrayList<BloodOrganizationAddModel> orgallDataList;
    EditText searchEditText;
    private ArrayList<BloodOrganizationAddModel> originalDataList;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_organization_home);
        init();
        locationRetrive();
        statusbarcolor();
        DataRetrive();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                DataRetrive();
                // Call your data retrieval method here
                swipeRefreshLayout.setRefreshing(false); // Call this when the refresh is complete
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);




        addorganizationAnimationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentUser == null) {
                    Toast.makeText(getApplicationContext(), "Please SignIn First", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                } else {
                    form_status();
                }


            }
        });

        searchEditText.setHint("Search by address...");

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




    void init(){
        locationTextView = findViewById(R.id.locationTextView);
        addorganizationAnimationId = findViewById(R.id.addorganizationAnimationId);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        bloodOrgRecyclearId = findViewById(R.id.bloodRecyclearId);
        empty_anim = findViewById(R.id.empty_anim);
        searchEditText = findViewById(R.id.searchEditText);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        bloodOrgRecyclearId.setLayoutManager(new LinearLayoutManager(this));
        orgallDataList = new ArrayList<>();
        adapter = new Blood_Organization_Adapter(Blood_Organization_Home.this, orgallDataList);
        bloodOrgRecyclearId.setAdapter(adapter);

    }

    void form_status() {

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot !=null){
                        boolean is_form_filled = documentSnapshot.getBoolean("isorgadd");
                        boolean is_form_notfilled = documentSnapshot.getBoolean("isorgnotadd");


                        if (is_form_filled) {
                            Log.d("orgform_status", "OrgForm  Filled Yet");
                            startActivity(new Intent(getApplicationContext(), Blood_Organization_Edit_Profile.class));
                            Toast.makeText(Blood_Organization_Home.this, "You already Added Your ORG", Toast.LENGTH_SHORT).show();

                        } else if (is_form_notfilled) {
                            Log.d("orgform_status", "OrgForm Not Filled Yet");
                            startActivity(new Intent(Blood_Organization_Home.this, Blood_Organization_Post.class));
                        }
                    }else {
                        Log.d("orgform_status", "DocumentSnapshot is null");
                    }
                }else {
                    Log.d("orgform_status", "Error getting document: ", task.getException());

                }





            }
        });

    }


    public void locationRetrive(){
        if (DrawerLayout.locationText != null) {
            locationTextView.setText(DrawerLayout.locationText);
        } else {
            locationTextView.setText("Location not available");
            // Handle the case where locationText is null
        }
    }

    private void statusbarcolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }
    }


    private void DataRetrive() {
        orgallDataList.clear(); // Clear previous data

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
                .collection("BloodOrganization")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                // Convert the document to BloodDonerRequestModel and add to allDataList
                                BloodOrganizationAddModel orgmodel = document.toObject(BloodOrganizationAddModel.class);
                                orgallDataList.add(orgmodel);
                                originalDataList = new ArrayList<>(orgallDataList);
                                // Log the size of the list
                                Log.d("DataRetrieved", "Size of allDataList: " + orgallDataList.size());

                                // Notify adapter about the data change
                                adapter.notifyDataSetChanged();

                            } else {
                                Log.d("Firestore", "No such document");
                            }

                            if (orgallDataList.isEmpty()){
                                empty_anim.setVisibility(View.VISIBLE);
                            }else {
                                empty_anim.setVisibility(View.GONE);
                            }


                        } else {
                            // Handle task unsuccessful
                            Log.e("Firestore", "Error getting document: ", task.getException());
                        }
                    }
                });


    }


    private void filterData(String query) {
        ArrayList<BloodOrganizationAddModel> filteredList = new ArrayList<>();

        for (BloodOrganizationAddModel model : originalDataList) {
            String address;
            if (isBanglaLanguage()) {
                address = model.getOrgaddress_Ed().toLowerCase();
            } else {
                address = model.getOrgaddress_Ed().toLowerCase();
            }

            if (address.contains(query.toLowerCase())) {
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
}