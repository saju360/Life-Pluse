package com.saju.lifepluse.activity;

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
import com.saju.lifepluse.modelclass.HospitalModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;

public class Hospital extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;
    HPRecyclearAdapter adapter;
    private ArrayList<HospitalModel> hpitalListData;
    EditText searchEditText;
    private ArrayList<HospitalModel> originalDataList;
    ImageView button_back;
    LottieAnimationView addhospitalId;
    FirebaseUser currentuser;
    HospitalModel hospitalModel;

    boolean ishospitaladd;
    boolean ishospitalnotadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital);


        recyclerView = findViewById(R.id.recyclearViewId);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchEditText = findViewById(R.id.searchEditText);
        button_back = findViewById(R.id.button_back);
        addhospitalId = findViewById(R.id.addhospitalId);
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        hpitalListData = new ArrayList<>();


        originalDataList = new ArrayList<>();

        adapter = new HPRecyclearAdapter(this, hpitalListData);  // Initialize adapter
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


        addhospitalId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (currentuser != null) {
                    // Show "Add Hospital" dialog if not added, else show "Update Hospital" dialog
                    if (ishospitaladd) {
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

    private void updateHospitalDialog() {
        // Update hospital dialog code
        AlertDialog.Builder builder = new AlertDialog.Builder(Hospital.this);
        builder.setTitle("Update Your Hospital Info");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_hospital, null);
        builder.setView(dialogView);

        TextInputEditText editTextEnglish = dialogView.findViewById(R.id.edittext_hospital_english);
        TextInputEditText editTextBangla = dialogView.findViewById(R.id.edittext_hospital_bangla);
        TextInputEditText mobileEd = dialogView.findViewById(R.id.edittext_phonenumber);
        TextInputEditText addressEd = dialogView.findViewById(R.id.edittext_address);
        TextInputEditText fblinkEd = dialogView.findViewById(R.id.edittext_fblink);
        TextInputEditText weblinkEd = dialogView.findViewById(R.id.edittext_weblink);
        TextInputEditText twitterEd = dialogView.findViewById(R.id.edittext_twitterlink);
        TextInputEditText youtubelinkEd = dialogView.findViewById(R.id.edittext_youtubelink);


        FirebaseUtil.donerUserDetails("hospital_list").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    HospitalModel hospitalModel = task.getResult().toObject(HospitalModel.class);

                    editTextEnglish.setText(hospitalModel.getHpname_eng());
                    editTextBangla.setText(hospitalModel.getHpname_bang());
                    mobileEd.setText(hospitalModel.getHpmobile());
                    addressEd.setText(hospitalModel.getHp_address());
                    fblinkEd.setText(hospitalModel.getHp_fblink());
                    weblinkEd.setText(hospitalModel.getHp_websitelink());
                    twitterEd.setText(hospitalModel.getHp_twitterlink());
                    youtubelinkEd.setText(hospitalModel.getHp_youtubelink());
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
                String hospitalEnglish = editTextEnglish.getText().toString().trim();
                String hospitalBangla = editTextBangla.getText().toString().trim();
                String hpmobile = mobileEd.getText().toString().trim();
                String hpaddress = addressEd.getText().toString().trim();
                String hpfblink = fblinkEd.getText().toString().trim();
                String hpweblink = weblinkEd.getText().toString().trim();
                String hptwiiterlink = twitterEd.getText().toString().trim();
                String hpyoutubelink = youtubelinkEd.getText().toString().trim();

                if (!hospitalEnglish.isEmpty() && !hospitalBangla.isEmpty() && !hpmobile.isEmpty() && !hpfblink.isEmpty()) {
                    updateHospital(hospitalEnglish, hospitalBangla, hpmobile, hpaddress, hpfblink, hpweblink, hptwiiterlink, hpyoutubelink);
                    UserDataRetrive();
                    // Update hospital details
                } else {
                    Toast.makeText(Hospital.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
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

    private void updateHospital(String hpname_eng, String hpname_bang, String hpmobile, String hpaddress, String fblink, String weblink, String twitterlink, String youtubelink) {
        // Implement logic to update the hospital in Firestore
        FirebaseUtil.donerUserDetails("hospital_list").update("hpname_eng", hpname_eng, "hpname_bang", hpname_bang, "hpmobile", hpmobile, "hp_address", hpaddress, "hp_fblink", fblink,"hp_websitelink", weblink,"hp_twitterlink", twitterlink, "hp_youtubelink", youtubelink)
                .addOnSuccessListener(aVoid -> Toast.makeText(Hospital.this, "Hospital Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Hospital", "Failed to update hospital", e));
    }

    private void addhospitalDialog() {

        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Hospital.this);
        builder.setTitle("Add Your Hospital");

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_hospital, null);
        builder.setView(dialogView);

        // Get the EditText fields from the custom layout
        TextInputEditText editTextEnglish = dialogView.findViewById(R.id.edittext_hospital_english);
        TextInputEditText editTextBangla = dialogView.findViewById(R.id.edittext_hospital_bangla);
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
                String hospitalEnglish = editTextEnglish.getText().toString().trim();
                String hospitalBangla = editTextBangla.getText().toString().trim();
                String hospitalmobile = mobileEd.getText().toString().trim();
                String hospitaladdress = addressEd.getText().toString().trim();
                String hospitalfblink = fblinkEd.getText().toString().trim();
                String hospitalwebsitelink = weblinkEd.getText().toString().trim();
                String hospitaltwitterlink = twitterEd.getText().toString().trim();
                String hospitalyoutubelink = youtubelinkEd.getText().toString().trim();

                // Check if fields are not empty
                if (!hospitalEnglish.isEmpty() && !hospitalBangla.isEmpty() && !hospitalmobile.isEmpty() && !hospitaladdress.isEmpty() && !hospitalfblink.isEmpty()) {
                    // Add the hospital to Firestore
                    addHospital(hospitalEnglish, hospitalBangla, hospitalmobile, hospitaladdress, hospitalfblink, hospitalwebsitelink, hospitaltwitterlink, hospitalyoutubelink);
                } else {
                    // Show a Toast message for empty fields
                    Toast.makeText(Hospital.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
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


    private void addHospital(String hpname_eng, String hpname_bang, String hpmobile, String hp_address, String hp_fblink, String hp_websitelink, String hp_twitterlink, String hp_youtubelink) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser !=null){
            String addedBy = currentUser.getUid();
            hospitalModel = new HospitalModel(hpname_eng, hpname_bang, hpmobile, hp_address, hp_fblink, hp_websitelink, hp_twitterlink, hp_youtubelink, addedBy);

            FirebaseUtil.donerUserDetails("hospital_list").set(hospitalModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    FirebaseUtil.currentUserDetails().update("ishospitaladd", true);
                    FirebaseUtil.currentUserDetails().update("ishospitalnotadd", false);

                    Toast.makeText(Hospital.this, "Hospital Added Succesffully", Toast.LENGTH_SHORT).show();
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
                        ishospitaladd = documentSnapshot.getBoolean("ishospitaladd");
                        ishospitalnotadd = documentSnapshot.getBoolean("ishospitalnotadd");

                        if (ishospitaladd) {
                            Log.d("hospitalform_status", "Hospital Form  Filled Yet");

                        } else if (ishospitalnotadd) {

                        }
                    } else {
                        Log.d("hospitalform_status", "DocumentSnapshot is null");
                    }
                } else {
                    Log.d("hospitalform_status", "Error getting document: ", task.getException());

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
        hpitalListData.clear();
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
        hpitalListData.clear();  // Clear previous data

        // Access the hospital list collection under the user's document
        db.collection("users").document(userid).collection("hospital_list")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Loop through each document in the collection
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                HospitalModel hospital = document.toObject(HospitalModel.class);

                                // Ensure hospital is not null before adding
                                if (hospital != null) {
                                    hpitalListData.add(hospital);
                                    originalDataList.add(hospital);
                                }
                            }

                            // Notify the adapter about the updated data
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("HPDataRetrive", "No hospitals found in the collection");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("HPDataRetrive", "Error fetching data: " + e.getLocalizedMessage());
                    }
                });
    }




    private void filterData(String query) {
        ArrayList<HospitalModel> filteredList = new ArrayList<>();

        for (HospitalModel model : originalDataList) {
            String hospitalName;
            if (isBanglaLanguage()) {
                hospitalName = model.getHpname_bang().toLowerCase();
            } else {
                hospitalName = model.getHpname_eng().toLowerCase();
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
