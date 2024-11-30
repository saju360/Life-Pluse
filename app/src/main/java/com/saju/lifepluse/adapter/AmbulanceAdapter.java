package com.saju.lifepluse.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.HospitalwithDoctorList;
import com.saju.lifepluse.modelclass.AmbulanceModel;
import com.saju.lifepluse.modelclass.HospitalModel;
import com.saju.lifepluse.modelclass.PharmecyModel;

import java.util.ArrayList;
import java.util.List;

public class AmbulanceAdapter extends RecyclerView.Adapter<AmbulanceAdapter.ambulanceviewHolder> {

    Context applicationContext;
    ArrayList<AmbulanceModel> ambulanceListData;
    private ArrayList<AmbulanceModel> originalDataList;

    public AmbulanceAdapter(Context applicationContext, ArrayList<AmbulanceModel> ambulanceListData) {
        this.applicationContext = applicationContext;
        this.ambulanceListData = ambulanceListData;
        this.originalDataList = new ArrayList<>(ambulanceListData);
    }

    public void filterList(ArrayList<AmbulanceModel> filteredList) {
        ambulanceListData = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ambulanceviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myview = layoutInflater.inflate(R.layout.ambulance_sample_layout, parent, false);
        return new ambulanceviewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull ambulanceviewHolder holder, int position) {
        AmbulanceModel ambulance = ambulanceListData.get(position);

        if (ambulance == null || ambulance.getAmbname_bang() == null || ambulance.getAmbname_eng().isEmpty()) {
            Log.e("AdapterError", "Ambulance at position " + position + " is invalid or null");
            return;  // Skip binding for invalid hospital objects
        }
        holder.regNoId.setText("Reg. No: "+ambulance.getAmbregnumber());
        SharedPreferences prefs = applicationContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");

        if (language.equals("bn")) {
            holder.ambulanceName.setText(ambulance.getAmbname_bang());
        } else {
            holder.ambulanceName.setText(ambulance.getAmbname_eng());
        }

        // Check if the current user added this hospital and show delete button
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && ambulance.getAddedBy() != null && ambulance.getAddedBy().equals(currentUser.getUid())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        // Set onClickListener to delete the hospital if deleteBtn is clicked
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAmbulance(ambulance);  // Logic to delete the hospital
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAmbulanceBottomSheet(ambulance); // Show BottomSheetDialog
            }
        });


        String phone = ambulance.getAmbmobile();

        if (!phone.isEmpty() && phone !=null){
            holder.callAnimation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(phone)) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phone));
                        applicationContext.startActivity(intent);
                    } else {
                        Toast.makeText(applicationContext, "Contact number not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            String address = ambulance.getAmb_address();
            if (!address.isEmpty() && address !=null){
                holder.SambAddressId.setText(address);
            }else {
                holder.SambAddressId.setVisibility(View.GONE);
            }

        }



    }

    @Override
    public int getItemCount() {
        if (ambulanceListData == null || ambulanceListData.isEmpty()) {
            return 0;
        }
        return ambulanceListData.size();
    }

    public class ambulanceviewHolder extends RecyclerView.ViewHolder {
        ImageView  deleteBtn;
        TextView ambulanceName, SambAddressId, regNoId;
        LottieAnimationView callAnimation;

        public ambulanceviewHolder(@NonNull View itemView) {
            super(itemView);
            ambulanceName = itemView.findViewById(R.id.SambNameId);
            regNoId = itemView.findViewById(R.id.regNoId);
            deleteBtn = itemView.findViewById(R.id.SambulancedelteBtn);
            SambAddressId = itemView.findViewById(R.id.SambAddressId);
            callAnimation = itemView.findViewById(R.id.callAnimationId);
        }
    }

    // Method to delete ambulance
    private void deleteAmbulance(AmbulanceModel ambulance) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String ambulanceId = ambulance.getAddedBy();  // Assuming HospitalModel contains a hospital ID


        AlertDialog.Builder dialog = new AlertDialog.Builder(applicationContext);
        dialog.setTitle("Delete Ambulance");
        dialog.setIcon(R.drawable.alert_icon);
        dialog.setMessage("Are you sure you want to delete this Ambulance?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("users")
                        .document(ambulance.getAddedBy())
                        .collection("ambulance_list")
                        .document(ambulanceId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(applicationContext, "Ambulance deleted successfully", Toast.LENGTH_SHORT).show();
                                ambulanceListData.remove(ambulance);
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FirestoreDelete", "Error deleting ambulance", e);
                            }
                        });
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();






    }

    private void showAmbulanceBottomSheet(AmbulanceModel ambulance) {
        View bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.ambulance_bottom_sheet, null);

        // Initialize views in the BottomSheetDialog
        TextView nameTextView = bottomSheetView.findViewById(R.id.ambulanceName);
        TextView addressTextView = bottomSheetView.findViewById(R.id.ambulanceAddress);
        TextView mobileTextView = bottomSheetView.findViewById(R.id.ambulanceMobile);
        TextView ambulanceproviderId = bottomSheetView.findViewById(R.id.ambulanceproviderId);
        ImageView weblink_btn = bottomSheetView.findViewById(R.id.weblink_btn);
        ImageView fblink_btn = bottomSheetView.findViewById(R.id.fblink_btn);
        ImageView instalink_btn = bottomSheetView.findViewById(R.id.instalink_btn);
        ImageView youtlink_btn = bottomSheetView.findViewById(R.id.youtlink_btn);
        Button callButton = bottomSheetView.findViewById(R.id.callAmbulanceBtn);

        // Set ambulance details
        SharedPreferences prefs = applicationContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        nameTextView.setText(language.equals("bn") ? ambulance.getAmbname_bang() : ambulance.getAmbname_eng());
        addressTextView.setText(ambulance.getAmb_address());
        mobileTextView.setText(ambulance.getAmbmobile());
        ambulanceproviderId.setText("Provider: "+ambulance.getAmbprovider());


        // Handle call button click
        callButton.setOnClickListener(v -> {
            String phone = ambulance.getAmbmobile();
            if (!TextUtils.isEmpty(phone)) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                applicationContext.startActivity(intent);
            } else {
                Toast.makeText(applicationContext, "Contact number not available", Toast.LENGTH_SHORT).show();
            }
        });
        fblink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fbLink = ambulance.getAmb_fblink();
                if (fbLink !=null && !fbLink.isEmpty()){
                    openLinkInAppOrBrowser(fbLink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Facebook Account", Toast.LENGTH_SHORT).show();
                }
            }
        });

        instalink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String instalink = ambulance.getAmb_twitterlink();

                if (instalink !=null && !instalink.isEmpty()){
                    openLinkInAppOrBrowser(instalink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Instagram Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
        youtlink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String youtlink = ambulance.getAmb_youtubelink();

                if (youtlink !=null && !youtlink.isEmpty()){
                    openLinkInAppOrBrowser(youtlink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Youtube Channel", Toast.LENGTH_SHORT).show();
                }
            }
        });
        weblink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String weblink = ambulance.getAmb_websitelink();

                if (weblink != null && !weblink.isEmpty()){
                    openLinkInBrowser(weblink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Website", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Create and show the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(applicationContext);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    // Method to open a link in a web browser
    private void openLinkInBrowser(String url) {
        url = ensureValidUrl(url);
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(applicationContext, "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            applicationContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(applicationContext, "No application can handle this request. Please install a web browser.", Toast.LENGTH_LONG).show();
            Log.e("LinkError", "ActivityNotFoundException: " + e.getMessage());
        }
    }


    private String ensureValidUrl(String url) {
        if (url != null && !(url.startsWith("http://") || url.startsWith("https://"))) {
            return "http://" + url; // Prepend default scheme if missing
        }
        return url;
    }


    // Method to open a link in the corresponding app if installed, or fallback to the web browser
    private void openLinkInAppOrBrowser(String url) {
        url = ensureValidUrl(url);
        if (url == null || url.trim().isEmpty()) {
            Toast.makeText(applicationContext, "Invalid URL", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PackageManager packageManager = applicationContext.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (activities.size() > 0) {
            try {
                applicationContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(applicationContext, "No application can handle this request.", Toast.LENGTH_LONG).show();
                Log.e("LinkError", "ActivityNotFoundException: " + e.getMessage());
            }
        } else {
            openLinkInBrowser(url); // Fallback to browser
        }
    }
}
