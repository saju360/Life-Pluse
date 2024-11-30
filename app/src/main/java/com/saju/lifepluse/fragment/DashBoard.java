package com.saju.lifepluse.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.CategoryModel;
import com.saju.lifepluse.GridviewAdapter;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.Ambulance;
import com.saju.lifepluse.activity.BloodBank_Splash;
import com.saju.lifepluse.activity.DoctorSpeciality;
import com.saju.lifepluse.activity.Hospital;
import com.saju.lifepluse.activity.Pharmecy;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;


public class DashBoard extends Fragment {

    ImageSlider imageSlider;
    GridView gridView;

    private FirebaseFirestore db;
    GridviewAdapter adapter;
    private ArrayList<CategoryModel> dataList;
    TextView marqueeText, noticeText;
    private String[] marqueeTextsEnglish = {"54 ballots cancelled in Nalchity due to irregularities", "Speaker Shirin Sharmin wins from Rangpur-6", "Prime Minister Sheikh Hasina will hold a post-election press conference at the Gono Bhaban at 3:00pm tomorrow"};
    private String[] marqueeTextsBangla = {"নলছিটিতে অনিয়মের কারণে ৫৪টি ব্যালট বাতিল হয়েছে", "রংপুর-৬ থেকে বিজয়ী হয়েছেন স্পিকার শিরীন শারমিন", "আগামীকাল বিকাল ৩টায় গণভবনে নির্বাচন-পরবর্তী সংবাদ সম্মেলন করবেন প্রধানমন্ত্রী শেখ হাসিনা।"};

    ArrayList<SlideModel> imageList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);


        db = FirebaseFirestore.getInstance();
        imageSlider = myview.findViewById(R.id.image_slider);
        gridView = myview.findViewById(R.id.gridviewId);
        marqueeText = myview.findViewById(R.id.marqueeTextView);
        noticeText = myview.findViewById(R.id.noticeText);
        dataList = new ArrayList<>();


        marqueetext();

        //=============================slide code here start==============//


        //=============================slide code here end==============//


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String catId = dataList.get(position).getCatId();
                String catNameEnglish = dataList.get(position).getNameEnglish();
                String catNameBangla = dataList.get(position).getNameBangla();

                if (position == 0) {
                    Intent myIntent = new Intent(getContext(), Pharmecy.class);
                    myIntent.putExtra("catId", catId);
                    startActivity(myIntent);
                } else if (position == 1) {
                    Intent myIntent = new Intent(getContext(), Ambulance.class);
                    myIntent.putExtra("catId", catId);
                    startActivity(myIntent);
                } else if (position == 2) {
                    Intent myIntent = new Intent(getContext(), DoctorSpeciality.class);
                    myIntent.putExtra("catId", catId);
                    myIntent.putExtra("catEnglishName", catNameEnglish);
                    myIntent.putExtra("catBanglaName", catNameBangla);
                    startActivity(myIntent);
                } else if (position == 3) {
                    Intent myIntent = new Intent(getContext(), Hospital.class);
                    myIntent.putExtra("catId", catId);
                    startActivity(myIntent);
                } else if (position == 5) {
                    Intent myIntent = new Intent(getContext(), BloodBank_Splash.class);
                    myIntent.putExtra("catId", catId);
                    startActivity(myIntent);
                }

            }
        });

        dataList.clear();

        db.collection("Category").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if (error == null) {
                    dataList.clear();
                    List<CategoryModel> data = value.toObjects(CategoryModel.class);
                    dataList.addAll(data);

                    adapter = new GridviewAdapter(getContext(), dataList);
                    gridView.setAdapter(adapter);
                }

            }
        });


        return myview;
    }

    private void marqueetext() {
        //=============================Marquee text code here start==============//


        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (language.equals("bn")) {

            noticeText.setText("বিজ্ঞপ্তি");
            String joinedText = TextUtils.join(" । ", marqueeTextsBangla);
            marqueeText.setText(joinedText);
            marqueeText.setSelected(true);

            imageList.add(new SlideModel(R.drawable.onboarding_1, "নলছিটিতে অনিয়মের কারণে ৫৪টি ব্যালট বাতিল হয়েছে", ScaleTypes.FIT));
            imageList.add(new SlideModel(R.drawable.onboarding_1, "নলছিটিতে অনিয়মের কারণে ৫৪টি ব্যালট বাতিল হয়েছে", ScaleTypes.FIT));
            imageList.add(new SlideModel(R.drawable.onboarding_1, "নলছিটিতে অনিয়মের কারণে ৫৪টি ব্যালট বাতিল হয়েছে", ScaleTypes.FIT));

            imageSlider.setImageList(imageList);

        } else {

            noticeText.setText("Notice");
            String joinedText = TextUtils.join(" • ", marqueeTextsEnglish);
            marqueeText.setText(joinedText);
            marqueeText.setSelected(true);

            imageList.add(new SlideModel(R.drawable.onboarding_1, "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));
            imageList.add(new SlideModel(R.drawable.onboarding_1, "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));
            imageList.add(new SlideModel(R.drawable.onboarding_1, "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));

            imageSlider.setImageList(imageList);

        }

        //=============================Marquee code here end==============//

    }





}