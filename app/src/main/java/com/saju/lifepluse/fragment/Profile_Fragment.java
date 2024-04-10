package com.saju.lifepluse.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.BloodBank;
import com.saju.lifepluse.activity.Blood_Donation_Registration;
import com.saju.lifepluse.activity.DrawerLayout;
import com.saju.lifepluse.activity.SignIn;
import com.saju.lifepluse.activity.SignUp;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;
import com.saju.lifepluse.utils.AndroidUtil;
import com.saju.lifepluse.utils.FirebaseUtil;


public class Profile_Fragment extends Fragment {

    // Declare UI elements
    public static LottieAnimationView logout_anim_btn;
    Button logintBtn, creataccountBtn;
    public static MaterialButton deleteaccountBtn_id, changepassword_btnId;
    ImageView button_back;
    public static LinearLayout singin_layoutforprofile, profile_design_layout;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    BloodDonerRequestModel bloodDonerRequestModel;
    TextView nameTv, acc_datetvId, bloodType_tv, dateofbirth_tv, identification_number_tv, district_tv, mobile_tv, gender_tv, marital_tv, address_tv;

    TextView application_status;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View profileview = inflater.inflate(R.layout.fragment_profile_, container, false);

        // Initialize UI elements and FirebaseAuth
        initmethod(profileview);
        firebasecurrentuser();




        // Set click listeners
        button_back.setOnClickListener(v -> startActivity(new Intent(getActivity(), BloodBank.class)));
        logout_anim_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // Update UI after logout
            updateUIAfterLogout();
        });
        logintBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignIn.class)));
        creataccountBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUp.class)));
        deleteaccountBtn_id.setOnClickListener(v -> deletaccount());

        return profileview;
    }



    void form_status(){

        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot documentSnapshot = task.getResult();
                boolean is_form_filled= documentSnapshot.getBoolean("isform_filled");
                boolean is_form_notfilled= documentSnapshot.getBoolean("isform_notfilled");


                if (is_form_filled){
                    approval_status();
                    Log.d("form_status", "Form  Filled Yet");
                    application_status.setVisibility(View.VISIBLE);

                } else if (is_form_notfilled) {
                    Log.d("form_status", "Form Not Filled Yet");
                    application_status.setText("Please Join as a Blood Doner");
                    application_status.setVisibility(View.VISIBLE);
                    startActivity(new Intent(getActivity(), Blood_Donation_Registration.class));
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
                    if (isApproved) {

                        profiledata_retrive();
                        application_status.setVisibility(View.GONE);


                    } else if (isDeclined) {

                        profile_design_layout.setVisibility(View.GONE);
                        singin_layoutforprofile.setVisibility(View.GONE);
                        application_status.setTextColor(Color.RED);
                        application_status.setText("Application Status: Your Application is rejected by Admin");

                    }else {
                        application_status.setText("Application Status: Your Application is in Review");


                    }


                }


            }
        });


    }

    private void profiledata_retrive() {


        FirebaseUtil.donerUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){


                    bloodDonerRequestModel = task.getResult().toObject(BloodDonerRequestModel.class);
                    String name = bloodDonerRequestModel.getName();
                    String bloodtype = bloodDonerRequestModel.getBloodType();
                    String mobile = bloodDonerRequestModel.getMobile();
                    String dob = bloodDonerRequestModel.getDateOfBirth();
                    String nid = bloodDonerRequestModel.getIdentity();
                    String district = bloodDonerRequestModel.getDistrict();
                    String gender = bloodDonerRequestModel.getGender();
                    String marital_txt = bloodDonerRequestModel.getMaritalStatus();
                    String address = bloodDonerRequestModel.getAddress();

                    nameTv.setText("Name: " + name);
                    bloodType_tv.setText(bloodtype);
                    acc_datetvId.setText("Joined: " + AndroidUtil.timestampToString(bloodDonerRequestModel.getCreatedtime()));
                    dateofbirth_tv.setText(dob);
                    identification_number_tv.setText(nid);
                    mobile_tv.setText(mobile);
                    district_tv.setText(district);
                    gender_tv.setText(gender);
                    marital_tv.setText(marital_txt);
                    address_tv.setText(address);


                    Log.d("name", name);

                    profile_design_layout.setVisibility(View.VISIBLE);
                    singin_layoutforprofile.setVisibility(View.GONE);
                    logout_anim_btn.setVisibility(View.VISIBLE);

                }
            }
        });

    }

    private void updateUIAfterLogout() {
        DrawerLayout.header_profile_layout.setVisibility(View.GONE);
        DrawerLayout.loginBtn.setVisibility(View.VISIBLE);
        DrawerLayout.createBtn.setVisibility(View.VISIBLE);
        profile_design_layout.setVisibility(View.GONE);
        singin_layoutforprofile.setVisibility(View.VISIBLE);
        logout_anim_btn.setVisibility(View.GONE);
        application_status.setVisibility(View.GONE);
        startActivity(new Intent(getContext(), BloodBank.class));
    }


    private void deletaccount() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Account Deletation")
                .setIcon(R.drawable.alert_icon)
                .setMessage("Are you sure you want to delete your account? Once you delete your account, you can no longer access it.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user = mAuth.getCurrentUser();
                        if (user != null) {
                            user.delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Account deleted Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getActivity(), SignUp.class));
                                                updateUIAfterLogout();
                                                mAuth.signOut();
                                            } else {
                                                Toast.makeText(getContext(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "No user signed in", Toast.LENGTH_SHORT).show();
                        }
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

    private void firebasecurrentuser() {
        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentuser == null) {
            startActivity(new Intent(getActivity(), SignIn.class));
            profile_design_layout.setVisibility(View.GONE);
            singin_layoutforprofile.setVisibility(View.VISIBLE);
            logout_anim_btn.setVisibility(View.GONE);
            application_status.setVisibility(View.GONE);
        } else {
            //profile_design_layout.setVisibility(View.VISIBLE);
            singin_layoutforprofile.setVisibility(View.GONE);
            logout_anim_btn.setVisibility(View.VISIBLE);
            form_status();
        }
    }

    private void initmethod(View profileview) {
        logout_anim_btn = profileview.findViewById(R.id.logout_anim_btn);
        logintBtn = profileview.findViewById(R.id.logintBtn);
        creataccountBtn = profileview.findViewById(R.id.creataccountBtn);
        singin_layoutforprofile = profileview.findViewById(R.id.singin_layoutforprofile);
        profile_design_layout = profileview.findViewById(R.id.profile_design_layout);
        button_back = profileview.findViewById(R.id.button_back);
        changepassword_btnId = profileview.findViewById(R.id.changepassword_btnId);
        deleteaccountBtn_id = profileview.findViewById(R.id.deleteaccountBtn_id);
        nameTv = profileview.findViewById(R.id.NametvId);
        acc_datetvId = profileview.findViewById(R.id.acc_datetvId);
        bloodType_tv = profileview.findViewById(R.id.bloodType_tv);
        dateofbirth_tv = profileview.findViewById(R.id.dateofbirth_tv);
        identification_number_tv = profileview.findViewById(R.id.identification_number_tv);
        district_tv = profileview.findViewById(R.id.district_tv);
        mobile_tv = profileview.findViewById(R.id.mobile_tv);
        gender_tv = profileview.findViewById(R.id.gender_tv);
        marital_tv = profileview.findViewById(R.id.marital_tv);
        address_tv = profileview.findViewById(R.id.address_tv);
        application_status = profileview.findViewById(R.id.application_status);
        mAuth = FirebaseAuth.getInstance();
    }

}
