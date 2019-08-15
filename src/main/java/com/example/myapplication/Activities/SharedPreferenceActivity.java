package com.example.myapplication.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.R;

public class SharedPreferenceActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etName, etRoll;
    private Button btnSave, btnDelete, btnClear;
    private SharedPreferences sharedPreferences;
    private static final String PREF_FILE_NAME = "com.example.myapplication.SharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharedpref);

        init();
    }

    private void init() {
        etName = findViewById(R.id.etName);
        etRoll = findViewById(R.id.etRollNo);
        btnSave = findViewById(R.id.btnSave);
        btnClear = findViewById(R.id.btnClear);
        btnDelete = findViewById(R.id.btnDelete);
        sharedPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        hideKeyPad();
        switch (view.getId()) {
            case R.id.btnSave:
                saveData();
                break;
            case R.id.btnClear:
                clearData();
                break;
            case R.id.btnDelete:
                deleteData();
                break;
        }
    }

    private void saveData() {
        String name = etName.getText().toString().trim();
        String roll = etRoll.getText().toString().trim();

        if (name.equals("") || roll.equals("")) {
            Toast.makeText(this, "Please enter valid data !!", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", name);
        editor.putString("Roll", roll);
        Toast.makeText(this, "Data saved successfully !!", Toast.LENGTH_SHORT).show();
        editor.apply();
        etName.setText("");
        etRoll.setText("");
    }

    private void deleteData() {
        if (sharedPreferences.getString("Roll", null) != null) {
            Toast.makeText(this, "Roll No Deleted !!", Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().remove("Roll").apply();
        } else {
            Toast.makeText(this, "Please first save some data", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearData() {
        if (sharedPreferences.getString("Roll", null) != null ||
                sharedPreferences.getString("Name", null) != null) {
            Toast.makeText(this, "Cleared all Data !!", Toast.LENGTH_SHORT).show();
            sharedPreferences.edit().clear().apply();
        }else{
            Toast.makeText(this, "Please first save some data", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyPad() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

}
