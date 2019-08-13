package com.example.myapplication.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.myapplication.BuildConfig;
import com.example.myapplication.Utils.PermissionsUtil;
import com.example.myapplication.R;
import com.google.android.material.snackbar.Snackbar;

import java.security.Permission;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CALL_PHONE = 1;
    private static final int PERMISSION_ALL= 2;
    private static final String PREFS_FILE_NAME = "MyPrefs";
    private EditText etData;
    private Button btnShare, btnCall, btnAll;
    private View mLayout;
    private String data;
    private String[] permissions;

    public static void firstTimeAskingPermission(Context context, String permission, boolean isFirstTime) {
        SharedPreferences sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply();
    }

    public static boolean isFirstTimeAskingPermission(Context context, String permission) {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


    }

    private void init() {
        etData = findViewById(R.id.etData);
        btnShare = findViewById(R.id.btnShare);
        mLayout = findViewById(R.id.layout);
        btnCall = findViewById(R.id.btnCall);
        btnAll = findViewById(R.id.btnAll);

        btnShare.setOnClickListener(this);
        btnCall.setOnClickListener(this);
        btnAll.setOnClickListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        data = etData.getText().toString();

        switch (view.getId()) {
            case R.id.btnShare:
                ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(this);
                Intent intent = intentBuilder
                        .setType("text/plain")
                        .setText(data)
                        .getIntent();

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;

            case R.id.btnCall:
                startCallPhone();
                //callPhoneProper();
                break;
            case R.id.btnAll:
                runMultiPermissions();
                break;
        }
    }

    private void runMultiPermissions() {
        permissions = new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
        };

        if(!hasPermissions(this, permissions)){
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_ALL);
        }
    }

    private void callPhoneProper() {
        PermissionsUtil.checkPermission(this, Manifest.permission.CALL_PHONE, new PermissionsUtil.PermissionAskListener() {
            @Override
            public void onNeedPermission() {
                askPermissionFirst();
            }

            @Override
            public void onPermissionPreviouslyDenied() {
                showExplanation();
            }

            @Override
            public void onPermissionDisabled() {
                showSettings();
            }

            @Override
            public void onPermissionGranted() {
                callIntent();
            }
        });
    }

    private void startCallPhone() {

        // Check for Android V23

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                    // Permission denied without checking checkbox
                    // Show an explanation to user and then again request for permission

                    showExplanation();

                } else {
                    // First Time Permission
                    if (isFirstTimeAskingPermission(this, Manifest.permission.CALL_PHONE)) {
                        askPermissionFirst();
                    } else {
                        // Permission denied by checking checkbox
                        showSettings();
                    }
                }
            } else {
                // Permission already Granted
                callIntent();

            }
        } else {
            callIntent();
        }
    }

    private void showSettings() {
        Snackbar.make(mLayout, R.string.open_settings,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request the permission
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
            }
        }).show();
    }

    private void askPermissionFirst() {
        firstTimeAskingPermission(this, Manifest.permission.CALL_PHONE, false);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_REQUEST_CALL_PHONE);

    }

    private void showExplanation() {
        Snackbar.make(mLayout, R.string.call_access_required,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        PERMISSION_REQUEST_CALL_PHONE);
            }
        }).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(mLayout, R.string.call_permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                callIntent();
            } else {
                Snackbar.make(mLayout, R.string.call_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void callIntent() {
        Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data));
        startActivity(i);
    }

    public static boolean hasPermissions(Context context, String... permissions){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context!=null && permissions!=null){
            for(String permission: permissions){
                if(ActivityCompat.checkSelfPermission(context, permission)!= PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }
}
