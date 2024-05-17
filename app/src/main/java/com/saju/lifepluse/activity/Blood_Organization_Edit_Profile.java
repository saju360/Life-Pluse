package com.saju.lifepluse.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;
import com.saju.lifepluse.modelclass.BloodOrganizationAddModel;
import com.saju.lifepluse.utils.AndroidUtil;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;

public class Blood_Organization_Edit_Profile extends AppCompatActivity {


    TextView org_name, org_acc_datetvId, orgAddress_tv;
    MaterialButton deleteorgBtn_id;
    LottieAnimationView org_callAnimationId;
    ImageView org_edit_icon;
    ImageView weblink_btn, fblink_btn, instalink_btn, youtlink_btn;
    ArrayList<BloodOrganizationAddModel> orgallDataList;
    FirebaseFirestore db;
    FirebaseAuth auth;
    TextView locationTextView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_organization_edit_profile);


        org_name = findViewById(R.id.org_name_tv_textview);
        org_acc_datetvId = findViewById(R.id.org_acc_datetvId);
        orgAddress_tv = findViewById(R.id.orgAddress_tv_textview);
        org_edit_icon =findViewById(R.id.org_edit_icon);
        org_callAnimationId = findViewById(R.id.org_callAnimationId);
        weblink_btn = findViewById(R.id.weblink_btn);
        fblink_btn = findViewById(R.id.fblink_btn);
        instalink_btn = findViewById(R.id.instalink_btn);
        youtlink_btn = findViewById(R.id.youtlink_btn);
        locationTextView = findViewById(R.id.locationTextView);
        deleteorgBtn_id = findViewById(R.id.deleteorgBtn_id);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        DataRetrive();
        statusbarcolor();
        locationRetrive();

        deleteorgBtn_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteorg();


            }
        });





    }

    private void deleteorg() {

        new AlertDialog.Builder(Blood_Organization_Edit_Profile.this)
                .setTitle("Organization Deletation")
                .setIcon(R.drawable.alert_icon)
                .setMessage("Are you sure you want to delete your organization?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUtil.donerUserDetails("BloodOrganization").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                finish();

                                FirebaseUtil.currentUserDetails().update("isorgadd", false);
                                FirebaseUtil.currentUserDetails().update("isorgnotadd", true);

                                Toast.makeText(getApplicationContext(), "Org Delete Successfully", Toast.LENGTH_SHORT).show();


                            }
                        });

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
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


        FirebaseUtil.donerUserDetails("BloodOrganization").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {


                if (task.isSuccessful()){


                    BloodOrganizationAddModel model = task.getResult().toObject(BloodOrganizationAddModel.class);


                    org_name.setText(model.getOrgname_Ed());
                    org_acc_datetvId.setText("Joined: "+AndroidUtil.timestampToString(model.getAddedtime()));
                    orgAddress_tv.setText(model.getOrgaddress_Ed());

                    String name = model.getOrgname_Ed();
                    String address = model.getOrgaddress_Ed();
                    String acc_createdate = AndroidUtil.timestampToString(model.getAddedtime());
                    String fb_link = model.getOrgfb_Ed();
                    String web_link = model.getOrgweb_Ed();
                    String insta_link = model.getOrginsta_Ed();
                    String yout_link = model.getOrgyout_Ed();
                    String phone = model.getOrgphone_Ed();
                    String email = model.getOrgemail_Ed();



                    org_edit_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Intent intent = new Intent(getApplicationContext(), Blood_Organization_Post.class);
                            intent.putExtra("org_name", name);
                            intent.putExtra("org_address", address);
                            intent.putExtra("org_phone", phone);
                            intent.putExtra("org_email", email);
                            intent.putExtra("fb_link", fb_link);
                            intent.putExtra("insta_link", insta_link);
                            intent.putExtra("web_link", web_link);
                            intent.putExtra("yout_link", yout_link);

                            startActivity(intent);



                        }
                    });


                }


            }
        });


    }


}