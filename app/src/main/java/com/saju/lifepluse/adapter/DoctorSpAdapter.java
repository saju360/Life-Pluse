package com.saju.lifepluse.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.DoctorSpecialityModel;

import java.util.ArrayList;

public class DoctorSpAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DoctorSpecialityModel> originalDataList;  // Store the original unfiltered data
    private ArrayList<DoctorSpecialityModel> doctorspecialityDataList;  // Store the filtered data

    public DoctorSpAdapter(Context mContext, ArrayList<DoctorSpecialityModel> doctorspecialityDataList) {
        this.mContext = mContext;
        this.originalDataList = new ArrayList<>(doctorspecialityDataList);  // Create a copy of the original data
        this.doctorspecialityDataList = doctorspecialityDataList;
    }

    // Method to update the adapter data for filtering
    public void filterList(ArrayList<DoctorSpecialityModel> filteredList) {
        doctorspecialityDataList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return doctorspecialityDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return doctorspecialityDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // You might need to replace 0 with a unique identifier for your items
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gridview_layout, null);

            holder = new ViewHolder();
            holder.catImage = convertView.findViewById(R.id.catImage);
            holder.catName = convertView.findViewById(R.id.catName);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Set data to views using holder
        String imgurl = doctorspecialityDataList.get(position).getSp_image();
        Glide.with(mContext).load(imgurl).placeholder(R.drawable.baseline_add_photo_alternate).into(holder.catImage);

        SharedPreferences prefs = mContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (language.equals("bn")) {
            holder.catName.setText(doctorspecialityDataList.get(position).getSp_bangla());
        } else {
            holder.catName.setText(doctorspecialityDataList.get(position).getSp_english());
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView catImage;
        TextView catName;
    }

}
