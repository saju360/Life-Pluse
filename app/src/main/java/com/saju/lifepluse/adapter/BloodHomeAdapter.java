package com.saju.lifepluse.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.BloodNeedPostModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BloodHomeAdapter extends RecyclerView.Adapter<BloodHomeAdapter.BloodHomeViewHolder> {

    private final Context context;
    private BloodNeedPostModel currentPost;
    private final Handler handler;

    public BloodHomeAdapter(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void updateData(BloodNeedPostModel newPost) {
        currentPost = newPost;
        notifyDataSetChanged();
    }

    public void startDataUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateDataRandomly();
                handler.postDelayed(this, getRandomDelay());
            }
        }, getRandomDelay());
    }

    private void updateDataRandomly() {
        // Implement your logic to update data randomly here
        // For example, you can get a random post from a list of posts
         //BloodNeedPostModel randomPost = getRandomPost();
         //updateData(randomPost);
    }

    private long getRandomDelay() {
        Random random = new Random();
        return TimeUnit.SECONDS.toMillis(random.nextInt(10) + 5); // Random delay between 5 to 15 seconds
    }

    @NonNull
    @Override
    public BloodHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bloodneed_home_recyclear_sample_layout, parent, false);
        return new BloodHomeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BloodHomeViewHolder holder, int position) {
        if (currentPost != null) {

            String donationDate = currentPost.getDate();

            if (isDonationDateWithinLastTwoDays(donationDate)){
                holder.itemView.setVisibility(View.VISIBLE);
                holder.title.setText(currentPost.getBloodNeed());
                holder.dontaionDate.setText("Donation Date: " + currentPost.getDate());
                holder.bloodTypeId.setText("Blood Group: " + currentPost.getBloodgroup());
                holder.bloodQtyId.setText("Blood Quantity: " + currentPost.getBloodQty() + " Bag");
                holder.hospitalNameId.setText("Hospital: " + currentPost.getHospital());
                holder.referenceNameId.setText("Reference: " + currentPost.getReference());
                String contactNumber = currentPost.getContact();

                String postTime = currentPost.getCurrentTime();
                long timeAgoMillis = getTimeDifferenceInMillis(postTime);
                String timeAgo = getTimeAgoString(timeAgoMillis);
                holder.postTimeTextView.setText("Posted: "+timeAgo);

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
                            context.startActivity(intent);
                        } else {
                            // If contact number is empty, display a toast indicating that the number is not available
                            Toast.makeText(context, "Contact number not available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }else {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }



            // Handle contact number if needed
        }
    }

    private boolean isDonationDateWithinLastTwoDays(String donationDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date date = sdf.parse(donationDate);
            Calendar donationCal = Calendar.getInstance();
            donationCal.setTime(date);

            Calendar twoDaysAgo = Calendar.getInstance();
            twoDaysAgo.add(Calendar.DATE, -2); // Subtract 2 days

            return donationCal.after(twoDaysAgo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public int getItemCount() {
        return currentPost != null ? 1 : 0;
    }

    public static class BloodHomeViewHolder extends RecyclerView.ViewHolder {
        TextView title, dontaionDate, postTimeTextView, bloodTypeId, bloodQtyId, hospitalNameId, referenceNameId;
        LottieAnimationView callAnimationId;

        public BloodHomeViewHolder(@NonNull View itemView) {
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

}
