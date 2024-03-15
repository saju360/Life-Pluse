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
        holder.bindData(position);
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

        public void bindData(int position) {
            BloodNeedPostModel model = bloodpostData.get(position);
            title.setText(model.getBloodNeed());
            dontaionDate.setText("Donation Date: " + model.getDate());
            bloodTypeId.setText("Blood Group: " + model.getBloodgroup());
            bloodQtyId.setText("Blood Quantity: " + model.getBloodQty() + " Bag");
            hospitalNameId.setText("Hospital: " + model.getHospital());
            referenceNameId.setText("Reference: " + model.getReference());

            String postTime = model.getCurrentTime();
            long timeAgoMillis = getTimeDifferenceInMillis(postTime);
            String timeAgo = getTimeAgoString(timeAgoMillis);
            postTimeTextView.setText("Posted: " + timeAgo);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                callAnimationId.setVisibility(View.GONE);
            } else {
                callAnimationId.setVisibility(View.VISIBLE);
            }

            String contactNumber = model.getContact();
            callAnimationId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(contactNumber)) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + contactNumber));
                        getcontext.startActivity(intent);
                    } else {
                        Toast.makeText(getcontext, "Contact number not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            String donationDate = model.getDate();
            if (isDonationDateOneDayAfter(donationDate)) {
                deleteDataFromFirestore(position);
                return;
            }
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

    private boolean isDonationDateOneDayAfter(String donationDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(donationDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_YEAR, 1); // Add 1 day to donation date
            Date oneDayAfterDonation = cal.getTime();
            Date currentDate = new Date(); // Current date

            SimpleDateFormat sdfComparison = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String strOneDayAfterDonation = sdfComparison.format(oneDayAfterDonation);
            String strCurrentDate = sdfComparison.format(currentDate);

            return strCurrentDate.compareTo(strOneDayAfterDonation) >= 0;
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
                            bloodpostData.remove(position);
                            notifyItemRemoved(position);
                            
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle deletion failure
                            Log.e("Firestore", "Error deleting document", e);
                            if (getcontext != null) {
                                Toast.makeText(getcontext, "Failed to delete data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Document ID is null, handle the case gracefully
            Toast.makeText(getcontext, "Document ID is null", Toast.LENGTH_SHORT).show();
        }
    }

}
