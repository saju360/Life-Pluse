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
import com.saju.lifepluse.modelclass.HorizontalItemModel;

import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.doctorListViewHolder> {


    private Activity mActivity;
    ArrayList<DoctorListModel> doctorlList;
    private ArrayList<DoctorListModel> originalDataList;

    public DoctorListAdapter(Activity mActivity, ArrayList<DoctorListModel> doctorlList) {

        this.mActivity = mActivity;
        this.doctorlList = doctorlList;
        this.originalDataList = new ArrayList<>(doctorlList);


    }

    public void filterList(ArrayList<DoctorListModel> filteredList) {
        doctorlList = filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public doctorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myview = inflater.inflate(R.layout.doctorlist_sample_layout, parent, false);

        return new doctorListViewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull doctorListViewHolder holder, int position) {
        DoctorListModel item = doctorlList.get(position);

        String imgurl = item.getDoc_image();

        Glide.with(mActivity).load(imgurl).placeholder(R.drawable.baseline_add_photo_alternate).into(holder.doctorImage);

        // Update views with data
        SharedPreferences prefs = mActivity.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        if (language.equals("bn")) {
            holder.docSNameTextId.setText(item.getDocnameBangla());
            holder.docShpAddressId.setText(item.getDoc_hpnameBangla());
            holder.docShospitalnameId.setText(item.getDoc_hpnameBangla());
            holder.docSptextId.setText(item.getDoc_spBangla());
        } else {
            holder.docSNameTextId.setText(item.getDocnameEnglish());
            holder.docShpAddressId.setText(item.getDoc_hpAddressEnglish());
            holder.docShospitalnameId.setText(item.getDoc_hpnameEnglish());
            holder.docSptextId.setText(item.getDoc_spEnglish());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorListModel clickedDoctor = doctorlList.get(holder.getAdapterPosition());
                Intent myintent = new Intent(mActivity, DoctorProfileview.class);
                myintent.putExtra("selectedDoctor", (Parcelable) clickedDoctor);
                mActivity.startActivity(myintent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return doctorlList.size();
    }

    public class doctorListViewHolder extends RecyclerView.ViewHolder {


        TextView docSNameTextId, docSqualificationTextId, docSptextId, docShospitalnameId, docShpAddressId;
        ImageView doctorImage;


        public doctorListViewHolder(@NonNull View itemView) {
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
