package com.example.myapplication.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AddFragment;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.Contact;
import com.example.myapplication.ContactAdapter;
import com.example.myapplication.R;
import com.example.myapplication.Utils.PreferencesUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_READ_CONTACTS = 1;
    public RecyclerView recyclerView;
    private FloatingActionButton fab;
    private View mLayout;
    public ContactAdapter contactAdapter;
    public List<Contact> contactList = new ArrayList<>();
    public ContentResolver resolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);


        init();
        setupAdapter();
        requestForPermission();


    }

    private void requestForPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            readContact();
        } else {
            checkPermission();
        }
    }

    private void checkPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
            showExplanation();

        } else {
            if (PreferencesUtil.isFirstTimeAskingPermission(this, Manifest.permission.READ_CONTACTS)) {
                askPermissionFirst();
            } else {
                // Permission denied by checking checkbox
                showSettings();
            }
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

    private void showExplanation() {
        // Request the permission
        ActivityCompat.requestPermissions(ContactActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS},
                PERMISSION_READ_CONTACTS);
    }

    private void askPermissionFirst() {
        PreferencesUtil.firstTimeAskingPermission(this, Manifest.permission.READ_CONTACTS, false);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACTS);
    }

    private void readContact() {

        resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String phone = "";
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id},
                            null);
                    if (phoneCursor != null) {
                        phoneCursor.moveToNext();
                        phone += phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + "\n";
                    }
                    phoneCursor.close();
                }
                contactList.add(new Contact(name, phone));
                contactAdapter.notifyDataSetChanged();
            }

            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void init() {
        recyclerView = findViewById(R.id.contact_list);
        mLayout = findViewById(R.id.layout);
        fab = findViewById(R.id.addContact);

        fab.setOnClickListener(this);
    }

    private void setupAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(this, contactList);
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_READ_CONTACTS:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContact();
                } else {
                    showAlertForRead();
                }
                break;
        }
    }

    public void showAlertForRead() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Permission Required to open this app")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        requestForPermission();
                    }
                })

                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onClick(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, new AddFragment())
                .addToBackStack(null)
                .commit();
    }


    @Override
    protected void onRestart() {
        Log.d("Activity", "Restart");
        super.onRestart();
        requestForPermission();
    }

    public void hideKeyPad() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),0);
    }


}
