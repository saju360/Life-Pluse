package com.saju.lifepluse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.BloodDonerRequestModel;
import com.saju.lifepluse.utils.AndroidUtil;

import java.util.ArrayList;

public class DonerListAdapter extends RecyclerView.Adapter<DonerListAdapter.ViewHolder>{

    Context context;
    ArrayList<BloodDonerRequestModel> allDataList;

    public DonerListAdapter(Context context, ArrayList<BloodDonerRequestModel> allDataList) {
        this.context = context;
        this.allDataList = allDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.blood_doner_list_sample_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        BloodDonerRequestModel model = allDataList.get(i);
        // Bind data to your ViewHolder views here
        viewHolder.name_tv_textview.setText("Name: " + model.getName());
        viewHolder.bloodType_tv_textview.setText( model.getBloodType());
        viewHolder.acc_datetvId.setText("Joined: "+ AndroidUtil.timestampToString(model.getCreatedtime()));
        viewHolder.totaldonation.setText(model.getDonateType());
        viewHolder.dateofbirth_tv.setText(model.getDateOfBirth());
        viewHolder.gender_tv.setText(model.getGender());
        viewHolder.marital_status_tv.setText(model.getMaritalStatus());
        viewHolder.district_tv.setText(model.getDistrict());


    }

    @Override
    public int getItemCount() {
        return allDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name_tv_textview, acc_datetvId, bloodType_tv_textview, totaldonation, dateofbirth_tv, gender_tv, marital_status_tv, district_tv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            name_tv_textview = itemView.findViewById(R.id.name_tv_textview);
            bloodType_tv_textview = itemView.findViewById(R.id.bloodType_tv_textview);
            acc_datetvId = itemView.findViewById(R.id.acc_datetvId);
            totaldonation = itemView.findViewById(R.id.totaldonation);
            dateofbirth_tv = itemView.findViewById(R.id.dateofbirth_tv);
            gender_tv = itemView.findViewById(R.id.gender_tv);
            marital_status_tv = itemView.findViewById(R.id.marital_status_tv);
            district_tv = itemView.findViewById(R.id.district_tv);


        }
    }

}
