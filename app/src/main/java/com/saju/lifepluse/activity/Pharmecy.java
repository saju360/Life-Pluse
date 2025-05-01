package com.saju.lifepluse.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.adapter.HPRecyclearAdapter;
import com.saju.lifepluse.adapter.PharmecyAdapter;
import com.saju.lifepluse.modelclass.HospitalModel;
import com.saju.lifepluse.modelclass.PharmecyModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;

public class Pharmecy extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;
    PharmecyAdapter adapter;
    private ArrayList<PharmecyModel> pharmecyListData;
    EditText searchEditText;
    private ArrayList<PharmecyModel> originalDataList;
    ImageView button_back;
    LottieAnimationView addpharmecyId;
    FirebaseUser currentuser;
    PharmecyModel pharmecyModel;

    boolean ispharmacyadd;
    boolean ispharmacynotadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmecy);


        recyclerView = findViewById(R.id.recyclearViewId);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchEditText = findViewById(R.id.searchEditText);
        button_back = findViewById(R.id.button_back);
        addpharmecyId = findViewById(R.id.addpharmecyId);
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        pharmecyListData = new ArrayList<>();


        originalDataList = new ArrayList<>();

        adapter = new PharmecyAdapter(this, pharmecyListData);  // Initialize adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);  // Set adapter to RecyclerView



        UserDataRetrive();
        userAuth();

        statusbar();

        searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Call your data retrieval method here
            UserDataRetrive();
            swipeRefreshLayout.setRefreshing(false); // Call this when the refresh is complete
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);


        addpharmecyId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (currentuser != null) {
                    // Show "Add Hospital" dialog if not added, else show "Update Hospital" dialog
                    if (ispharmacyadd) {
                        updateHospitalDialog();  // Show Update dialog
                    } else {
                        addhospitalDialog();  // Show Add dialog
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please SignIn First", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), SignIn.class));
                }

            }
        });


        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DrawerLayout.class));
            }
        });

        searchEditText.setHint("Search by pharmecy name...");

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

    private void updateHospitalDialog() {
        // Update hospital dialog code
        AlertDialog.Builder builder = new AlertDialog.Builder(Pharmecy.this);
        builder.setTitle("Update Your Pharmecy Info");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_pharmecy, null);
        builder.setView(dialogView);

        TextInputEditText editTextEnglish = dialogView.findViewById(R.id.edittext_pharmecy_english);
        TextInputEditText editTextBangla = dialogView.findViewById(R.id.edittext_pharmecy_bangla);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText regNoId = dialogView.findViewById(R.id.edittext_pharmecy_regNoId);
        TextInputEditText mobileEd = dialogView.findViewById(R.id.edittext_phonenumber);
        TextInputEditText addressEd = dialogView.findViewById(R.id.edittext_address);
        TextInputEditText fblinkEd = dialogView.findViewById(R.id.edittext_fblink);
        TextInputEditText weblinkEd = dialogView.findViewById(R.id.edittext_weblink);
        TextInputEditText twitterEd = dialogView.findViewById(R.id.edittext_twitterlink);
        TextInputEditText youtubelinkEd = dialogView.findViewById(R.id.edittext_youtubelink);


        FirebaseUtil.donerUserDetails("pharmacy_list").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    PharmecyModel pharmecyModel = task.getResult().toObject(PharmecyModel.class);

                    editTextEnglish.setText(pharmecyModel.getPhname_eng());
                    editTextBangla.setText(pharmecyModel.getPhname_bang());
                    regNoId.setText(pharmecyModel.getPhregno());
                    mobileEd.setText(pharmecyModel.getPhmobile());
                    addressEd.setText(pharmecyModel.getPh_address());
                    fblinkEd.setText(pharmecyModel.getPh_fblink());
                    weblinkEd.setText(pharmecyModel.getPh_websitelink());
                    twitterEd.setText(pharmecyModel.getPh_twitterlink());
                    youtubelinkEd.setText(pharmecyModel.getPh_youtubelink());
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pharmecyEnglish = editTextEnglish.getText().toString().trim();
                String pharmecyBangla = editTextBangla.getText().toString().trim();
                String phregno = regNoId.getText().toString().trim();
                String phmobile = mobileEd.getText().toString().trim();
                String phaddress = addressEd.getText().toString().trim();
                String phfblink = fblinkEd.getText().toString().trim();
                String phweblink = weblinkEd.getText().toString().trim();
                String phtwiiterlink = twitterEd.getText().toString().trim();
                String phyoutubelink = youtubelinkEd.getText().toString().trim();

                if (!pharmecyEnglish.isEmpty() && !pharmecyBangla.isEmpty() && !phregno.isEmpty() && !phmobile.isEmpty() && !phfblink.isEmpty()) {
                    updateHospital(pharmecyEnglish, pharmecyBangla, phregno, phmobile, phaddress, phfblink, phweblink, phtwiiterlink, phyoutubelink);
                    UserDataRetrive();
                    // Update hospital details
                } else {
                    Toast.makeText(Pharmecy.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void updateHospital(String phname_eng, String phname_bang, String phregno, String phmobile, String phaddress, String fblink, String weblink, String twitterlink, String youtubelink) {
        // Implement logic to update the hospital in Firestore
        FirebaseUtil.donerUserDetails("hopital_list").update("phname_eng", phname_eng, "phregno", phregno, "phname_bang", phname_bang, "phmobile", phmobile, "ph_address", phaddress, "ph_fblink", fblink,"ph_websitelink", weblink,"ph_twitterlink", twitterlink, "ph_youtubelink", youtubelink)
                .addOnSuccessListener(aVoid -> Toast.makeText(Pharmecy.this, "Hospital Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Hospital", "Failed to update hospital", e));
    }

    private void addhospitalDialog() {

        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Pharmecy.this);
        builder.setTitle("Add Your Pharmecy Info");

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_pharmecy, null);
        builder.setView(dialogView);

        // Get the EditText fields from the custom layout
        TextInputEditText editTextEnglish = dialogView.findViewById(R.id.edittext_pharmecy_english);
        TextInputEditText editTextBangla = dialogView.findViewById(R.id.edittext_pharmecy_bangla);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextInputEditText regNoId = dialogView.findViewById(R.id.edittext_pharmecy_regNoId);
        TextInputEditText mobileEd = dialogView.findViewById(R.id.edittext_phonenumber);
        TextInputEditText addressEd = dialogView.findViewById(R.id.edittext_address);
        TextInputEditText fblinkEd = dialogView.findViewById(R.id.edittext_fblink);
        TextInputEditText weblinkEd = dialogView.findViewById(R.id.edittext_weblink);
        TextInputEditText twitterEd = dialogView.findViewById(R.id.edittext_twitterlink);
        TextInputEditText youtubelinkEd = dialogView.findViewById(R.id.edittext_youtubelink);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve input values from the EditText fields
                String pharmecyEnglish = editTextEnglish.getText().toString().trim();
                String pharmecyBangla = editTextBangla.getText().toString().trim();
                String phregno = regNoId.getText().toString().trim();
                String phmobile = mobileEd.getText().toString().trim();
                String phaddress = addressEd.getText().toString().trim();
                String phfblink = fblinkEd.getText().toString().trim();
                String phweblink = weblinkEd.getText().toString().trim();
                String phtwiiterlink = twitterEd.getText().toString().trim();
                String phyoutubelink = youtubelinkEd.getText().toString().trim();

                // Check if fields are not empty
                if (!pharmecyEnglish.isEmpty() && !pharmecyBangla.isEmpty() && !phregno.isEmpty() && !phmobile.isEmpty() && !phaddress.isEmpty() && !phfblink.isEmpty()) {
                    // Add the hospital to Firestore
                    addHospital(pharmecyEnglish, pharmecyBangla, phregno, phmobile, phaddress, phfblink, phweblink, phtwiiterlink, phyoutubelink);
                } else {
                    // Show a Toast message for empty fields
                    Toast.makeText(Pharmecy.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.create().show();
    }


    private void addHospital(String phname_eng, String phname_bang, String phregno, String phmobile, String ph_address, String ph_fblink, String ph_websitelink, String ph_twitterlink, String ph_youtubelink) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser !=null){
            String addedBy = currentUser.getUid();
            pharmecyModel = new PharmecyModel(phname_eng, phname_bang, phregno, phmobile, ph_address, ph_fblink, ph_websitelink, ph_twitterlink, ph_youtubelink, addedBy);

            FirebaseUtil.donerUserDetails("pharmacy_list").set(pharmecyModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    FirebaseUtil.currentUserDetails().update("ispharmacyadd", true);
                    FirebaseUtil.currentUserDetails().update("ispharmacynotadd", false);

                    Toast.makeText(Pharmecy.this, "Pharmecy Added Succesffully", Toast.LENGTH_SHORT).show();
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }




    }

    private void userAuth() {

        if (currentuser != null) {
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
                        ispharmacyadd = documentSnapshot.getBoolean("ispharmacyadd");
                        ispharmacynotadd = documentSnapshot.getBoolean("ispharmacynotadd");

                        if (ispharmacyadd) {
                            Log.d("pharmecyform_status", "Pharmecy Form  Filled Yet");

                        } else if (ispharmacynotadd) {

                        }
                    } else {
                        Log.d("pharmecy_status", "DocumentSnapshot is null");
                    }
                } else {
                    Log.d("pharmecy_status", "Error getting document: ", task.getException());

                }


            }
        });

    }

    private void statusbar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Change the status bar color programmatically
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar));
        }
    }

    private void UserDataRetrive() {
        pharmecyListData.clear();
        originalDataList = new ArrayList<>();  // Ensure it's initialized
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userId = document.getId();
                                Log.d("userId", userId);
                                HPDataRetrive(userId);
                            }
                        } else {
                            Log.e("Firestore", "Error getting documents: ", task.getException());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void HPDataRetrive(String userid) {
        pharmecyListData.clear();  // Clear previous data

        // Access the hospital list collection under the user's document
        db.collection("users").document(userid).collection("pharmacy_list")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Loop through each document in the collection
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                PharmecyModel pharmecy = document.toObject(PharmecyModel.class);

                                // Ensure hospital is not null before adding
                                if (pharmecy != null) {
                                    pharmecyListData.add(pharmecy);
                                    originalDataList.add(pharmecy);
                                }
                            }

                            // Notify the adapter about the updated data
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("PHDataRetrive", "No pharmecy found in the collection");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("PHDataRetrive", "Error fetching data: " + e.getLocalizedMessage());
                    }
                });
    }




    private void filterData(String query) {
        ArrayList<PharmecyModel> filteredList = new ArrayList<>();

        for (PharmecyModel model : originalDataList) {
            String pharmecyName;
            if (isBanglaLanguage()) {
                pharmecyName = model.getPhname_bang().toLowerCase();
            } else {
                pharmecyName = model.getPhname_eng().toLowerCase();
            }

            if (pharmecyName.contains(query.toLowerCase())) {
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
