package com.example.myapplication.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class EmailIntentActvity extends AppCompatActivity implements View.OnClickListener {

    private EditText etAddress, etCC, etBCC, etSubject, etMessage;
    private TextView tvFilePath;
    private File file;
    private Uri uri;
    private Button btnSendEmail, btnSelect;
    private String receiver, receiverCC, receiverBCC, subject, message, filepath;
    private String[] receiverList, receiverCCList, receiverBCCList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_intent_actvity);

        init();
    }

    private void init() {
        etAddress = findViewById(R.id.etAddress);
        etCC = findViewById(R.id.etCC);
        etBCC = findViewById(R.id.etBCC);
        etSubject = findViewById(R.id.etSubject);
        etMessage = findViewById(R.id.etMessage);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnSelect = findViewById(R.id.btnSelect);
        tvFilePath = findViewById(R.id.tvFile);


        btnSendEmail.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSendEmail:
                getData();
                sendData();
                break;
            case R.id.btnSelect:
                selectFile();
                break;
        }
    }

    private void selectFile() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("*/*");
        startActivityForResult(i, 1);
    }

    private void getData() {
        receiver = etAddress.getText().toString();
        receiverList = receiver.split(",");
        receiverCC = etCC.getText().toString();
        receiverCCList = receiverCC.split(",");
        receiverBCC = etBCC.getText().toString();
        receiverBCCList = receiverBCC.split(",");
        subject = etSubject.getText().toString();
        message = etMessage.getText().toString();
    }

    private void sendData() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, receiverList);
        i.putExtra(Intent.EXTRA_CC, receiverCCList);
        i.putExtra(Intent.EXTRA_BCC, receiverBCCList);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);
        if(filepath != null && filepath != "") {
            i.putExtra(Intent.EXTRA_STREAM, uri);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                filepath = data.getData().getPath();
                uri = data.getData();
                Log.d("Data", data.getData().toString());
                tvFilePath.setText(filepath);
            }
        }

    }
}
