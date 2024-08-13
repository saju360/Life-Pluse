package com.saju.lifepluse.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.saju.lifepluse.R;


public class HospitalView extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View hospitalview = inflater.inflate(R.layout.fragment_hospital_view, container, false);





        return hospitalview;
    }
}