package com.saju.lifepluse.activity;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;

import com.saju.lifepluse.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;

public class IntroActivity extends MaterialIntroActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(new SlideFragmentBuilder()
                .title("247 HOUR Service")
                .description("An ambulance is a critical emergency vehicle equipped with life-saving equipment and trained personnel to provide rapid transportation and medical assistance to those in urgent need.")
                .image(R.drawable.onboarding_1)
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.red)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Blood Bank")
                .image(R.drawable.blood_white_icon)
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.red)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("Find Your Doctor")
                .image(R.drawable.ic_launcher_background)
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.colorPrimary)
                .build());

        addSlide(new SlideFragmentBuilder()
                .title("title 4")
                .image(R.drawable.ic_launcher_background)
                .buttonsColor(R.color.black)
                .backgroundColor(R.color.colorPrimary)
                .build());


    }
}
