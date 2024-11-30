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
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.saju.lifepluse.activity.HospitalwithDoctorList;
import com.saju.lifepluse.modelclass.HospitalModel;
import com.saju.lifepluse.modelclass.PharmecyModel;
import com.saju.lifepluse.utils.FirebaseUtil;

import java.util.ArrayList;
import java.util.List;

public class PharmecyAdapter extends RecyclerView.Adapter<PharmecyAdapter.pharmecyviewHolder> {

    Context applicationContext;
    ArrayList<PharmecyModel> pharmecyListData;
    private ArrayList<PharmecyModel> originalDataList;

    public PharmecyAdapter(Context applicationContext, ArrayList<PharmecyModel> pharmecyListData) {
        this.applicationContext = applicationContext;
        this.pharmecyListData = pharmecyListData;
        this.originalDataList = new ArrayList<>(pharmecyListData);
    }

    public void filterList(ArrayList<PharmecyModel> filteredList) {
        pharmecyListData = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public pharmecyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myview = layoutInflater.inflate(R.layout.pharmecy_sample_layout, parent, false);
        return new pharmecyviewHolder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull pharmecyviewHolder holder, int position) {
        PharmecyModel pharmecy = pharmecyListData.get(position);

        String weblink = pharmecy.getPh_websitelink();
        String fblink = pharmecy.getPh_fblink();
        String instalink = pharmecy.getPh_twitterlink();
        String youtlink = pharmecy.getPh_youtubelink();
        String regno = pharmecy.getPhregno();


        if (regno.isEmpty() && regno==null){
            holder.regNoId.setVisibility(View.GONE);
        }
        else {
            holder.regNoId.setText("Reg. No: "+regno);
        }


        // Check for null or invalid pharmacy object
        if (pharmecy == null || pharmecy.getPhname_bang() == null || pharmecy.getPhname_eng() == null || pharmecy.getPhname_eng().isEmpty()) {
            Log.e("AdapterError", "Pharmecy at position " + position + " is invalid or null");

            return;  // Skip further binding for this item
        }

        SharedPreferences prefs = applicationContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");

        if (language.equals("bn")) {
            holder.pharmecyName.setText(pharmecy.getPhname_bang());
        } else {
            holder.pharmecyName.setText(pharmecy.getPhname_eng());
        }

        // Check if the current user added this hospital and show delete button
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && pharmecy.getAddedBy() != null && pharmecy.getAddedBy().equals(currentUser.getUid())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        // Set onClickListener to delete the hospital if deleteBtn is clicked
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHospital(pharmecy);  // Logic to delete the hospital
            }
        });
        holder.weblink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (weblink != null && !weblink.isEmpty()){
                    openLinkInBrowser(weblink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Website", Toast.LENGTH_SHORT).show();
                }

            }
        });
        holder.fblink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fblink !=null && !fblink.isEmpty()){
                    openLinkInAppOrBrowser(fblink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Facebook Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.instalink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (instalink !=null && !instalink.isEmpty()){
                    openLinkInAppOrBrowser(instalink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Instagram Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.youtlink_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (youtlink !=null && !youtlink.isEmpty()){
                    openLinkInAppOrBrowser(youtlink);
                }else {
                    Toast.makeText(applicationContext, "Not Have Youtube Channel", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, HospitalwithDoctorList.class);
                intent.putExtra("hospitalnameEnglish", pharmecy.getHpname_eng());
                intent.putExtra("hospitalnameBangla", pharmecy.getHpname_bang());
                intent.putExtra("hospitaladdress", pharmecy.getHp_address());
                intent.putExtra("hospitalmobile", pharmecy.getHpmobile());
                intent.putExtra("hospitalfblink", pharmecy.getHp_fblink());

                if (applicationContext instanceof Activity) {
                    applicationContext.startActivity(intent);
                } else {
                    Log.e("ContextError", "Context is not an Activity. Cannot start new Activity.");
                }

                Log.d("itemviewclicked", "Hospital name " + pharmecy.getHpname_eng() + pharmecy.getHp_address());
            }
        });*/




        if (pharmecy.getPhmobile() != null && !pharmecy.getPhmobile().isEmpty()) {
            String phonenumber = pharmecy.getPhmobile();
            holder.callAnimationId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(phonenumber)) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phonenumber));
                        applicationContext.startActivity(intent);
                    } else {
                        Toast.makeText(applicationContext, "Contact number not available", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        if (pharmecy.getPh_address() !=null && !pharmecy.getPh_address().isEmpty()){

            holder.SphAddressId.setText(pharmecy.getPh_address());

        }else {
            holder.SphAddressId.setVisibility(View.GONE);

        }




    }

    @Override
    public int getItemCount() {
        if (pharmecyListData == null || pharmecyListData.isEmpty()) {
            return 0;
        }
        return pharmecyListData.size();
    }

    public class pharmecyviewHolder extends RecyclerView.ViewHolder {
        ImageView  deleteBtn, weblink_btn, fblink_btn, instalink_btn, youtlink_btn;
        TextView pharmecyName, SphAddressId, regNoId;
        LottieAnimationView callAnimationId;

        public pharmecyviewHolder(@NonNull View itemView) {
            super(itemView);
            pharmecyName = itemView.findViewById(R.id.SphNameId);
            deleteBtn = itemView.findViewById(R.id.SambulancedelteBtn);
            callAnimationId = itemView.findViewById(R.id.callAnimationId);
            SphAddressId = itemView.findViewById(R.id.SphAddressId);
            weblink_btn = itemView.findViewById(R.id.weblink_btn);
            fblink_btn = itemView.findViewById(R.id.fblink_btn);
            instalink_btn = itemView.findViewById(R.id.instalink_btn);
            youtlink_btn = itemView.findViewById(R.id.youtlink_btn);
            regNoId = itemView.findViewById(R.id.regNoId);
        }
    }

    // Method to delete hospital
    private void deleteHospital(PharmecyModel pharmecy) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String pharmecyId = pharmecy.getAddedBy();  // Assuming HospitalModel contains a hospital ID


        AlertDialog.Builder dialog = new AlertDialog.Builder(applicationContext);
        dialog.setTitle("Delete Pharmecy");
        dialog.setIcon(R.drawable.alert_icon);
        dialog.setMessage("Are you sure you want to delete this Pharmecy?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("users")
                        .document(pharmecy.getAddedBy())
                        .collection("pharmecy_list")
                        .document(pharmecyId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseUtil.currentUserDetails().update("ispharmacyadd", false);
                                FirebaseUtil.currentUserDetails().update("ispharmacynotadd", true);
                                Toast.makeText(applicationContext, "Pharmecy deleted successfully", Toast.LENGTH_SHORT).show();
                                pharmecyListData.remove(pharmecy);
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FirestoreDelete", "Error deleting pharmecy", e);
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
