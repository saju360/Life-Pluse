package com.saju.lifepluse.adapter;// HorizontalAdapter class for the RecyclerView - Updated code
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.HorizontalItemModel;

import java.util.ArrayList;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

    private Context applicationContext;
    private ArrayList<HorizontalItemModel> doctorspecialityDataList;

    private OnItemClickListener mListener; // Item click listener interface

    public interface OnItemClickListener {
        void onItemClick(HorizontalItemModel item);
    }

    private void clearPreviousSelections() {
        for (HorizontalItemModel item : doctorspecialityDataList) {
            item.setSelected(false);
        }
    }



    public HorizontalAdapter(Context applicationContext, ArrayList<HorizontalItemModel> doctorspecialityDataList) {
        this.applicationContext = applicationContext;
        this.doctorspecialityDataList = doctorspecialityDataList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(applicationContext);
        View itemView = inflater.inflate(R.layout.item_horizontal_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HorizontalItemModel item = doctorspecialityDataList.get(position);

        // Highlight selected item
        if (item.isSelected()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary));
            holder.doctorSpecialtyText.setTextColor(ContextCompat.getColor(applicationContext, R.color.white));
        } else {
            // Reset background and text color to default
            holder.itemView.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.transparent));
            holder.doctorSpecialtyText.setTextColor(ContextCompat.getColor(applicationContext, R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            // Clear previous selections and toggle selection state of the clicked item
            for (HorizontalItemModel currentItem : doctorspecialityDataList) {
                if (currentItem == item) {
                    currentItem.setSelected(!currentItem.isSelected());
                } else {
                    currentItem.setSelected(false);
                }
            }

            // Notify item click if needed
            if (mListener != null) {
                mListener.onItemClick(item);
            }

            // Update the UI
            notifyDataSetChanged();
        });

        // Update views with data
        SharedPreferences prefs = applicationContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (language.equals("bn")) {
            holder.doctorSpecialtyText.setText(item.getSp_bangla());
        } else {
            holder.doctorSpecialtyText.setText(item.getSp_english());
        }
    }

    @Override
    public int getItemCount() {
        return doctorspecialityDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView doctorSpecialtyText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorSpecialtyText = itemView.findViewById(R.id.SHorizontalTextviewId);
        }
    }
}
