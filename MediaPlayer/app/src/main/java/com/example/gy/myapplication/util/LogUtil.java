package com.example.gy.myapplication.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gy on 2016/10/26.
 */

public class LogUtil {
    private static final String tag = "-------------------------myLog-----------------------";
    private static boolean isOpen = true;

    public static void log(String log){
        if(isOpen){
            Log.d(tag,log);
        }
    }

    public static void toast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
