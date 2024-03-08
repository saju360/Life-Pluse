package com.saju.lifepluse.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;
import com.saju.lifepluse.fragment.BloodNeed_Fragment;
import com.saju.lifepluse.modelclass.BloodDonationPostModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BloodNeed_Post extends AppCompatActivity {

    TextInputEditText manualDivisionEditText, bloodNeedEditText, dateEditText, bloodQtyEditText, hospitalEditText, referenceEditText, contactEditText;
    Button postNowButton, cancelButton;
    ImageView back_button;
    AutoCompleteTextView autoDivison, selectbloodType;
    TextInputLayout manualDivisionTextInputLayout;
    String[] divisionValues;
    String[] bloodGroup;
    String selectedItem;
    ProgressBar progressBar;
    TextView locationTextView;
    FirebaseFirestore db;
    String bloodNeed, bloodgroup, date, bloodQty, hospital, division, manualDivision, reference, contact, documentId;


    private static final String PREF_NAME = "BloodDonationPrefs";
    private static final String KEY_BLOOD_NEED = "bloodNeed";
    private static final String KEY_BLOOD_QTY = "bloodQty";
    private static final String KEY_HOSPITAL = "hospital";

    private static final String KEY_MANUAL_DIVISION = "manualDivision";
    private static final String KEY_REFERENCE = "reference";
    private static final String KEY_CONTACT = "contact";

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_need_post);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }

        initializeViews();
        setListeners();
        initializeFirestore();
        locationget();

        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);


        // Retrieve data from SharedPreferences
        String savedBloodNeed = sharedPreferences.getString(KEY_BLOOD_NEED, "");
        String savedBloodQty = sharedPreferences.getString(KEY_BLOOD_QTY, "");
        String savedHospital = sharedPreferences.getString(KEY_HOSPITAL, "");
        String savedManualDivision = sharedPreferences.getString(KEY_MANUAL_DIVISION, "");
        String savedReference = sharedPreferences.getString(KEY_REFERENCE, "");
        String savedContact = sharedPreferences.getString(KEY_CONTACT, "");

        // Initialize views and set their values to saved data
        bloodNeedEditText.setText(savedBloodNeed);
        bloodQtyEditText.setText(savedBloodQty);
        hospitalEditText.setText(savedHospital);
        manualDivisionEditText.setText(savedManualDivision);
        referenceEditText.setText(savedReference);
        contactEditText.setText(savedContact);


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BloodNeed_Post.this, BloodBank.class));
                finish();
            }
        });

    }

    private void locationget() {

        if (DrawerLayout.locationText != null) {

            locationTextView.setText(DrawerLayout.locationText);
        } else {
            locationTextView.setText("Location not available");
        }
    }


    private void initializeViews() {
        bloodNeedEditText = findViewById(R.id.bloodNeedTextInputLayout);
        dateEditText = findViewById(R.id.dateTextInputLayout);
        bloodQtyEditText = findViewById(R.id.bloodqtyId);
        hospitalEditText = findViewById(R.id.hospitalNameId);
        referenceEditText =findViewById(R.id.referenceNameId);
        contactEditText = findViewById(R.id.contactId);
        postNowButton = findViewById(R.id.postNowButton);
        cancelButton =findViewById(R.id.cancelButton);
        manualDivisionTextInputLayout = findViewById(R.id.manualDivisionTextInputLayout);
        manualDivisionEditText = findViewById(R.id.manualDivisionEditText);
        autoDivison = findViewById(R.id.selectdivision);
        selectbloodType = findViewById(R.id.selectbloodType);
        locationTextView = findViewById(R.id.locationTextView);
        progressBar = findViewById(R.id.progressBar);
        back_button = findViewById(R.id.button_back);

        divisionValues = getResources().getStringArray(R.array.division_array);
        bloodGroup = getResources().getStringArray(R.array.bloodGroup);
    }

    private void setListeners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, divisionValues);
        autoDivison.setAdapter(adapter);

        ArrayAdapter<String> bloodGroupadapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, bloodGroup);
        selectbloodType.setAdapter(bloodGroupadapter);

        autoDivison.setOnItemClickListener((parent, view, position, id) -> {
            selectedItem = ((TextView) view).getText().toString();
            if (selectedItem.equals("Other") || selectedItem.equals("অন্যন্য")) {
                manualDivisionTextInputLayout.setVisibility(View.VISIBLE);
            } else {
                manualDivisionTextInputLayout.setVisibility(View.GONE);
            }
        });


        dateEditText.setOnClickListener(v -> datepicker());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BloodNeed_Post.this, BloodBank.class));
                finish();
            }
        });

        postNowButton.setOnClickListener(v -> validateAndPost());
    }

    private void initializeFirestore() {
        db = FirebaseFirestore.getInstance();
    }

    private void validateAndPost() {
        bloodNeed = bloodNeedEditText.getText().toString();
        bloodgroup = selectbloodType.getText().toString();
        date = dateEditText.getText().toString();
        bloodQty = bloodQtyEditText.getText().toString();
        hospital = hospitalEditText.getText().toString();
        division = autoDivison.getText().toString();
        manualDivision = manualDivisionEditText.getText().toString();
        reference = referenceEditText.getText().toString();
        contact = contactEditText.getText().toString();

        if (TextUtils.isEmpty(bloodNeed) || TextUtils.isEmpty(bloodgroup) || TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(bloodQty) || TextUtils.isEmpty(hospital) || TextUtils.isEmpty(division) ||
                (selectedItem != null && (selectedItem.equals("Other") || selectedItem.equals("অন্যন্য")) && TextUtils.isEmpty(manualDivision)) ||
                TextUtils.isEmpty(contact)) {



            Toast.makeText(getApplicationContext(), "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_BLOOD_NEED, bloodNeed);
        editor.putString(KEY_BLOOD_QTY, bloodQty);
        editor.putString(KEY_HOSPITAL, hospital);
        editor.putString(KEY_MANUAL_DIVISION, manualDivision);
        editor.putString(KEY_REFERENCE, reference);
        editor.putString(KEY_CONTACT, contact);
        editor.apply();

        // Get the current time
        long currentTimeMillis = System.currentTimeMillis();

        // Convert the current time to a readable format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = dateFormat.format(calendar.getTime());

        BloodDonationPostModel bloodPost = new BloodDonationPostModel(bloodNeed, bloodgroup, date, bloodQty, hospital, division, manualDivision, reference, contact, currentTime, documentId);

        progressBar.setVisibility(View.VISIBLE);

        db.collection("Blood Need Post")
                .add(bloodPost)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Retrieve the ID of the newly added document
                            progressBar.setVisibility(View.GONE);
                            documentId = task.getResult().getId();

                            // Update the bloodPost object with the documentId
                            bloodPost.setDocumentId(documentId);

                            // Update the document in Firestore to include the documentId
                            updateDocumentInFirestore(task.getResult(), bloodPost);

                            Toast.makeText(getApplicationContext(), "Post Added Successfully", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(BloodNeed_Post.this, BloodBank.class));
                            finish();


                        } else {
                            Toast.makeText(getApplicationContext(), "Error! Please try again later", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore Error", "Error writing document", task.getException());
                        }
                    }
                });
    }

    private void updateDocumentInFirestore(DocumentReference documentReference, BloodDonationPostModel bloodPost) {
        documentReference.update("documentId", bloodPost.getDocumentId())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firestore", "Document updated with documentId");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error updating document with documentId", e);
                    }
                });

    }

    private void datepicker() {
        // Set up DatePicker to show only current date and future dates
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            // Check if the selected date is before the current date
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            Calendar currentCalendar = Calendar.getInstance();
            if (selectedCalendar.before(currentCalendar)) {
                // Show a toast indicating that the selected date is invalid
                Toast.makeText(BloodNeed_Post.this, "Please select a date from today onwards", Toast.LENGTH_SHORT).show();
            } else {
                // Set the selected date in the EditText
                dateEditText.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month + 1, year));
            }
        };

        // Initialize DatePickerDialog with current date and dateSetListener
        DatePickerDialog datePickerDialog = new DatePickerDialog(BloodNeed_Post.this, dateSetListener, currentYear, currentMonth, currentDay);

        // Set minimum date to current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Show DatePickerDialog
        dateEditText.setOnClickListener(v -> datePickerDialog.show());
    }





}