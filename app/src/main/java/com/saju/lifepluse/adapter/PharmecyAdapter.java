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
import com.saju.lifepluse.modelclass.PharmecyModel;

import java.util.ArrayList;

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

        if (pharmecy == null || pharmecy.getPhname_bang() == null || pharmecy.getPhname_eng().isEmpty()) {
            Log.e("AdapterError", "Pharmecy at position " + position + " is invalid or null");
            return;  // Skip binding for invalid hospital objects
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
        if (pharmecyListData == null || pharmecyListData.isEmpty()) {
            return 0;
        }
        return pharmecyListData.size();
    }

    public class pharmecyviewHolder extends RecyclerView.ViewHolder {
        ImageView  deleteBtn;
        TextView pharmecyName;
        ImageButton nextImgBtn;

        public pharmecyviewHolder(@NonNull View itemView) {
            super(itemView);
            pharmecyName = itemView.findViewById(R.id.SphNameId);
            nextImgBtn = itemView.findViewById(R.id.SPhimgButton);
            deleteBtn = itemView.findViewById(R.id.SpharmecydelteBtn);
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
}
