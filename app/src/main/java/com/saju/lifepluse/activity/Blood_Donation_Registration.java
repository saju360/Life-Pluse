package com.saju.lifepluse.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.fragment.BloodNeed_Fragment;
import com.saju.lifepluse.fragment.DashBoard;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;
import com.saju.lifepluse.modelclass.BloodOrganizationAddModel;
import com.saju.lifepluse.modelclass.DoctorListModel;
import com.saju.lifepluse.modelclass.EmailUser;
import com.saju.lifepluse.modelclass.PhoneAuthModel;
import com.saju.lifepluse.utils.AndroidUtil;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Blood_Donation_Registration extends AppCompatActivity {


    LinearLayout inreview_layout;
    ImageView review_icon;
    TextView timeTv, statusTv, resonTv;
    EditText nameEd, addressEd, identityEd, mobileEd;
    TextInputEditText dateEditText, manualOrgEditText, lastDonatedateTextEd;
    TextInputLayout manualOrgTextInputLayout, lastDonateLayout;
    AutoCompleteTextView Divison, selectCountry, selectorg;
    MaterialCardView doner_register_form;
    MaterialCardView maleCardview, femaleCardview, o_positiveCardview, o_negativeCardview, a_positiveCardview, a_negativeCardview, b_positiveCardview, b_negativeCardview, ab_positiveCardview, ab_negativeCardview, unknown_Cardview;
    MaterialCardView signle_cardview, married_cardview, divorced_cardview, widowed_cardview, nationalCardview, passportCardview, yesCardview, noCardview;
    MaterialButton registerBtn, againRegisterBtn;
    ImageView button_back;
    ProgressBar progressbar;
    TextView maleTextview, femaleTextview, o_positiveTextview, o_negativeTextview, a_positiveTextview, a_negativeTextview, b_positiveTextview, b_negativeTextview, ab_positiveTextview, ab_negativeTextview, unknown_textview;

    TextView single_textview, married_textview, widowd_textview, divorced_textview, national_textview, passport_textview, yes_textview, no_textview;
    TextView locationTextView;
    String selectedGender = "";
    String bloodType = "";
    String maritalStatus = "";
    String identificationType = "";
    String firstDonate = "";
    String[] divisionValues;
    String[] countryList;
    String selectedorg;
    String lastdonatedate;
    PhoneAuthModel phoneAuthModel;
    EmailUser emailUser;
    String uid;
    BloodDonerRequestModel bloodDonerRequestModel;

    private FirebaseFirestore db;
    private ArrayList<BloodOrganizationAddModel> orgallDataList;
    ArrayList <String> orglist;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_donation_registration);

        Init();
        locationget();
        userDataInpute();
        statusbarcolor();
        getName();

        chek_form_filled();


        divisionValues = getResources().getStringArray(R.array.division_data);
        countryList = getResources().getStringArray(R.array.countryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_item, divisionValues);
        Divison.setAdapter(adapter);

        ArrayAdapter<String> countryadapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_item, countryList);
        selectCountry.setAdapter(countryadapter);

        ArrayAdapter<String> orgadapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_item, getOrglist());
        selectorg.setAdapter(orgadapter);



        registerBtn.setOnClickListener(v -> register());
        lastDonatedateTextEd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastdonatedatepicker();
            }
        });


        selectorg.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedorg = ((TextView) view).getText().toString();
                if (selectedorg.equals("Other") || selectedorg.equals("অন্যন্য")) {
                    manualOrgTextInputLayout.setVisibility(View.VISIBLE);
                } else {
                    manualOrgTextInputLayout.setVisibility(View.GONE);
                }
            }
        });


    }


    private void chek_form_filled() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    boolean isFormFilled = documentSnapshot.getBoolean("isform_filled");
                    boolean isFormnotFilled = documentSnapshot.getBoolean("isform_notfilled");
                    if (isFormFilled) {
                        Log.d("form_status", "form filled");
                        form_approval();

                    } else if (isFormnotFilled) {

                        doner_register_form.setVisibility(View.VISIBLE);
                        inreview_layout.setVisibility(View.GONE);
                        Log.d("form_status", "form not filled");
                        DataRetrive();
                    }
                }
            }
        });
    }


    private void form_approval() {
// Check if the user has already filled the form
        FirebaseUtil.donerUserDetails("bloodDoner").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    // User form exists, check its status
                    boolean isApproved = document.getBoolean("approved");
                    boolean isDeclined = document.getBoolean("declined");
                    if (isApproved) {
                        // Form has been approved, proceed to another activity
                        doner_register_form.setVisibility(View.GONE);
                        inreview_layout.setVisibility(View.GONE);
                        againRegisterBtn.setVisibility(View.GONE);

                        startActivity(new Intent(Blood_Donation_Registration.this, Blood_NeedPost_Doner_Action.class ));
                        finish();

                        Log.d("approval", "approve");
                        // Start another activity
                    } else if (isDeclined) {
                        // Form has been declined, inform the user and prevent them from filling it again
                        // Show a message to the user
                        doner_register_form.setVisibility(View.GONE);
                        inreview_layout.setVisibility(View.VISIBLE);
                        review_icon.setImageResource(R.drawable.reject_icon);
                        timeTv.setText("Request Submited: " + AndroidUtil.timestampToString(document.getTimestamp("createdtime")));
                        statusTv.setText("Your Blood Doner Application is rejected by Admin.");
                        resonTv.setText("Reason of Rejection: Your Application Not Match with our Criteria, Please Contact With Admin.");
                        againRegisterBtn.setVisibility(View.VISIBLE);
                        Log.d("approval", "decline");
                    } else {
                        // Form is still under review, inform the user
                        doner_register_form.setVisibility(View.GONE);
                        inreview_layout.setVisibility(View.VISIBLE);
                        timeTv.setText("Request Submited: " + AndroidUtil.timestampToString(document.getTimestamp("createdtime")));
                        Toast.makeText(Blood_Donation_Registration.this, "Form is still under review", Toast.LENGTH_SHORT).show();
                        // Show the form to the user
                    }

                } else {
                    // Error accessing Firestore
                    Log.d("formadd", "Error getting user form data: ", task.getException());
                }
            }
        });

    }

    private void register() {

        String name = nameEd.getText().toString().trim();
        String address = addressEd.getText().toString().trim();
        String identity = identityEd.getText().toString().trim();
        String mobile = mobileEd.getText().toString().trim();
        String dateOfBirth = dateEditText.getText().toString().trim();
        String district = Divison.getText().toString().trim();
        String nationality = selectCountry.getText().toString().trim();
        String selectedorg = selectorg.getText().toString().trim();
        String selectedmanualorg = manualOrgEditText.getText().toString().trim();


        // Retrieve selected gender
        String gender = selectedGender;

        // Retrieve selected blood type
        String BloodType = bloodType;

        // Retrieve selected marital status
        String MaritalStatus = maritalStatus;

        // Retrieve selected identification type
        String IdentificationType = identificationType;

        // Retrieve selected donate type
        String DonateType = firstDonate;


        if (name.isEmpty() ||
                gender.isEmpty() ||
                dateOfBirth.isEmpty() ||
                BloodType.isEmpty() ||
                MaritalStatus.isEmpty() ||
                identity.isEmpty() ||
                mobile.isEmpty() ||
                district.isEmpty() ||
                nationality.isEmpty() ||
                DonateType.isEmpty() ||
                IdentificationType.isEmpty()) {

            Toast.makeText(Blood_Donation_Registration.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }


        submitForm(name, address, identity, mobile, dateOfBirth, district, nationality, gender, BloodType, MaritalStatus, IdentificationType, selectedorg, selectedmanualorg, DonateType);

    }

    private void Init() {

        db = FirebaseFirestore.getInstance();
        orglist = new ArrayList<>();

        button_back = findViewById(R.id.button_back);
        lastDonateLayout = findViewById(R.id.lastDonateLayout);
        lastDonatedateTextEd = findViewById(R.id.lastDonatedateTextEd);
        manualOrgTextInputLayout = findViewById(R.id.manualOrgTextInputLayout);
        manualOrgEditText = findViewById(R.id.manualOrgEditText);
        selectorg = findViewById(R.id.selectorg);
        maleCardview = findViewById(R.id.maleCardview);
        femaleCardview = findViewById(R.id.femaleCardview);
        maleTextview = findViewById(R.id.maleTextview);
        femaleTextview = findViewById(R.id.femaleTextview);
        o_positiveCardview = findViewById(R.id.o_positiveCardview);
        o_negativeCardview = findViewById(R.id.o_negativeCardview);
        a_positiveCardview = findViewById(R.id.a_positiveCardview);
        a_negativeCardview = findViewById(R.id.a_negativeCardview);
        b_positiveCardview = findViewById(R.id.b_positiveCardview);
        b_negativeCardview = findViewById(R.id.b_negativeCardview);
        ab_positiveCardview = findViewById(R.id.ab_positiveCardview);
        ab_negativeCardview = findViewById(R.id.ab_negativeCardview);
        unknown_Cardview = findViewById(R.id.unknown_Cardview);
        o_positiveTextview = findViewById(R.id.o_positiveTextview);
        o_negativeTextview = findViewById(R.id.o_negativeTextview);
        a_positiveTextview = findViewById(R.id.a_positiveTextview);
        a_negativeTextview = findViewById(R.id.a_negativeTextview);
        b_positiveTextview = findViewById(R.id.b_positiveTextview);
        b_negativeTextview = findViewById(R.id.b_negativeTextview);
        ab_positiveTextview = findViewById(R.id.ab_positiveTextview);
        ab_negativeTextview = findViewById(R.id.ab_negativeTextview);
        unknown_textview = findViewById(R.id.unknown_textview);
        signle_cardview = findViewById(R.id.single_cardview);
        married_cardview = findViewById(R.id.married_cardview);
        widowed_cardview = findViewById(R.id.widowed_cardview);
        divorced_cardview = findViewById(R.id.divorced_cardview);
        single_textview = findViewById(R.id.single_textview);
        married_textview = findViewById(R.id.married_textview);
        divorced_textview = findViewById(R.id.divoreced_textview);
        widowd_textview = findViewById(R.id.widowed_textview);
        nationalCardview = findViewById(R.id.nationalid_cardview);
        passportCardview = findViewById(R.id.passport_cardview);
        yesCardview = findViewById(R.id.yesCardview);
        noCardview = findViewById(R.id.no_cardview);
        national_textview = findViewById(R.id.nationalId_textview);
        passport_textview = findViewById(R.id.passport_textview);
        yes_textview = findViewById(R.id.yesTextview);
        no_textview = findViewById(R.id.no_textview);
        registerBtn = findViewById(R.id.registerBtn);
        progressbar = findViewById(R.id.progressbar);
        nameEd = findViewById(R.id.nameEd);
        addressEd = findViewById(R.id.addressEd);
        identityEd = findViewById(R.id.identityEd);
        mobileEd = findViewById(R.id.mobileEd);
        dateEditText = findViewById(R.id.dateTextEd);
        Divison = findViewById(R.id.selectdivision);
        selectCountry = findViewById(R.id.selectcountry);
        doner_register_form = findViewById(R.id.doner_register_form);
        inreview_layout = findViewById(R.id.inreview_layout);
        review_icon = findViewById(R.id.review_icon);
        timeTv = findViewById(R.id.time_tv);
        statusTv = findViewById(R.id.status_tv);
        resonTv = findViewById(R.id.reason_tv);
        locationTextView = findViewById(R.id.locationTextView);
        button_back = findViewById(R.id.button_back);
        againRegisterBtn = findViewById(R.id.againRegisterBtn);
        orgallDataList = new ArrayList<>();
        


    }

    private void userDataInpute() {

        dateEditText.setOnClickListener(v -> datepicker());
        maleCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Male";
                setSelectedGender();
            }
        });

        femaleCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedGender = "Female";
                setSelectedGender();
            }
        });

        o_positiveCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "O Positive";
                setSelectedBloodType();
            }
        });

        o_negativeCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "O Negative";
                setSelectedBloodType();
            }
        });

        a_positiveCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "A Positive";
                setSelectedBloodType();
            }
        });

        a_negativeCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "A Negative";
                setSelectedBloodType();
            }
        });

        b_positiveCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "B Positive";
                setSelectedBloodType();
            }
        });

        b_negativeCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "B Negative";
                setSelectedBloodType();
            }
        });

        ab_positiveCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "AB Positive";
                setSelectedBloodType();
            }
        });

        ab_negativeCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "AB Negative";
                setSelectedBloodType();
            }
        });
        unknown_Cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bloodType = "Unknown";
                setSelectedBloodType();
            }
        });

        signle_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maritalStatus = "Single";
                setSelectedMaritalStatus();
            }
        });

        married_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maritalStatus = "Married";
                setSelectedMaritalStatus();
            }
        });

        divorced_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maritalStatus = "Divorced";
                setSelectedMaritalStatus();
            }
        });

        widowed_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maritalStatus = "Widowed";
                setSelectedMaritalStatus();
            }
        });

        nationalCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationType = "National";
                setSelectedIdentificationType();
            }
        });

        passportCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationType = "Passport";
                setSelectedIdentificationType();
            }
        });

        yesCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstDonate = "First time donate";
                setSelectedDonateType();
            }
        });

        noCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstDonate = "No";
                setSelectedDonateType();
            }
        });
    }

    private void setSelectedDonateType() {
        yesCardview.setCardBackgroundColor(Color.WHITE);
        yes_textview.setTextColor(Color.BLACK);
        noCardview.setCardBackgroundColor(Color.WHITE);
        no_textview.setTextColor(Color.BLACK);


        switch (firstDonate) {
            case "First time donate":
                yesCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                yes_textview.setTextColor(Color.WHITE);
                lastDonateLayout.setVisibility(View.GONE);
                break;
            case "No":
                noCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                no_textview.setTextColor(Color.WHITE);
                lastDonateLayout.setVisibility(View.VISIBLE);
                lastdonatedate = lastDonatedateTextEd.getText().toString();
                firstDonate = lastdonatedate;
                break;
        }
    }

    private void setSelectedGender() {
        // Reset the background color and text color of both gender TextViews
        maleCardview.setCardBackgroundColor(Color.WHITE);
        maleTextview.setTextColor(Color.BLACK);
        femaleCardview.setCardBackgroundColor(Color.WHITE);
        femaleTextview.setTextColor(Color.BLACK);

        // Set the background color and text color of the selected gender TextView
        if (selectedGender.equals("Male")) {
            maleCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
            maleTextview.setTextColor(Color.WHITE);
        } else {
            femaleCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
            femaleTextview.setTextColor(Color.WHITE);
        }
    }


    private void setSelectedBloodType() {
        switch (bloodType) {
            case "O Positive":
                updateBloodTypeUI(o_positiveCardview, o_positiveTextview);
                break;
            case "O Negative":
                updateBloodTypeUI(o_negativeCardview, o_negativeTextview);
                break;
            case "A Positive":
                updateBloodTypeUI(a_positiveCardview, a_positiveTextview);
                break;
            case "A Negative":
                updateBloodTypeUI(a_negativeCardview, a_negativeTextview);
                break;
            case "B Positive":
                updateBloodTypeUI(b_positiveCardview, b_positiveTextview);
                break;
            case "B Negative":
                updateBloodTypeUI(b_negativeCardview, b_negativeTextview);
                break;
            case "AB Positive":
                updateBloodTypeUI(ab_positiveCardview, ab_positiveTextview);
                break;
            case "AB Negative":
                updateBloodTypeUI(ab_negativeCardview, ab_negativeTextview);
                break;
            case "Unknown":
                updateBloodTypeUI(unknown_Cardview, unknown_textview);
        }
        Log.d("bloodType", bloodType);
    }

    private void updateBloodTypeUI(MaterialCardView selectedCard, TextView selectedTextView) {
        // Reset the background color and text color of all blood type TextViews
        o_positiveCardview.setCardBackgroundColor(Color.WHITE);
        o_positiveTextview.setTextColor(Color.BLACK);
        o_negativeCardview.setCardBackgroundColor(Color.WHITE);
        o_negativeTextview.setTextColor(Color.BLACK);
        a_positiveCardview.setCardBackgroundColor(Color.WHITE);
        a_positiveTextview.setTextColor(Color.BLACK);
        a_negativeCardview.setCardBackgroundColor(Color.WHITE);
        a_negativeTextview.setTextColor(Color.BLACK);
        b_positiveCardview.setCardBackgroundColor(Color.WHITE);
        b_positiveTextview.setTextColor(Color.BLACK);
        b_negativeCardview.setCardBackgroundColor(Color.WHITE);
        b_negativeTextview.setTextColor(Color.BLACK);
        ab_positiveCardview.setCardBackgroundColor(Color.WHITE);
        ab_positiveTextview.setTextColor(Color.BLACK);
        ab_negativeCardview.setCardBackgroundColor(Color.WHITE);
        ab_negativeTextview.setTextColor(Color.BLACK);
        unknown_Cardview.setCardBackgroundColor(Color.WHITE);
        unknown_textview.setTextColor(Color.BLACK);

        // Set the background color and text color of the selected blood type TextView
        selectedCard.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
        selectedTextView.setTextColor(Color.WHITE);
    }


    // Method to set selected marital status
    private void setSelectedMaritalStatus() {
        // Reset the background color and text color of all marital status TextViews
        signle_cardview.setCardBackgroundColor(Color.WHITE);
        single_textview.setTextColor(Color.BLACK);
        married_cardview.setCardBackgroundColor(Color.WHITE);
        married_textview.setTextColor(Color.BLACK);
        divorced_cardview.setCardBackgroundColor(Color.WHITE);
        divorced_textview.setTextColor(Color.BLACK);
        widowed_cardview.setCardBackgroundColor(Color.WHITE);
        widowd_textview.setTextColor(Color.BLACK);

        // Set the background color and text color of the selected marital status TextView
        switch (maritalStatus) {
            case "Single":
                signle_cardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                single_textview.setTextColor(Color.WHITE);
                break;
            case "Married":
                married_cardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                married_textview.setTextColor(Color.WHITE);
                break;
            case "Divorced":
                divorced_cardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                divorced_textview.setTextColor(Color.WHITE);
                break;
            case "Widowed":
                widowed_cardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                widowd_textview.setTextColor(Color.WHITE);
                break;

        }

        Log.d("status", maritalStatus);
    }


    private void setSelectedIdentificationType() {
        // Reset the background color and text color of all identification type CardViews
        nationalCardview.setCardBackgroundColor(Color.WHITE);
        national_textview.setTextColor(Color.BLACK);
        passportCardview.setCardBackgroundColor(Color.WHITE);
        passport_textview.setTextColor(Color.BLACK);


        // Set the background color and text color of the selected identification type CardView
        switch (identificationType) {
            case "National":
                nationalCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                national_textview.setTextColor(Color.WHITE);
                break;
            case "Passport":
                passportCardview.setCardBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blood_splashbg));
                passport_textview.setTextColor(Color.WHITE);
                break;

        }
    }


    private void statusbarcolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }
    }


    private void datepicker() {
        // Set up DatePicker to show only dates that are at least 18 years ago from today
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -18); // Subtract 18 years from current date
        long minDateInMillis = calendar.getTimeInMillis();

        // Define a listener to be triggered when the user selects a date
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            // Check if the selected date is at least 18 years ago from today
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            if (selectedCalendar.getTimeInMillis() > minDateInMillis) {
                // Show a toast indicating that the selected date is invalid
                Toast.makeText(Blood_Donation_Registration.this, "Please select a date from at least 18 years ago", Toast.LENGTH_SHORT).show();
            } else {
                // Set the selected date in the EditText
                dateEditText.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month + 1, year));
            }
        };

        // Initialize DatePickerDialog with current date and dateSetListener
        DatePickerDialog datePickerDialog = new DatePickerDialog(Blood_Donation_Registration.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Set maximum date to 18 years ago from today
        datePickerDialog.getDatePicker().setMaxDate(minDateInMillis);

        // Show DatePickerDialog when the EditText is clicked
        dateEditText.setOnClickListener(v -> datePickerDialog.show());
    }

    private void lastdonatedatepicker() {
        // Get current date
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Calculate the maximum selectable date (tomorrow)
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        int maxYear = calendar.get(Calendar.YEAR);
        int maxMonth = calendar.get(Calendar.MONTH);
        int maxDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Listener to handle date selection
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            // Check if the selected date is within the allowed range
            Calendar selectedCalendar = Calendar.getInstance();
            selectedCalendar.set(year, month, dayOfMonth);
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.add(Calendar.MONTH, -6); // Move 6 months back from the current date
            if (selectedCalendar.before(currentCalendar) || selectedCalendar.after(Calendar.getInstance())) {
                // Show a toast indicating that the selected date is invalid
                Toast.makeText(Blood_Donation_Registration.this, "Please select a date within the last 6 months or up to tomorrow", Toast.LENGTH_SHORT).show();
            } else {
                // Set the selected date in the EditText
                lastDonatedateTextEd.setText(String.format(Locale.getDefault(), "%d/%d/%d", dayOfMonth, month + 1, year));
            }
        };

        // Initialize DatePickerDialog with current date and dateSetListener
        DatePickerDialog datePickerDialog = new DatePickerDialog(Blood_Donation_Registration.this, dateSetListener, currentYear, currentMonth, currentDay);

        // Set minimum date to 6 months ago from today
        calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -6);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        // Set maximum date to tomorrow
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        // Show DatePickerDialog when EditText is clicked
        lastDonatedateTextEd.setOnClickListener(v -> datePickerDialog.show());
    }





    void getName() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    phoneAuthModel = task.getResult().toObject(PhoneAuthModel.class);
                    if (phoneAuthModel != null) {
                        nameEd.setText(phoneAuthModel.getUsername());

                    }
                }

                if (task.isSuccessful()) {

                    emailUser = task.getResult().toObject(EmailUser.class);
                    if (emailUser != null) {
                        nameEd.setText(emailUser.getName());

                    }
                }


            }
        });
    }


    //==================================================//

    // Upon submission of the form, insert the data into Firestore
    private void submitForm(String name, String address, String identity, String mobile, String dateOfBirth, String district, String nationality, String gender, String bloodType, String maritalStatus, String identificationType, String selectedorg, String selectedmanualorg, String donateType) {

        boolean isApproved = false;
        boolean isDeclined = false;

        uid = FirebaseAuth.getInstance().getUid();

        bloodDonerRequestModel = new BloodDonerRequestModel(uid, name, address, identity, mobile, dateOfBirth, district, nationality, gender, bloodType, maritalStatus, identificationType, selectedorg, selectedmanualorg, donateType, Timestamp.now(), isApproved, isDeclined);

        // Add other form fields

        setInProgress(true);
        // Add form data to Firestore with the current user's ID
        FirebaseUtil.donerUserDetails("bloodDoner").set(bloodDonerRequestModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setInProgress(false);

                        FirebaseUtil.currentUserDetails().update("isform_filled", true);
                        FirebaseUtil.currentUserDetails().update("isform_notfilled", false);

                        Toast.makeText(Blood_Donation_Registration.this, "Form Sumbit Succesffully", Toast.LENGTH_SHORT).show();
                        finish();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("formadd", "Error adding form data", e);
                        // Show an error message to the user
                        Toast.makeText(Blood_Donation_Registration.this, "Form Sumbit Failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    // Update Firestore document when admin approves or declines the form


    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressbar.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.GONE);
        } else {
            progressbar.setVisibility(View.GONE);
            registerBtn.setVisibility(View.VISIBLE);
        }
    }

    private void locationget() {

        if (DrawerLayout.locationText != null) {

            locationTextView.setText(DrawerLayout.locationText);
        } else {
            locationTextView.setText("Location not available");
        }
    }

    private void DataRetrive() {
        orgallDataList.clear(); // Clear previous data
        orglist.clear();

        orglist.add("Other");

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
                                if (orgmodel != null) {
                                    orgallDataList.add(orgmodel);
                                    orglist.add(orgmodel.getOrgname_Ed());
                                }


                                // Log the size of the list
                                Log.d("DataRetrieved", "Size of allDataList: " + orgallDataList.size());

                                // Notify adapter about the data change
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

    private List<String> getOrglist() {
        return orglist;
    }



}
