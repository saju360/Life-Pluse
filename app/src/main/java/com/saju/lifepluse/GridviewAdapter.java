package com.saju.lifepluse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GridviewAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<CategoryModel> dataList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public GridviewAdapter(Context context, ArrayList<CategoryModel> dataList) {
        this.mContext = context;
        this.dataList = dataList;

    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View gridView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            gridView = new View(mContext);
            gridView = inflater.inflate(R.layout.gridview_layout, null);

            ImageView catImage = gridView.findViewById(R.id.catImage);
            TextView catName = gridView.findViewById(R.id.catName);


            String imgurl = dataList.get(position).image;

            Glide.with(mContext).load(imgurl).placeholder(R.drawable.baseline_add_photo_alternate).into(catImage);


            SharedPreferences prefs = mContext.getSharedPreferences("Settings", Activity.MODE_PRIVATE);
            String language = prefs.getString("My_Lang", "");
            if (language.equals("bn")){
                catName.setText(dataList.get(position).nameBangla);
            }else {
                catName.setText(dataList.get(position).getNameEnglish());
            }

        } else {
            gridView = convertView;
        }

        return gridView;
    }
}
