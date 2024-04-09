package com.saju.lifepluse.utils;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;


public class AndroidUtil {

   public static  void showToast(Context context,String message){
       Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
    }


}
