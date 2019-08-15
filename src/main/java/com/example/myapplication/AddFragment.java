package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.Activities.ContactActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddFragment extends Fragment implements View.OnClickListener {

    private View view;
    private FloatingActionButton fab;
    private EditText etName, etPhone;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        init(view);
        hideFab();
        return view;
    }

    private void init(View view) {
        fab = getActivity().findViewById(R.id.addContact);
        etName = view.findViewById(R.id.etContactName);
        etPhone = view.findViewById(R.id.etPhoneNo);
        btnSave = view.findViewById(R.id.btnSaveContact);

        btnSave.setOnClickListener(this);
    }

    @SuppressLint("RestrictedApi")
    private void hideFab() {
        if (fab.getVisibility() == View.VISIBLE) {
            fab.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        ((ContactActivity) getActivity()).hideKeyPad();
        saveContact();
    }


    /*private void requestForPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            saveContact();
        } else {
            checkPermission();
        }
    }


    private void checkPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.WRITE_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
            showExplanation();

        } else {
            if (PreferencesUtil.isFirstTimeAskingPermission(getContext(), Manifest.permission.WRITE_CONTACTS)) {
                askPermissionFirst();
            } else {
                // Permission denied by checking checkbox
                showSettings();
            }
        }
    }

    private void showSettings() {
        Snackbar.make(view, R.string.open_settings,
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
        ActivityCompat.requestPermissions((Activity) getContext(),
                new String[]{Manifest.permission.WRITE_CONTACTS},
                PERMISSION_WRITE_CONTACTS);
    }

    private void askPermissionFirst() {
        PreferencesUtil.firstTimeAskingPermission(getContext(), Manifest.permission.WRITE_CONTACTS, false);
        ActivityCompat.requestPermissions((Activity) getContext(),
                new String[]{Manifest.permission.WRITE_CONTACTS}, PERMISSION_WRITE_CONTACTS);
    }
*/

    private void saveContact() {

        String contactName = etName.getText().toString().trim();
        String contactPhone = etPhone.getText().toString().trim();

        if (contactName.equals("") || contactPhone.length() != 10) {
            Toast.makeText(getContext(), "Please enter valid data !!", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList contentProviderOperations = new ArrayList();
        //insert raw contact using RawContacts.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        //insert contact display name using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName)
                .build());
        //insert mobile number using Data.CONTENT_URI
        contentProviderOperations.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contactPhone)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        try {
            getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperations);
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        ((ContactActivity) getActivity()).contactList.add(new Contact(contactName, contactPhone));
        ((ContactActivity) getActivity()).contactAdapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "Contact Added Successfully :)", Toast.LENGTH_SHORT).show();

        getActivity().getSupportFragmentManager().popBackStack();
        ((ContactActivity)getActivity()).recyclerView.scrollToPosition(((ContactActivity)getActivity()).contactAdapter.getItemCount() - 1);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void onPause() {
        fab.setVisibility(View.VISIBLE);
        super.onPause();
    }
}
