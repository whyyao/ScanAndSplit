package com.whyyao.scanandsplit.helpers;

/**
 * Created by Chandler on 12/5/17.
 */

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.models.Contact;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactPicker extends ListActivity implements OnClickListener {

    // List variables
    public String[] Contacts = {};
    public int[] to = {};
    public ListView myListView;

    FloatingActionButton save_button;
    private TextView phone;
    private String phoneNumber;
    private Cursor cursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);

        // Initializing the buttons according to their ID
        save_button = (FloatingActionButton) findViewById(R.id.save_selection);

        // Defines listeners for the buttons
        save_button.setOnClickListener(this);

        Cursor mCursor = getContacts();
        startManagingCursor(mCursor);

        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                mCursor,
                Contacts = new String[] { ContactsContract.Contacts.DISPLAY_NAME },
                to = new int[] { android.R.id.text1 });

        setListAdapter(adapter);
        myListView = getListView();
        myListView.setItemsCanFocus(false);
        myListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    }

    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";

        return managedQuery(uri, projection, selection, selectionArgs,
                sortOrder);
    }

    public void onClick(View src) {
        Intent i;
        switch (src.getId()) {
            case R.id.save_selection:

                SparseBooleanArray selectedPositions = myListView
                        .getCheckedItemPositions();
                SparseBooleanArray checkedPositions = myListView
                        .getCheckedItemPositions();
                if (checkedPositions != null) {
                    for (int k = 0; k < checkedPositions.size(); k++) {
                        if (checkedPositions.valueAt(k)) {
                            String name =
                                    ((Cursor)myListView.getAdapter().getItem(k)).getString(1);
                            Log.i("XXXX",name + " was selected");
                        }
                    }
                }

                break;
        }

    }
}