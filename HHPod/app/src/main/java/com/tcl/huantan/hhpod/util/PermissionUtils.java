package com.tcl.huantan.hhpod.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

/**
 * Created by huantan on 16-9-24.
 */
public class PermissionUtils {
    public static final String STORAGE = Manifest.permission.READ_CONTACTS;

    public static boolean hasStoragePermission(Context context){
        return hasPermission(context,STORAGE);
    }

    private static boolean hasPermission(Context context, String permission) {
        if (!isLeastM()){
            return true;
        }
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    //If SDK version is not below M,this method will be works
    private static boolean isLeastM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
