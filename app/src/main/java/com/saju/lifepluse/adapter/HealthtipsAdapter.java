package com.saju.lifepluse.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.HealthtipsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HealthtipsAdapter extends RecyclerView.Adapter<HealthtipsAdapter.healthviewHolder> {


    Context healthtipscontext;
    ArrayList<HealthtipsModel> healthtipsListData;
    public HealthtipsAdapter(Context healthtipscontext, ArrayList<HealthtipsModel> healthtipsListData) {
        this.healthtipscontext = healthtipscontext;
        this.healthtipsListData = healthtipsListData;
    }

    @NonNull
    @Override
    public healthviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View myview = View.inflate(healthtipscontext, R.layout.health_tips_sample_layout, null);

        return new healthviewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull healthviewHolder healthviewHolder, int i) {

        HealthtipsModel healthtips = healthtipsListData.get(i);

        if (healthtips.getCover_imageurl() != null && !healthtips.getCover_imageurl().isEmpty()) {
            Picasso.get().load(healthtips.getCover_imageurl()).into(healthviewHolder.healthtipsImg);
        }

        healthviewHolder.healthtips_title.setText(healthtips.getTips_title());
        healthviewHolder.healthtips_description.setText(healthtips.getTips_desc());

        


    }

    @Override
    public int getItemCount() {
        return healthtipsListData.size();
    }

    public class healthviewHolder extends RecyclerView.ViewHolder {

        ImageView healthtipsImg;
        TextView healthtips_title, healthtips_description;

        public healthviewHolder(@NonNull View itemView) {
            super(itemView);

            healthtipsImg = itemView.findViewById(R.id.healthtipsImg);
            healthtips_title = itemView.findViewById(R.id.healthtips_title);
            healthtips_description = itemView.findViewById(R.id.healthtips_description);

        }
    }}
