package com.saju.lifepluse.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.saju.lifepluse.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                .title("24/7 Emergency Service")
                .description("Get immediate medical help with our always-available ambulance service.")
                .image(R.drawable.onboarding_1) // Replace with a more stylized ambulance image if available
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.deep_red) // e.g., #B71C1C
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Blood Bank")
                .description("Locate nearby blood banks and ensure availability in real-time.")
                .image(R.drawable.blood_white_icon) // You can update this to a modern flat icon
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.dark_red) // e.g., #880E4F
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Find Your Doctor")
                .description("Search for the right doctor near you and book appointments easily.")
                .image(R.drawable.blood_white_icon) // Replace with a vector or custom doctor icon
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.deep_blue) // e.g., #0D47A1
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Health Tips & Updates")
                .description("Stay informed with regular health tips, news, and emergency alerts.")
                .image(R.drawable.blood_white_icon) // Add a health tips icon or image
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.teal_700) // e.g., #00796B
                .build());
    }
}
