package com.saju.lifepluse.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.HospitalwithDoctorList;
import com.saju.lifepluse.modelclass.DoctorSpecialityModel;
import com.saju.lifepluse.modelclass.HospitalModel;

import java.util.ArrayList;

public class HPRecyclearAdapter extends RecyclerView.Adapter <HPRecyclearAdapter.hospitalviewHolder> {

    Context  applicationContext;
    ArrayList<HospitalModel> hpitalListData;
    private ArrayList<HospitalModel> originalDataList;

    public HPRecyclearAdapter(Context applicationContext, ArrayList<HospitalModel> hpitalListData) {

        this.applicationContext = applicationContext;
        this.hpitalListData = hpitalListData;
        this.originalDataList = new ArrayList<>(hpitalListData);
    }

    public void filterList(ArrayList<HospitalModel> filteredList) {
        hpitalListData = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public hospitalviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View myview = layoutInflater.inflate(R.layout.hospital_sample_layout, parent, false);

        return new hospitalviewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull hospitalviewHolder holder, int position) {
        HospitalModel hospital = hpitalListData.get(position);

        //String hp_imgurl = hpitalListData.get(position).getHp_image();

        //Glide.with(applicationContext).load(hp_imgurl).placeholder(R.drawable.baseline_add_photo_alternate).into(holder.hospitalLogo);


        SharedPreferences prefs = applicationContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (hospital!=null && language.equals("bn")){
            holder.hospitalName.setText(hospital.getHpname_bang());
        }else if (hospital!=null && language.equals("en")){
            holder.hospitalName.setText(hospital.getHpname_eng());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, HospitalwithDoctorList.class);
                // Pass hospital name values; ensure they are not null.
                intent.putExtra("hospitalnameEnglish", hospital.getHpname_eng());
                intent.putExtra("hospitalnameBangla", hospital.getHpname_bang());
                intent.putExtra("hospitaladdress", hospital.getHp_address());
                intent.putExtra("hospitalmobile", hospital.getHpmobile());
                intent.putExtra("hospitalfblink", hospital.getHp_fblink());

                // Safeguard: Check if applicationContext is correct. It should be activity context to start a new activity.
                if (applicationContext instanceof Activity) {
                    applicationContext.startActivity(intent);
                } else {
                    Log.e("ContextError", "Context is not an Activity. Cannot start new Activity.");
                }

                Log.d("itemviewclicked", "Hospital name " + hospital.getHpname_eng()+ hospital.getHp_address());
            }
        });


    }

    @Override
    public int getItemCount() {
        return hpitalListData.size();
    }

    public class hospitalviewHolder extends RecyclerView.ViewHolder{

        ImageView hospitalLogo;
        TextView hospitalName;
        ImageButton nextImgBtn;

        public hospitalviewHolder(@NonNull View itemView) {
            super(itemView);

            hospitalLogo = itemView.findViewById(R.id.ShospitalImgId);
            hospitalName = itemView.findViewById(R.id.ShoNameId);
            nextImgBtn = itemView.findViewById(R.id.SHpimgButton);

        }
    }


}

