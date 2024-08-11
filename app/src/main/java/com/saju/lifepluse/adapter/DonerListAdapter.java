package com.saju.lifepluse.adapter;

import android.content.Context;
import android.util.Log;
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

public class DonerListAdapter extends RecyclerView.Adapter<DonerListAdapter.ViewHolder> {

    Context context;
    ArrayList<BloodDonerRequestModel> allDataList;
    ArrayList<BloodDonerRequestModel> originalDataList;

    public DonerListAdapter(Context context, ArrayList<BloodDonerRequestModel> allDataList, ArrayList<BloodDonerRequestModel> originalDataList) {
        this.context = context;
        this.allDataList = allDataList;
        this.originalDataList = originalDataList; // Keep a copy of the original list
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
        viewHolder.name_tv_textview.setText("Name: " + model.getName());
        viewHolder.bloodType_tv_textview.setText(model.getBloodType());
        viewHolder.acc_datetvId.setText("Joined: " + AndroidUtil.timestampToString(model.getCreatedtime()));
        viewHolder.lastdonationId.setText(model.getDonateType());
        viewHolder.dateofbirth_tv.setText(model.getDateOfBirth());
        viewHolder.gender_tv.setText(model.getGender());
        viewHolder.marital_status_tv.setText(model.getMaritalStatus());
        viewHolder.district_tv.setText(model.getDistrict());

        if (model.getSelectedorg().isEmpty() || model.getSelectedorg() == null || model.getSelectedorg().contains("Other")) {
            viewHolder.blood_org_tv.setText(model.getSelectedmanualorg());
        } else {
            viewHolder.blood_org_tv.setText(model.getSelectedorg());
        }
    }

    @Override
    public int getItemCount() {
        return allDataList.size();
    }

    public void filterByBloodGroups(ArrayList<String> selectedBloodGroups) {
        // Check if the originalDataList has data
        if (originalDataList == null || originalDataList.isEmpty()) {
            Log.d("Filter", "originalDataList is empty or null.");
            return;
        }

        ArrayList<BloodDonerRequestModel> filteredList = new ArrayList<>();

        for (BloodDonerRequestModel model : originalDataList) {
            String modelBloodGroup = model.getBloodType() != null ? model.getBloodType().trim() : "";

            if (!modelBloodGroup.isEmpty() && selectedBloodGroups.contains(modelBloodGroup)) {
                filteredList.add(model);
                Log.d("Filter", "Matched model: " + model.getName());
            }
        }

        allDataList.clear();
        allDataList.addAll(filteredList);

        Log.d("Filter", "Filtered list size: " + filteredList.size());

        notifyDataSetChanged();
    }




    public void resetFilter() {
        allDataList.clear();
        allDataList.addAll(originalDataList); // Reset to original data
        Log.d("Filter", "Resetting filter, original list size: " + allDataList.size());
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv_textview, acc_datetvId, bloodType_tv_textview, lastdonationId, dateofbirth_tv, gender_tv, marital_status_tv, district_tv, blood_org_tv, totalDonateId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name_tv_textview = itemView.findViewById(R.id.name_tv_textview);
            bloodType_tv_textview = itemView.findViewById(R.id.bloodType_tv_textview);
            acc_datetvId = itemView.findViewById(R.id.acc_datetvId);
            lastdonationId = itemView.findViewById(R.id.lastdonationId);
            dateofbirth_tv = itemView.findViewById(R.id.dateofbirth_tv);
            gender_tv = itemView.findViewById(R.id.gender_tv);
            marital_status_tv = itemView.findViewById(R.id.marital_status_tv);
            district_tv = itemView.findViewById(R.id.district_tv);
            blood_org_tv = itemView.findViewById(R.id.blood_org_tv);
            totalDonateId = itemView.findViewById(R.id.totalDonateId);
        }
    }
}
