package com.whyyao.scanandsplit.UI;

/**
 * Created by Chandler on 12/5/17.
 */

// TODO: Make this look pretty. Search functionality. Go back ability!


import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
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

/**
* Created by Chandler Forrest on 12/5/17.
*/

// https://stackoverflow.com/questions/12413159/android-contact-picker-with-checkbox/

public class ContactPickerActivity extends ListActivity implements OnClickListener {

    // List variables
    public String[] Contacts = {};
    public int[] to = {};
    public ListView myListView;
    private FloatingActionButton save_button;
    private ArrayList<Contact> contactList = new ArrayList<>();

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

    // Setting the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.contact_picker_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
        int viewID = src.getId();
        switch(viewID){
            case R.id.save_selection:
                long[] id = getListView().getCheckedItemIds();//  i get the checked contact_id instead of position

                for (int i = 0; i < id.length; i++) {
                    contactList.add(getContact(id[i]));
                }

                for (int i = 0; i < contactList.size(); i++) {
                    Log.i("Contact Info", contactList.get(i).getName() + ", " + contactList.get(i).getPhoneNo());
                }

                Intent pickContactIntent = new Intent();
                // TODO: figure out why this is becoming null
                pickContactIntent.putParcelableArrayListExtra("CONTACTS", contactList); // Add checked contacts in intent and finish current activity.
                setResult(Activity.RESULT_OK, pickContactIntent);
                if (pickContactIntent.getParcelableArrayListExtra("CONTACTS") != null) {
                    System.out.println("Not null");
                }
                finish();

        }
    }

    private Contact getContact(long id) {
        String name = null;
        String phone = null;
        Cursor phoneCursor = null;
        Cursor nameCursor = null;

        phoneCursor = queryPhoneNumber(id);
        if (phoneCursor == null || phoneCursor.getCount() == 0) {
            // No valid number
            return null;
        } else if (phoneCursor.getCount() == 1) {
            // only one number, call it.
            phone = phoneCursor.getString(phoneCursor.getColumnIndex(Phone.NUMBER));
        } else {
            phoneCursor.moveToPosition(-1);
            while (phoneCursor.moveToNext()) {
                // Found super primary, call it.
                phone = phoneCursor.getString(phoneCursor
                        .getColumnIndex(Phone.NUMBER));
                break;

            }
        }

        nameCursor = queryPrimaryName(id);
        if (nameCursor == null || nameCursor.getCount() == 0) {
            // No valid name
            return null;
        } else if (nameCursor.getCount() == 1) {
            // one primary name
            name = nameCursor.getString(nameCursor.getColumnIndex(Phone.DISPLAY_NAME_PRIMARY));
        } else {
            // Somehow more primary names ??
            // TODO: figure this out eventually...
        }

        return(new Contact(name, phone));
    }

    private Cursor queryPrimaryName(long contactId) {
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
        Cursor c = cr.query(dataUri, new String[] {Phone._ID, Phone.DISPLAY_NAME_PRIMARY,
                        Phone.IS_SUPER_PRIMARY, ContactsContract.RawContacts.ACCOUNT_TYPE, Phone.TYPE,
                        Phone.LABEL }, ContactsContract.Contacts.Data.MIMETYPE + "=?",
                new String[] { Phone.CONTENT_ITEM_TYPE }, null);
        if (c != null && c.moveToFirst()) {
            return c;
        }
        return null;
    }


    private Cursor queryPhoneNumber(long contactId) {
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY);

        Cursor c = cr.query(dataUri, new String[] { Phone._ID, Phone.NUMBER,
                        Phone.IS_SUPER_PRIMARY, ContactsContract.RawContacts.ACCOUNT_TYPE, Phone.TYPE,
                        Phone.LABEL }, ContactsContract.Contacts.Data.MIMETYPE + "=?",
                new String[] { Phone.CONTENT_ITEM_TYPE }, null);
        if (c != null && c.moveToFirst()) {
            return c;
        }
        return null;
    }

}