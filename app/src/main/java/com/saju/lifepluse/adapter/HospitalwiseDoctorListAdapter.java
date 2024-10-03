package com.saju.lifepluse.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.DoctorProfileview;
import com.saju.lifepluse.modelclass.DoctorListModel;
import com.saju.lifepluse.modelclass.HospitalwiseDoctorListModel;

import java.util.ArrayList;

public class HospitalwiseDoctorListAdapter extends RecyclerView.Adapter<HospitalwiseDoctorListAdapter.DoctorListViewHolder> {

    private Context mContext;
    private ArrayList<HospitalwiseDoctorListModel> doctorList;

    public HospitalwiseDoctorListAdapter(Context mContext, ArrayList<HospitalwiseDoctorListModel> doctorList) {
        this.mContext = mContext;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public DoctorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.doctorlist_sample_layout, parent, false);
        return new DoctorListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorListViewHolder holder, int position) {
        HospitalwiseDoctorListModel item = doctorList.get(position);

        String imgUrl = item.getDoc_image();
        Glide.with(mContext).load(imgUrl).placeholder(R.drawable.baseline_add_photo_alternate).into(holder.doctorImage);

        // Update views with data
        SharedPreferences prefs = mContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");

        if ("bn".equals(language)) {
            holder.docSNameTextId.setText(item.getDocnameBangla());
            holder.docShpAddressId.setText(item.getDoc_hpnameBangla());
            holder.docShospitalnameId.setText(item.getDoc_hpnameBangla());
            holder.docSptextId.setText(item.getDoc_spBangla());

            Log.d("hospitalname", item.getDoc_hpnameBangla());
        } else {
            holder.docSNameTextId.setText(item.getDocnameEnglish());
            holder.docShpAddressId.setText(item.getDoc_hpAddressEnglish());
            holder.docShospitalnameId.setText(item.getDoc_hpnameEnglish());
            holder.docSptextId.setText(item.getDoc_spEnglish());
            Log.d("hospitalname", item.getDoc_hpnameEnglish());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(mContext, DoctorProfileview.class);
                myIntent.putExtra("selectedDoctorType", "Hospitalwise");
                myIntent.putExtra("selectedDoctor", item);  // Pass the Parcelable object
                mContext.startActivity(myIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return doctorList.size();
    }

    public static class DoctorListViewHolder extends RecyclerView.ViewHolder {

        TextView docSNameTextId, docSqualificationTextId, docSptextId, docShospitalnameId, docShpAddressId;
        ImageView doctorImage;

        public DoctorListViewHolder(@NonNull View itemView) {
            super(itemView);
            docSNameTextId = itemView.findViewById(R.id.docSNameTextId);
            docSqualificationTextId = itemView.findViewById(R.id.docSqualificationTextId);
            docSptextId = itemView.findViewById(R.id.docSptextId);
            docShospitalnameId = itemView.findViewById(R.id.docShospitalnameId);
            docShpAddressId = itemView.findViewById(R.id.docShpAddressId);
            doctorImage = itemView.findViewById(R.id.doctorImage);
        }
    }
}
