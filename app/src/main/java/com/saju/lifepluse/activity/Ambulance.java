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
import com.saju.lifepluse.adapter.AmbulanceAdapter;
import com.saju.lifepluse.modelclass.AmbulanceModel;
import com.saju.lifepluse.modelclass.PharmecyModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;

public class Ambulance extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    FirebaseFirestore db;
    AmbulanceAdapter adapter;
    private ArrayList<AmbulanceModel> ambulanceListData;
    EditText searchEditText;
    private ArrayList<AmbulanceModel> originalDataList;
    ImageView button_back;
    LottieAnimationView addambulanceId;
    FirebaseUser currentuser;
    AmbulanceModel ambulanceModel;

    boolean isambulanceadd;
    boolean isambulancenotadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance);


        recyclerView = findViewById(R.id.recyclearViewId);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        searchEditText = findViewById(R.id.searchEditText);
        button_back = findViewById(R.id.button_back);
        addambulanceId = findViewById(R.id.addambulanceId);
        db = FirebaseFirestore.getInstance();
        currentuser = FirebaseAuth.getInstance().getCurrentUser();

        ambulanceListData = new ArrayList<>();


        originalDataList = new ArrayList<>();

        adapter = new AmbulanceAdapter(this, ambulanceListData);  // Initialize adapter
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


        addambulanceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (currentuser != null) {
                    // Show "Add Hospital" dialog if not added, else show "Update Hospital" dialog
                    if (isambulanceadd) {
                        updateAmbulanceDialog();  // Show Update dialog
                    } else {
                        addambulanceDialog();  // Show Add dialog
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

        searchEditText.setHint("Search by ambulance name...");

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

    private void updateAmbulanceDialog() {
        // Update hospital dialog code
        AlertDialog.Builder builder = new AlertDialog.Builder(Ambulance.this);
        builder.setTitle("Update Your Ambulance Info");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_ambulance, null);
        builder.setView(dialogView);

        TextInputEditText editTextEnglish = dialogView.findViewById(R.id.edittext_ambulance_english);
        TextInputEditText editTextBangla = dialogView.findViewById(R.id.edittext_ambulance_bangla);
        TextInputEditText mobileEd = dialogView.findViewById(R.id.edittext_phonenumber);
        TextInputEditText addressEd = dialogView.findViewById(R.id.edittext_address);
        TextInputEditText fblinkEd = dialogView.findViewById(R.id.edittext_fblink);
        TextInputEditText weblinkEd = dialogView.findViewById(R.id.edittext_weblink);
        TextInputEditText twitterEd = dialogView.findViewById(R.id.edittext_twitterlink);
        TextInputEditText youtubelinkEd = dialogView.findViewById(R.id.edittext_youtubelink);


        FirebaseUtil.donerUserDetails("ambulance_list").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    AmbulanceModel ambulanceModel = task.getResult().toObject(AmbulanceModel.class);

                    editTextEnglish.setText(ambulanceModel.getAmbname_eng());
                    editTextBangla.setText(ambulanceModel.getAmbname_bang());
                    mobileEd.setText(ambulanceModel.getAmbmobile());
                    addressEd.setText(ambulanceModel.getAmb_address());
                    fblinkEd.setText(ambulanceModel.getAmb_fblink());
                    weblinkEd.setText(ambulanceModel.getAmb_websitelink());
                    twitterEd.setText(ambulanceModel.getAmb_twitterlink());
                    youtubelinkEd.setText(ambulanceModel.getAmb_youtubelink());
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
                String ambulanceEnglish = editTextEnglish.getText().toString().trim();
                String ambulanceBangla = editTextBangla.getText().toString().trim();
                String ambmobile = mobileEd.getText().toString().trim();
                String ambaddress = addressEd.getText().toString().trim();
                String ambfblink = fblinkEd.getText().toString().trim();
                String ambweblink = weblinkEd.getText().toString().trim();
                String ambtwiiterlink = twitterEd.getText().toString().trim();
                String ambyoutubelink = youtubelinkEd.getText().toString().trim();

                if (!ambulanceEnglish.isEmpty() && !ambulanceBangla.isEmpty() && !ambmobile.isEmpty() && !ambaddress.isEmpty() && !ambfblink.isEmpty()) {
                    updateAmbulance(ambulanceEnglish, ambulanceBangla, ambmobile, ambaddress, ambfblink, ambweblink, ambtwiiterlink, ambyoutubelink);
                    UserDataRetrive();
                    // Update hospital details
                } else {
                    Toast.makeText(Ambulance.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
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

    private void updateAmbulance(String ambname_eng, String ambname_bang, String ambmobile, String ambaddress, String fblink, String weblink, String twitterlink, String youtubelink) {
        // Implement logic to update the hospital in Firestore
        FirebaseUtil.donerUserDetails("ambulance_list").update("ambname_eng", ambname_eng, "ambname_bang", ambname_bang, "ambmobile", ambmobile, "amb_address", ambaddress, "amb_fblink", fblink,"amb_websitelink", weblink,"amb_twitterlink", twitterlink, "amb_youtubelink", youtubelink)
                .addOnSuccessListener(aVoid -> Toast.makeText(Ambulance.this, "Ambulance Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.e("Ambulacne", "Failed to update ambulance", e));
    }

    private void addambulanceDialog() {

        // Create an AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Ambulance.this);
        builder.setTitle("Add Your Pharmecy Info");

        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_ambulance, null);
        builder.setView(dialogView);

        // Get the EditText fields from the custom layout
        TextInputEditText editTextEnglish = dialogView.findViewById(R.id.edittext_ambulance_english);
        TextInputEditText editTextBangla = dialogView.findViewById(R.id.edittext_ambulance_bangla);
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
                String ambulanceEnglish = editTextEnglish.getText().toString().trim();
                String ambulanceBangla = editTextBangla.getText().toString().trim();
                String ambmobile = mobileEd.getText().toString().trim();
                String ambaddress = addressEd.getText().toString().trim();
                String ambfblink = fblinkEd.getText().toString().trim();
                String ambweblink = weblinkEd.getText().toString().trim();
                String ambtwiiterlink = twitterEd.getText().toString().trim();
                String ambyoutubelink = youtubelinkEd.getText().toString().trim();

                // Check if fields are not empty
                if (!ambulanceEnglish.isEmpty() && !ambulanceBangla.isEmpty() && !ambmobile.isEmpty() && !ambaddress.isEmpty() && !ambfblink.isEmpty()) {
                    // Add the hospital to Firestore
                    addAmbulance(ambulanceEnglish, ambulanceBangla, ambmobile, ambaddress, ambfblink, ambweblink, ambtwiiterlink, ambyoutubelink);
                } else {
                    // Show a Toast message for empty fields
                    Toast.makeText(Ambulance.this, "Please fill both fields", Toast.LENGTH_SHORT).show();
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


    private void addAmbulance(String ambname_eng, String ambname_bang, String ambmobile, String amb_address, String amb_fblink, String amb_websitelink, String amb_twitterlink, String amb_youtubelink) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser !=null){
            String addedBy = currentUser.getUid();
            ambulanceModel = new AmbulanceModel(ambname_eng, ambname_bang, ambmobile, amb_address, amb_fblink, amb_websitelink, amb_twitterlink, amb_youtubelink, addedBy);

            FirebaseUtil.donerUserDetails("ambulance_list").set(ambulanceModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    FirebaseUtil.currentUserDetails().update("isambulanceadd", true);
                    FirebaseUtil.currentUserDetails().update("isambulancenotadd", false);

                    Toast.makeText(Ambulance.this, "Ambulance Added Succesffully", Toast.LENGTH_SHORT).show();
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
                        isambulanceadd = documentSnapshot.getBoolean("isambulanceadd");
                        isambulancenotadd = documentSnapshot.getBoolean("isambulancenotadd");

                        if (isambulanceadd) {
                            Log.d("ambulance_status", "Ambulance Form  Filled Yet");

                        } else if (isambulancenotadd) {

                        }
                    } else {
                        Log.d("ambulance_status", "DocumentSnapshot is null");
                    }
                } else {
                    Log.d("ambulance_status", "Error getting document: ", task.getException());

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
        ambulanceListData.clear();
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
        ambulanceListData.clear();  // Clear previous data

        // Access the hospital list collection under the user's document
        db.collection("users").document(userid).collection("ambulance_list")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Loop through each document in the collection
                            for (DocumentSnapshot document : queryDocumentSnapshots) {
                                AmbulanceModel ambulanceModel = document.toObject(AmbulanceModel.class);

                                // Ensure hospital is not null before adding
                                if (ambulanceModel != null) {
                                    ambulanceListData.add(ambulanceModel);
                                    originalDataList.add(ambulanceModel);
                                }
                            }

                            // Notify the adapter about the updated data
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d("AMDataRetrive", "No pharmecy found in the collection");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("AMDataRetrive", "Error fetching data: " + e.getLocalizedMessage());
                    }
                });
    }




    private void filterData(String query) {
        ArrayList<AmbulanceModel> filteredList = new ArrayList<>();

        for (AmbulanceModel model : originalDataList) {
            String ambulanceName;
            if (isBanglaLanguage()) {
                ambulanceName = model.getAmbname_bang().toLowerCase();
            } else {
                ambulanceName = model.getAmbname_eng().toLowerCase();
            }

            if (ambulanceName.contains(query.toLowerCase())) {
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
