package com.saju.lifepluse.fragment;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.BloodNeed_Post;
import com.saju.lifepluse.activity.DrawerLayout;
import com.saju.lifepluse.adapter.BloodNeedPostREcyclearview;
import com.saju.lifepluse.modelclass.BloodNeedPostModel;

import java.util.ArrayList;


public class BloodNeed_Fragment extends Fragment {

    RecyclerView bloodRecyclearId;
    SwipeRefreshLayout swipeRefreshLayout;
    LottieAnimationView addpostAnimationId, empty_anim;
    TextView locationTextView;

    ArrayList<BloodNeedPostModel> BloodpostData;
    ArrayList<String> notifiedPosts = new ArrayList<>();

    String userDivision;
    FirebaseFirestore db;
    CollectionReference bloodPostsCollection;

    private static final String PREF_NAME = "BloodPostNotifications";
    private static final String PREF_KEY_PREFIX = "notified_post_";

    private SharedPreferences sharedPreferences;




    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View myview = inflater.inflate(R.layout.fragment_blood_need_, container, false);

        bloodRecyclearId = myview.findViewById(R.id.bloodRecyclearId);
        swipeRefreshLayout = myview.findViewById(R.id.swipeRefreshLayout);
        locationTextView = myview.findViewById(R.id.locationTextView);
        addpostAnimationId = myview.findViewById(R.id.addpostAnimationId);
        empty_anim = myview.findViewById(R.id.empty_anim);

        db = FirebaseFirestore.getInstance();

        bloodPostsCollection = db.collection("Blood Need Post");

        sharedPreferences = getActivity().getSharedPreferences("bloodpost", Context.MODE_PRIVATE);


        userDivision = DrawerLayout.userDivision;


        locationRetrive();
        dataRetrive();
        listenForNewBloodPosts();


        addpostAnimationId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), BloodNeed_Post.class));

            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataRetrive();
                // Call your data retrieval method here
                swipeRefreshLayout.setRefreshing(false); // Call this when the refresh is complete
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimaryDark);


        return myview;
    }//=============================oncreate end here================//


    private void locationRetrive() {

        if (DrawerLayout.locationText != null) {
            locationTextView.setText(DrawerLayout.locationText);
        } else {
            locationTextView.setText("Location not available");
            // Handle the case where locationText is null
        }
    }

    private void dataRetrive() {
        BloodpostData = new ArrayList<>();

        db.collection("Blood Need Post")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        if (value != null && !value.isEmpty()) {
                            BloodpostData.clear();
                            for (DocumentSnapshot document : value.getDocuments()) {
                                BloodNeedPostModel model = document.toObject(BloodNeedPostModel.class);
                                BloodpostData.add(model);
                            }


                            // Update RecyclerView adapter after retrieving all documents
                            prioritizePostsByDivision(BloodpostData, userDivision);
                            bloodRecyclearId.setLayoutManager(new LinearLayoutManager(getContext()));
                            bloodRecyclearId.setAdapter(new BloodNeedPostREcyclearview(getContext(), BloodpostData));


                            if (BloodpostData.isEmpty()) {
                                empty_anim.setVisibility(View.VISIBLE);
                            } else {
                                empty_anim.setVisibility(View.GONE);
                            }


                        } else {
                            // If there's no data, show empty animation
                            empty_anim.setVisibility(View.VISIBLE);
                        }


                    }

                });
    }


    private void prioritizePostsByDivision(ArrayList<BloodNeedPostModel> bloodpostData, String userDivision) {
        ArrayList<BloodNeedPostModel> matchedPosts = new ArrayList<>();
        ArrayList<BloodNeedPostModel> otherPosts = new ArrayList<>();

        // Iterate through bloodpostData
        for (BloodNeedPostModel post : bloodpostData) {
            if (post.getDivision() != null && post.getDivision().equals(userDivision)) {
                // If the post's division matches user's division, add it to matchedPosts
                matchedPosts.add(post);
            } else {
                // Otherwise, add it to otherPosts
                otherPosts.add(post);
            }
        }

        // Clear bloodpostData and add matched posts followed by other posts
        bloodpostData.clear();
        bloodpostData.addAll(matchedPosts);
        bloodpostData.addAll(otherPosts);
    }


    private void listenForNewBloodPosts() {
        db.collection("Blood Need Post")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle errors
                        Log.e("FirestoreListener", "Listen failed.", error);
                        return;
                    }

                    if (value != null && !value.isEmpty()) {
                        // Trigger notification for each new blood post
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                BloodNeedPostModel newPost = dc.getDocument().toObject(BloodNeedPostModel.class);
                                // Check if this post has already been notified
                                String postId = dc.getDocument().getId();
                                if (!isPostNotified(postId)) {
                                    sendNotification("New Blood Donation Post", newPost.getBloodgroup());
                                    markPostAsNotified(postId);
                                }
                            }
                        }
                    }
                });
    }

    private boolean isPostNotified(String postId) {
        return sharedPreferences.getBoolean(PREF_KEY_PREFIX + postId, false);
    }

    private void markPostAsNotified(String postId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_KEY_PREFIX + postId, true);
        editor.apply();
    }


    private void sendNotification(String title, String body) {


        Intent intent = new Intent(getContext(), DrawerLayout.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getContext(), channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title) // Assuming getTitle() is a method in BloodNeedPostModel to get post title
                        .setContentText(body) // Assuming getBody() is a method in BloodNeedPostModel to get post body
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);


        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}