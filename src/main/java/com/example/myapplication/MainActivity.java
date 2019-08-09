package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PHONE_CALL = 1;
    private EditText etData;
    private Button btnShare, btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        etData = findViewById(R.id.etData);
        btnShare = findViewById(R.id.btnShare);
        btnCall = findViewById(R.id.btnCall);

        btnShare.setOnClickListener(this);
        btnCall.setOnClickListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View view) {
        String data = etData.getText().toString();

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
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + data));
                if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
                    startActivity(i);
                }
                break;
        }
    }
}
