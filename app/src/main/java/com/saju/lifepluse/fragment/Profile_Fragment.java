package com.saju.lifepluse.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.BloodBank;
import com.saju.lifepluse.activity.DrawerLayout;
import com.saju.lifepluse.activity.SignIn;
import com.saju.lifepluse.activity.SignUp;


public class Profile_Fragment extends Fragment {

    LottieAnimationView logout_anim_btn;
    Button logintBtn, creataccountBtn;
    ImageView button_back;
    LinearLayout singin_layoutforprofile, profile_design_layout;



    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View profileview = inflater.inflate(R.layout.fragment_profile_, container, false);



        initmethod(profileview);

        firebasecurrentuser();


        button_back.setOnClickListener(v -> startActivity(new Intent(getActivity(), BloodBank.class)));




        logout_anim_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            DrawerLayout.header_profile_layout.setVisibility(View.GONE);
            DrawerLayout.loginBtn.setVisibility(View.VISIBLE);
            DrawerLayout.createBtn.setVisibility(View.VISIBLE);
            profile_design_layout.setVisibility(View.GONE);
            singin_layoutforprofile.setVisibility(View.VISIBLE);
            logout_anim_btn.setVisibility(View.GONE);
            startActivity(new Intent(getContext(), BloodBank.class));
        });


        logintBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignIn.class)));


        creataccountBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SignUp.class)));



        return profileview;
    }

    private void firebasecurrentuser() {


        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentuser==null){

            startActivity(new Intent(getActivity(), SignIn.class));
            profile_design_layout.setVisibility(View.GONE);
            singin_layoutforprofile.setVisibility(View.VISIBLE);
            logout_anim_btn.setVisibility(View.GONE);

        }

        else {

            profile_design_layout.setVisibility(View.VISIBLE);
            singin_layoutforprofile.setVisibility(View.GONE);
            logout_anim_btn.setVisibility(View.VISIBLE);

        }

    }

    private void initmethod(View profileview) {
        logout_anim_btn = profileview.findViewById(R.id.logout_anim_btn);
        logintBtn = profileview.findViewById(R.id.logintBtn);
        creataccountBtn = profileview.findViewById(R.id.creataccountBtn);
        singin_layoutforprofile = profileview.findViewById(R.id.singin_layoutforprofile);
        profile_design_layout = profileview.findViewById(R.id.profile_design_layout);
        button_back = profileview.findViewById(R.id.button_back);





    }


}