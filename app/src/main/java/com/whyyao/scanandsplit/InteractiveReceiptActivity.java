package com.whyyao.scanandsplit;

import android.Manifest;
import android.app.Activity;
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
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tristantstarck on 12/1/17.
 */

public class InteractiveReceiptActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView list;
    private ArrayList<Item> items;
    private HashMap<Item, Integer> itemMap;
    private final int PICK_CONTACT = 2;
    private FloatingActionButton mButton;
    private ArrayList<Contact> contacts;
    private final int REQUEST_CODE_PICK_CONTACT = 1;
    private final int CALCULATION = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        contacts = new ArrayList<>();
        itemMap = new HashMap<>();
        items = intent.getParcelableArrayListExtra("Items");
        setContentView(R.layout.activity_list_view);
        TextView testing = (TextView) findViewById(R.id.text_view_test);
        String huge_String = "";
        for (int i = 0; i < items.size(); i++) {
            huge_String = huge_String + "\n" + items.get(i).toString();
            itemMap.put(items.get(i), 0);
        }
        testing.setText(huge_String);
        mButton = (FloatingActionButton) findViewById(R.id.add_contact);
        mButton.setOnClickListener(this);
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
        startActivityForResult(intent, REQUEST_CODE_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_CODE_PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    contacts = data.getParcelableArrayListExtra("CONTACTS");
                }
                break;
        }
        makeFakeData();
    }

    // TODO: Use parts of this function to pass actual data!
    private void makeFakeData() {
        Contact c1 = new Contact("Jeff", "(000) 000-0000");
        Contact c2 = new Contact("Chan", "(111) 111-1111");
        Contact c3 = new Contact("Yao", "(222) 222-2222");
        Contact c4 = new Contact("Trist", "(333) 333-3333");

        // Adding item 1
        addItemToContact(c1, 0);
        addItemToContact(c2, 0);
        addItemToContact(c1, 1);
        addItemToContact(c1, 2);
        addItemToContact(c2, 2);
        addItemToContact(c3, 3);
        addItemToContact(c4, 4);
        addItemToContact(c1, 5);
        addItemToContact(c2, 5);
        addItemToContact(c3, 5);
        addItemToContact(c4, 5);

        Intent intent = new Intent(InteractiveReceiptActivity.this, CalculationActivity.class);
        intent.putParcelableArrayListExtra("Items", items);
        intent.putParcelableArrayListExtra("Contacts", contacts);

        Bundle extras = new Bundle();
        extras.putSerializable("ItemsMap",itemMap);
        intent.putExtras(extras);

        startActivityForResult(intent, CALCULATION);
    }

    // Takes in a contact and the item location within the items ArrayList
    private void addItemToContact(Contact c, int location) {
        Item i = items.get(location);
        c.addItem(i);
        itemMap.put(i, itemMap.get(i) + 1);
    }
}
