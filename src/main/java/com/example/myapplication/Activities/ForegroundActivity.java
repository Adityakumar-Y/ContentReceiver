package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.Services.ForegroundService;
import com.example.myapplication.R;

public class ForegroundActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStart, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreground);

        init();
    }

    private void init() {
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnStart:
                Log.d("Activity","Starting Service");
                startService(new Intent(this, ForegroundService.class));
                break;
            case R.id.btnStop:
                Log.d("Activity","Stopping Service");
                stopService(new Intent(this, ForegroundService.class));
                break;
        }
    }
}
