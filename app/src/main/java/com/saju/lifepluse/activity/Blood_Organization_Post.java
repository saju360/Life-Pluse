package com.saju.lifepluse.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.BloodOrganizationAddModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.HashMap;
import java.util.Map;

public class Blood_Organization_Post extends AppCompatActivity {


    EditText orgname_Ed, orgaddress_Ed, orgphone_Ed, orgemail_Ed, orgfb_Ed, orginsta_Ed, orgyout_Ed, orgweb_Ed;
    Button orgcancelButton, orgAddButton;
    ProgressBar progressBar;
    String uid;
    TextView org_title;
    LinearLayout sumbitBtnLayout;
    FirebaseUser currentuser;
    boolean isEditMode = false;

    ImageView button_back;
    BloodOrganizationAddModel bloodOrganizationAddModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_organization_post);
        statusbarcolor();
        initial();

        userAuth();


        orgcancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        orgAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validatycheck();

            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Blood_Organization_Post.this, Blood_Organization_Home.class));
                finish();
            }
        });


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
                        boolean is_form_filled = documentSnapshot.getBoolean("isorgadd");
                        boolean is_form_notfilled = documentSnapshot.getBoolean("isorgnotadd");


                        if (is_form_filled) {
                            Log.d("orgform_status", "OrgForm  Filled Yet");

                            retrivepersingData();

                        } else if (is_form_notfilled) {

                        }
                    } else {
                        Log.d("orgform_status", "DocumentSnapshot is null");
                    }
                } else {
                    Log.d("orgform_status", "Error getting document: ", task.getException());

                }


            }
        });

    }

    private void retrivepersingData() {


        String org_name = getIntent().getStringExtra("org_name");
        String org_address = getIntent().getStringExtra("org_address");
        String org_phone = getIntent().getStringExtra("org_phone");
        String org_email = getIntent().getStringExtra("org_email");
        String org_fb = getIntent().getStringExtra("fb_link");
        String org_insta = getIntent().getStringExtra("insta_link");
        String org_web = getIntent().getStringExtra("web_link");
        String org_yout = getIntent().getStringExtra("yout_link");

        if (!org_name.isEmpty() && org_name != null) {

            org_title.setText("Update Your Org Info");
            orgAddButton.setText("Update Now");
            orgname_Ed.setText(org_name);
            orgaddress_Ed.setText(org_address);
            orgphone_Ed.setText(org_phone);
            orgemail_Ed.setText(org_email);
            orgfb_Ed.setText(org_fb);
            orginsta_Ed.setText(org_insta);
            orgweb_Ed.setText(org_web);
            orgyout_Ed.setText(org_yout);
            isEditMode = true;
        }

    }

    private void validatycheck() {
        String orgname = orgname_Ed.getText().toString();
        String orgaddress = orgaddress_Ed.getText().toString();
        String orgphone = orgphone_Ed.getText().toString();
        String orgemail = orgemail_Ed.getText().toString();
        String orgfblink = orgfb_Ed.getText().toString();
        String orginstalink = orginsta_Ed.getText().toString();
        String orgyoutlink = orgyout_Ed.getText().toString();
        String orgweblink = orgweb_Ed.getText().toString();

        if (TextUtils.isEmpty(orgname) || TextUtils.isEmpty(orgaddress) || TextUtils.isEmpty(orgphone) ||
                TextUtils.isEmpty(orgemail) || (TextUtils.isEmpty(orgfblink) && TextUtils.isEmpty(orginstalink) &&
                TextUtils.isEmpty(orgyoutlink) && TextUtils.isEmpty(orgweblink))) {
            Toast.makeText(getApplicationContext(), "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Function to validate URLs
        boolean isValidURL = true;

        // Facebook link validation
        if (!TextUtils.isEmpty(orgfblink)) {
            if (!(orgfblink.startsWith("https://www.facebook.com") || orgfblink.startsWith("www.facebook.com") || orgfblink.startsWith("https://facebook.com"))) {
                isValidURL = false;
                Toast.makeText(getApplicationContext(), "Facebook link should start with 'https://www.facebook.com', 'www.facebook.com', or 'https://facebook.com'", Toast.LENGTH_SHORT).show();
            }
        }


        // Instagram link validation
        if (!TextUtils.isEmpty(orginstalink)) {
            if (!(orginstalink.startsWith("https://www.instagram.com") || orginstalink.startsWith("www.instagram.com") || orginstalink.startsWith("https://instagram.com"))) {
                isValidURL = false;
                Toast.makeText(getApplicationContext(), "Instagram link should start with 'https://www.instagram.com', 'www.instagram.com', or 'https://instagram.com'", Toast.LENGTH_SHORT).show();
            }
        }


        // YouTube link validation
        if (!TextUtils.isEmpty(orgyoutlink)) {
            if (!(orgyoutlink.startsWith("https://www.youtube.com") || orgyoutlink.startsWith("www.youtube.com") || orgyoutlink.startsWith("https://youtube.com"))) {
                isValidURL = false;
                Toast.makeText(getApplicationContext(), "YouTube link should start with 'https://www.youtube.com', 'www.youtube.com', or 'https://youtube.com'", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isValidURL) {
            return;
        }

        orgDataAdd(orgname, orgaddress, orgphone, orgemail, orgfblink, orginstalink, orgyoutlink, orgweblink);

        // Proceed with further actions if all validations pass
    }

    private void orgDataAdd(String orgname, String orgaddress, String orgphone, String orgemail, String orgfblink, String orginstalink, String orgyoutlink, String orgweblink) {


        uid = FirebaseAuth.getInstance().getUid();


        bloodOrganizationAddModel = new BloodOrganizationAddModel(uid, orgname, orgaddress, orgphone, orgemail, orgfblink, orginstalink, orgyoutlink, orgweblink, Timestamp.now());

        setInProgress(true);


        if (isEditMode) {

            Map<String, Object> map = new HashMap<>();
            map.put("orgname_Ed", orgname); // Add your key-value pair here
            map.put("orgaddress_Ed", orgaddress);
            map.put("orgphone_Ed", orgphone);
            map.put("orgemail_Ed", orgemail);
            map.put("orgfb_Ed", orgfblink);
            map.put("orginsta_Ed", orginstalink);
            map.put("orgyout_Ed", orgyoutlink);
            map.put("orgweb_Ed", orgweblink);


            FirebaseUtil.donerUserDetails("BloodOrganization").update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    setInProgress(false);

                    Log.d("datasave", "data update successfully");
                    startActivity(new Intent(Blood_Organization_Post.this, Blood_Organization_Edit_Profile.class));
                    finish();

                }
            });


        } else {
            FirebaseUtil.donerUserDetails("BloodOrganization").set(bloodOrganizationAddModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    setInProgress(false);

                    FirebaseUtil.currentUserDetails().update("isorgadd", true);
                    FirebaseUtil.currentUserDetails().update("isorgnotadd", false);
                    Log.d("datasave", "data add successfully");
                    startActivity(new Intent(Blood_Organization_Post.this, Blood_Organization_Home.class));
                    finish();


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("orgdataadd", e.toString());
                }
            });

        }


    }


    private void initial() {

        orgname_Ed = findViewById(R.id.orgname_Ed);
        orgaddress_Ed = findViewById(R.id.orgaddress_Ed);
        orgphone_Ed = findViewById(R.id.orgphone_Ed);
        orgemail_Ed = findViewById(R.id.orgemail_Ed);
        orgfb_Ed = findViewById(R.id.orgfb_Ed);
        orginsta_Ed = findViewById(R.id.orginsta_Ed);
        orgyout_Ed = findViewById(R.id.orgyout_Ed);
        orgweb_Ed = findViewById(R.id.orgweb_Ed);
        orgcancelButton = findViewById(R.id.org_cancelButton);
        orgAddButton = findViewById(R.id.org_AddButton);
        progressBar = findViewById(R.id.progressbarId);
        sumbitBtnLayout = findViewById(R.id.sumbitBtnLayout);
        org_title = findViewById(R.id.org_title);
        button_back = findViewById(R.id.button_back);
        currentuser = FirebaseAuth.getInstance().getCurrentUser();


    }


    private void statusbarcolor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blood_splashbg));
        }
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            sumbitBtnLayout.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            sumbitBtnLayout.setVisibility(View.VISIBLE);
        }
    }
}