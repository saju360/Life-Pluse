package com.saju.lifepluse.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.CategoryModel;
import com.saju.lifepluse.GridviewAdapter;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.Ambulance;
import com.saju.lifepluse.activity.BloodBank_Splash;
import com.saju.lifepluse.activity.ChatbotActivity;
import com.saju.lifepluse.activity.Contact_US;
import com.saju.lifepluse.activity.DoctorSpeciality;
import com.saju.lifepluse.activity.Hospital;
import com.saju.lifepluse.activity.MainActivity;
import com.saju.lifepluse.activity.Pharmecy;

import java.util.ArrayList;
import java.util.List;

public class DashBoard extends Fragment {

    ImageSlider imageSlider;
    GridView gridView;

    private FirebaseFirestore db;
    GridviewAdapter adapter;
    private ArrayList<CategoryModel> dataList;
    TextView marqueeText, noticeText;
    ArrayList<SlideModel> imageList = new ArrayList<>();
    RelativeLayout chatbot_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_dashboard, container, false);

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");
        }

        db = FirebaseFirestore.getInstance();
        imageSlider = myview.findViewById(R.id.image_slider);
        gridView = myview.findViewById(R.id.gridviewId);
        marqueeText = myview.findViewById(R.id.marqueeTextView);
        noticeText = myview.findViewById(R.id.noticeText);
        chatbot_btn = myview.findViewById(R.id.chatbot_btn);
        ImageView chatbotIcon = myview.findViewById(R.id.chatbot_icon);
        View pulseCircle = myview.findViewById(R.id.pulse_circle);
        RelativeLayout chatbotBtn = myview.findViewById(R.id.chatbot_btn);
        dataList = new ArrayList<>();

        loadMarqueeAndSlides();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String catId = dataList.get(position).getCatId();
                String catNameEnglish = dataList.get(position).getNameEnglish();
                String catNameBangla = dataList.get(position).getNameBangla();

                if (position == 0) {
                    startActivity(new Intent(getContext(), Pharmecy.class).putExtra("catId", catId));
                } else if (position == 1) {
                    startActivity(new Intent(getContext(), Ambulance.class).putExtra("catId", catId));
                } else if (position == 2) {
                    Intent intent = new Intent(getContext(), DoctorSpeciality.class);
                    intent.putExtra("catId", catId);
                    intent.putExtra("catEnglishName", catNameEnglish);
                    intent.putExtra("catBanglaName", catNameBangla);
                    startActivity(intent);
                } else if (position == 3) {
                    startActivity(new Intent(getContext(), Hospital.class).putExtra("catId", catId));
                }else if (position == 4) {
                    startActivity(new Intent(getContext(), MainActivity.class));
                }else if (position == 5){
                    startActivity(new Intent(getContext(), Contact_US.class));
                }else if (position == 6) {
                    startActivity(new Intent(getContext(), BloodBank_Splash.class).putExtra("catId", catId));
                }
            }
        });

        db.collection("Category").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null && value != null) {
                    dataList.clear();
                    List<CategoryModel> data = value.toObjects(CategoryModel.class);
                    dataList.addAll(data);
                    adapter = new GridviewAdapter(getContext(), dataList);
                    gridView.setAdapter(adapter);
                }
            }
        });


        ValueAnimator pulseAnimator = ValueAnimator.ofFloat(0f, 1f);
        pulseAnimator.setDuration(1000);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.RESTART);
        pulseAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            pulseCircle.setScaleX(0.8f + 0.4f * value);
            pulseCircle.setScaleY(0.8f + 0.4f * value);
            pulseCircle.setAlpha(1f - value);
        });

        chatbotBtn.setOnClickListener(v -> {
            // Handle click
            startActivity(new Intent(getContext(), ChatbotActivity.class));
        });

// Start animation when visible
        chatbotBtn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pulseCircle.setVisibility(View.VISIBLE);
                    pulseAnimator.start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    pulseCircle.setVisibility(View.INVISIBLE);
                    pulseAnimator.cancel();
                    break;
            }
            return false;
        });

// Add idle animation (breathing effect)
        ValueAnimator breathAnimator = ValueAnimator.ofFloat(0.9f, 1.1f);
        breathAnimator.setDuration(2000);
        breathAnimator.setRepeatCount(ValueAnimator.INFINITE);
        breathAnimator.setRepeatMode(ValueAnimator.REVERSE);
        breathAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            chatbotIcon.setScaleX(value);
            chatbotIcon.setScaleY(value);
        });
        breathAnimator.start();

        return myview;
    }

    private void loadMarqueeAndSlides() {
        SharedPreferences prefs = getActivity().getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "en");

        // Load marquee texts
        db.collection("MarqueeTexts").document("latest").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> marqueeTexts = (List<String>) documentSnapshot.get(language.equals("bn") ? "textBangla" : "textEnglish");
                String noticeLabel = language.equals("bn") ? "বিজ্ঞপ্তি" : "Notice";
                noticeText.setText(noticeLabel);
                String joined = TextUtils.join(language.equals("bn") ? " । " : " • ", marqueeTexts);
                marqueeText.setText(joined);
                marqueeText.setSelected(true);
            }
        });

        // Load slider images
        db.collection("SliderImages").get().addOnSuccessListener(querySnapshot -> {
            imageList.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                String imageUrl = doc.getString("imageUrl");
                String caption = language.equals("bn") ? doc.getString("captionBangla") : doc.getString("captionEnglish");

                if (imageUrl != null && caption != null) {
                    imageList.add(new SlideModel(imageUrl, caption, ScaleTypes.FIT));
                }
            }
            imageSlider.setImageList(imageList);
        });
    }
}
