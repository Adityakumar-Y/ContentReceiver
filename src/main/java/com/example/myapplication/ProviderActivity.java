package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;

public class ProviderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ContentResolver resolver;
    private Cursor cursor;
    private ArrayList<String> studList;
    private Uri uri;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);


        init();
        setupAdapter();
        fetchData();
    }

    private void setupAdapter() {
        listView.setAdapter(mAdapter);

    }

    private void init() {
        listView = (ListView) findViewById(R.id.list_item);
        studList = new ArrayList<>();
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studList);
        resolver = getContentResolver();

        listView.setOnItemClickListener(this);
    }

    private void fetchData() {

        uri = Uri.parse("content://com.example.my.provider");

        cursor = resolver.query(uri,
                null,
                null,
                null,
                null
            );

        if(cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("Name"));
                studList.add(name);
            }
        }

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        cursor.moveToFirst();
        cursor.moveToPosition(i);
        long id = cursor.getLong(cursor.getColumnIndex("_ID"));
        Log.d("ID", id+"");
        String name = adapterView.getItemAtPosition(i).toString();
        studList.remove(name);
        Toast.makeText(this, name + " is Deleted !!", Toast.LENGTH_SHORT).show();
        resolver.delete(uri,
                "Name = ?",
                new String[]{name});

        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cursor.close();
    }
}
