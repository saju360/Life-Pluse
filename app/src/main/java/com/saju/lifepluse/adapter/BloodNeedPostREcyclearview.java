package com.saju.lifepluse.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.BloodNeedPostModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BloodNeedPostREcyclearview extends RecyclerView.Adapter<BloodNeedPostREcyclearview.BloodNeedHolder> {

    Context getcontext;

    ArrayList<BloodNeedPostModel> bloodpostData;


    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public BloodNeedPostREcyclearview(Context context, ArrayList<BloodNeedPostModel> bloodpostData) {
        this.bloodpostData = bloodpostData;
        this.getcontext = context;
    }

    @NonNull
    @Override
    public BloodNeedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BloodNeedHolder(LayoutInflater.from(getcontext).inflate(R.layout.bloodneed_recyclear_sample_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BloodNeedHolder holder, int position) {
        holder.title.setText(bloodpostData.get(position).getBloodNeed());
        holder.dontaionDate.setText("Donation Date: " + bloodpostData.get(position).getDate());
        holder.bloodTypeId.setText("Blood Group: " + bloodpostData.get(position).getBloodgroup());
        holder.bloodQtyId.setText("Blood Quantity: " + bloodpostData.get(position).getBloodQty() + " Bag");
        holder.hospitalNameId.setText("Hospital: " + bloodpostData.get(position).getHospital());
        holder.referenceNameId.setText("Reference: " + bloodpostData.get(position).getReference());
        String contactNumber = bloodpostData.get(position).getContact();

        String postTime = bloodpostData.get(position).getCurrentTime();
        long timeAgoMillis = getTimeDifferenceInMillis(postTime);
        String timeAgo = getTimeAgoString(timeAgoMillis);
        holder.postTimeTextView.setText("Posted: " + timeAgo);


        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentuser==null){
            holder.callAnimationId.setVisibility(View.GONE);
        }else {
            holder.callAnimationId.setVisibility(View.VISIBLE);
        }

        holder.callAnimationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(contactNumber)) {
                    // If contact number is not empty, initiate a phone call
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contactNumber));
                    getcontext.startActivity(intent);
                } else {
                    // If contact number is empty, display a toast indicating that the number is not available
                    Toast.makeText(getcontext, "Contact number not available", Toast.LENGTH_SHORT).show();
                }
            }
        });


        String donationDate = bloodpostData.get(position).getDate();
        if (isDonationDateOneDayAfter(donationDate)) {
            // Hide the item view
            //holder.itemView.setVisibility(View.GONE);
            deleteDataFromFirestore(position);
            return;
        }

        // If the donation date is not one day ahead of the current date, show the item view
        //holder.itemView.setVisibility(View.VISIBLE);

    }

    @Override
    public int getItemCount() {
        return bloodpostData.size();
    }

    public class BloodNeedHolder extends RecyclerView.ViewHolder {
        TextView title, dontaionDate, postTimeTextView, bloodTypeId, bloodQtyId, hospitalNameId, referenceNameId;
        LottieAnimationView callAnimationId;

        public BloodNeedHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.bloodNeedTitleSTv);
            dontaionDate = itemView.findViewById(R.id.blooddonateDateSId);
            postTimeTextView = itemView.findViewById(R.id.postTimeTextView);
            bloodTypeId = itemView.findViewById(R.id.bloodTypeId);
            bloodQtyId = itemView.findViewById(R.id.bloodQtyId);
            hospitalNameId = itemView.findViewById(R.id.hospitalNameId);
            referenceNameId = itemView.findViewById(R.id.referenceNameId);
            callAnimationId = itemView.findViewById(R.id.callAnimationId);
        }
    }

    private long getTimeDifferenceInMillis(String postTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(postTime);
            long postTimeMillis = date.getTime();
            return System.currentTimeMillis() - postTimeMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Method to format time difference as "x minutes ago", "x hours ago", etc.
    private String getTimeAgoString(long millis) {
        if (millis < 0) {
            return "Future";
        }

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " days ago";
        } else if (hours > 0) {
            return hours + " hours ago";
        } else if (minutes > 0) {
            return minutes + " minutes ago";
        } else {
            return "Just now";
        }
    }

    //============================auto delete data from firestore
    private boolean isDonationDateOneDayAfter(String donationDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(donationDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_YEAR, 1); // Add 1 day to donation date
            Date oneDayAfterDonation = cal.getTime();
            Date currentDate = new Date(); // Current date

            // Convert both dates to "yyyy-MM-dd" format for proper comparison
            SimpleDateFormat sdfComparison = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String strOneDayAfterDonation = sdfComparison.format(oneDayAfterDonation);
            String strCurrentDate = sdfComparison.format(currentDate);

            return strCurrentDate.compareTo(strOneDayAfterDonation) >= 0; // Check if current date is after or equals one day after donation date
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deleteDataFromFirestore(int position) {
        String documentId = bloodpostData.get(position).getDocumentId();

        if (documentId != null) {
            db.collection("Blood Need Post").document(documentId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Document successfully deleted
                            Toast.makeText(getcontext, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                            bloodpostData.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, bloodpostData.size());

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors
                            Toast.makeText(getcontext, "Failed to delete data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error deleting document", e);
                        }
                    });
        } else {
            Toast.makeText(getcontext, "Document ID is null", Toast.LENGTH_SHORT).show();
        }
    }


}
