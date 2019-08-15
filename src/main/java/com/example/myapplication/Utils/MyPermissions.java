package com.example.myapplication.Utils;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

public class MyPermissions {

    public static void showExplanation(Context context, int permissionCode, String permissionName) {
        // Request the permission
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{permissionName},
                permissionCode);
    }

    public static void askPermissionFirst(Context context, int permissionCode, String permissionName) {
        PreferencesUtil.firstTimeAskingPermission(context, permissionName, false);
        ActivityCompat.requestPermissions((Activity) context,
                new String[]{permissionName},
                permissionCode);
    }
}
