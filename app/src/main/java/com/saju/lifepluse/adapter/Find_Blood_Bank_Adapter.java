package com.saju.lifepluse.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.saju.lifepluse.R;
import com.saju.lifepluse.modelclass.BloodOrganizationAddModel;
import com.saju.lifepluse.modelclass.FindBloodBankAddModel;
import com.saju.lifepluse.utils.AndroidUtil;

import java.util.ArrayList;
import java.util.List;

public class Find_Blood_Bank_Adapter extends RecyclerView.Adapter<Find_Blood_Bank_Adapter.viewholder> {

    Context context;
    ArrayList<FindBloodBankAddModel> orgallDataList;
    private ArrayList<FindBloodBankAddModel> originalDataList;
    public Find_Blood_Bank_Adapter(Context context, ArrayList<FindBloodBankAddModel> orgallDataList) {
        this.context = context;
        this.orgallDataList = orgallDataList;
        this.originalDataList = new ArrayList<>(orgallDataList);
    }

    public void filterList(ArrayList<FindBloodBankAddModel> filteredList) {
        orgallDataList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(context).inflate(R.layout.blood_doner_org_list_sample_layout, parent, false);
        return new viewholder(myview);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        FindBloodBankAddModel orgmodel = orgallDataList.get(position);

        holder.org_name.setText(orgmodel.getOrgname_Ed());
        holder.org_address.setText(orgmodel.getOrgaddress_Ed());
        holder.org_adddate.setText("Joined: "+ AndroidUtil.timestampToString(orgmodel.getAddedtime()));
        String weblink = orgmodel.getOrgweb_Ed();
        String fblink = orgmodel.getOrgfb_Ed();
        String instalink = orgmodel.getOrginsta_Ed();
        String youtlink = orgmodel.getOrgyout_Ed();
        String contactNumber = orgmodel.getOrgphone_Ed();

        holder.org_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(contactNumber)) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + contactNumber));
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "Contact number not available", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.web_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!weblink.isEmpty()){
                    openLinkInBrowser(weblink);
                }else {
                    Toast.makeText(context, "Not Have Website", Toast.LENGTH_SHORT).show();
                }

            }
        });
        holder.fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLinkInAppOrBrowser(fblink);
            }
        });
        holder.insta_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!instalink.isEmpty()){
                    openLinkInAppOrBrowser(instalink);
                }else {
                    Toast.makeText(context, "Not Have Instagram Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.yout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!youtlink.isEmpty()){
                    openLinkInAppOrBrowser(youtlink);
                }else {
                    Toast.makeText(context, "Not Have Youtube Channel", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return orgallDataList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{


        TextView org_name, org_address, org_adddate;
        LottieAnimationView org_phone;
        ImageView fb_btn, insta_btn, yout_btn, web_btn;

        public viewholder(@NonNull View itemView) {
            super(itemView);


            org_name = itemView.findViewById(R.id.org_name_tv_textview);
            org_address = itemView.findViewById(R.id.orgAddress_tv_textview);
            org_adddate = itemView.findViewById(R.id.org_acc_datetvId);
            org_phone = itemView.findViewById(R.id.org_callAnimationId);
            fb_btn = itemView.findViewById(R.id.fblink_btn);
            insta_btn = itemView.findViewById(R.id.instalink_btn);
            yout_btn = itemView.findViewById(R.id.youtlink_btn);
            web_btn = itemView.findViewById(R.id.weblink_btn);


        }
    }


    // Method to open a link in a web browser
    private void openLinkInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    // Method to open a link in the corresponding app if installed, or fallback to the web browser
    private void openLinkInAppOrBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // Check if there's an app available to handle the intent
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            context.startActivity(intent);
        } else {
            openLinkInBrowser(url);
        }
    }
}
