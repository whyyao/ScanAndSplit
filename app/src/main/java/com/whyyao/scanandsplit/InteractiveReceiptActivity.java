package com.whyyao.scanandsplit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TtsSpan;
import android.util.FloatProperty;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.whyyao.scanandsplit.helpers.ContactPicker;
import com.whyyao.scanandsplit.helpers.ContactPicker;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

/**
 * Created by tristantstarck on 12/1/17.
 */

public class InteractiveReceiptActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView list;
    private ArrayList<Item> items;
    private final int PICK_CONTACT = 2;
    private FloatingActionButton mButton;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        items = intent.getParcelableArrayListExtra("Items");
        setContentView(R.layout.activity_list_view);
        TextView testing = (TextView) findViewById(R.id.text_view_test);
        String huge_String = "";
        for (int i = 0; i < items.size(); i++) {
            huge_String = huge_String + items.get(i).toString();
        }
        testing.setText(huge_String);
        mButton = (FloatingActionButton) findViewById(R.id.add_contact);
        mButton.setOnClickListener(this);
        /*
        for (int i = 0; i < 5; i++){
            Item temp = new Item("Item" + i, i + 5);
            items.add(temp.toString());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
        list.setAdapter(arrayAdapter); */
    }

    public void onClick(View view){
        int viewId = view.getId();
        switch(viewId) {
            case R.id.add_contact:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    pickContacts();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, PICK_CONTACT);
                    Log.e("Contacts", "Not Opening");
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                        pickContacts();
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_CONTACT) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContacts();
            }
        }
    }



    protected void pickContacts(){
        Intent intent = new Intent(InteractiveReceiptActivity.this, ContactPicker.class);
        startActivity(intent);
        /*
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, PICK_CONTACT); */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            String cNumber = phones.getString(phones.getColumnIndex("data1"));
                            System.out.println("number is:" + cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        System.out.println("name is " + name);
                    }
                }
        }
    }

}
