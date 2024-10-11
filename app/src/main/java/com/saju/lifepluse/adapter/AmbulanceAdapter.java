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
import com.saju.lifepluse.modelclass.AmbulanceModel;
import com.saju.lifepluse.modelclass.HospitalModel;
import com.saju.lifepluse.modelclass.PharmecyModel;

import java.util.ArrayList;

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
        TextView ambulanceName;
        ImageButton nextImgBtn;

        public ambulanceviewHolder(@NonNull View itemView) {
            super(itemView);
            ambulanceName = itemView.findViewById(R.id.SambNameId);
            nextImgBtn = itemView.findViewById(R.id.SAmbimgButton);
            deleteBtn = itemView.findViewById(R.id.SambulancedelteBtn);
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
}
