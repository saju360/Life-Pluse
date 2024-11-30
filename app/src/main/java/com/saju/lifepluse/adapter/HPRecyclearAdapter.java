package com.saju.lifepluse.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;
import com.saju.lifepluse.activity.HospitalwithDoctorList;
import com.saju.lifepluse.modelclass.HospitalModel;

import java.util.ArrayList;

public class HPRecyclearAdapter extends RecyclerView.Adapter<HPRecyclearAdapter.hospitalviewHolder> {

    Context applicationContext;
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

        if (hospital == null || hospital.getHpname_eng() == null || hospital.getHpname_eng().isEmpty()) {
            Log.e("AdapterError", "Hospital at position " + position + " is invalid or null");
            return;  // Skip binding for invalid hospital objects
        }

        SharedPreferences prefs = applicationContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");

        if (language.equals("bn")) {
            holder.hospitalName.setText(hospital.getHpname_bang());
        } else {
            holder.hospitalName.setText(hospital.getHpname_eng());
        }

        // Check if the current user added this hospital and show delete button
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && hospital.getAddedBy() != null && hospital.getAddedBy().equals(currentUser.getUid())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
        } else {
            holder.deleteBtn.setVisibility(View.GONE);
        }

        // Set onClickListener to delete the hospital if deleteBtn is clicked
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteHospital(hospital);  // Logic to delete the hospital
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(applicationContext, HospitalwithDoctorList.class);
                intent.putExtra("hospitalnameEnglish", hospital.getHpname_eng());
                intent.putExtra("hospitalnameBangla", hospital.getHpname_bang());
                intent.putExtra("hospitaladdress", hospital.getHp_address());
                intent.putExtra("hospitalmobile", hospital.getHpmobile());
                intent.putExtra("hospitalfblink", hospital.getHp_fblink());
                intent.putExtra("hospitalweblink", hospital.getHp_websitelink());
                intent.putExtra("hospitaltwitterlink", hospital.getHp_twitterlink());
                intent.putExtra("hospitalyoutubelink", hospital.getHp_youtubelink());

                if (applicationContext instanceof Activity) {
                    applicationContext.startActivity(intent);
                } else {
                    Log.e("ContextError", "Context is not an Activity. Cannot start new Activity.");
                }

                Log.d("itemviewclicked", "Hospital name " + hospital.getHpname_eng() + hospital.getHp_address());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (hpitalListData == null || hpitalListData.isEmpty()) {
            return 0;
        }
        return hpitalListData.size();
    }

    public class hospitalviewHolder extends RecyclerView.ViewHolder {
        ImageView hospitalLogo, deleteBtn;
        TextView hospitalName;
        ImageButton nextImgBtn;

        public hospitalviewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalLogo = itemView.findViewById(R.id.ShospitalImgId);
            hospitalName = itemView.findViewById(R.id.ShoNameId);
            nextImgBtn = itemView.findViewById(R.id.SHpimgButton);
            deleteBtn = itemView.findViewById(R.id.ShospitaldelteBtn);
        }
    }

    // Method to delete hospital
    private void deleteHospital(HospitalModel hospital) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String hospitalId = hospital.getAddedBy();  // Assuming HospitalModel contains a hospital ID


        AlertDialog.Builder dialog = new AlertDialog.Builder(applicationContext);
        dialog.setTitle("Delete Hospital");
        dialog.setIcon(R.drawable.alert_icon);
        dialog.setMessage("Are you sure you want to delete this hospital?");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("users")
                        .document(hospital.getAddedBy())
                        .collection("hopital_list")
                        .document(hospitalId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(applicationContext, "Hospital deleted successfully", Toast.LENGTH_SHORT).show();
                                hpitalListData.remove(hospital);
                                notifyDataSetChanged();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FirestoreDelete", "Error deleting hospital", e);
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
}
