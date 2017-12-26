package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.adapters.ContactsPagerAdapter;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

/**
 * Created by tristantstarck on 12/1/17.
 */

public class InteractiveReceiptActivity extends AppCompatActivity implements View.OnClickListener {

    public ArrayList<Item> mItems;
    private ArrayList<Contact> mContactsArray;
    private Double mTax;

    private FloatingActionButton mFAB;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ContactsPagerAdapter pagerAdapter;

    private final String TAG = "ReceiptActivity";
    private final int PICK_CONTACT = 1;
    private final int PERMISSION_PICK_CONTACT = 2;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager = null;
    }

    private int tabPosition;

    @Override
    protected void onStop() {
        super.onStop();
        mViewPager = null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_receipt);
        bindViews();
        init();
        setupViewPager();
    }

    private void bindViews(){
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void init(){
        Intent intent = getIntent();
        mItems = intent.getParcelableArrayListExtra("Items");
        mTax = intent.getDoubleExtra("Tax", 0.0);
        mContactsArray = new ArrayList<Contact>();
        Log.i(TAG, mTax.toString());
        mFAB.setOnClickListener(this);
        mToolbar.setTitle("Picking Shoppers");
        setSupportActionBar(mToolbar);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
               tabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(){
        pagerAdapter = new ContactsPagerAdapter(getSupportFragmentManager());
        Contact allItem = new Contact("Me",null);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        addTap(allItem);
    }

    //adding contact to view pager
    private void addTap(Contact contact) {
        mTabLayout.setVisibility(View.VISIBLE);
        ContactFragment myFrag = new ContactFragment().newInstance();
        Bundle args = new Bundle();
        args.putParcelableArrayList("items", mItems);
        args.putParcelable("contact", contact);
        myFrag.setArguments(args);
        pagerAdapter.addFrag(myFrag, contact);
        pagerAdapter.notifyDataSetChanged();

        TabLayout.Tab tab = mTabLayout.getTabAt(pagerAdapter.getCount() - 1);
        tab.select();
    }

    public void onClick(View view){
        int viewId = view.getId();
        switch(viewId) {
            case R.id.fab:
                Log.d("taggg","pressed");
                mContactsArray = pagerAdapter.updateContacts();
                Intent intent = new Intent(InteractiveReceiptActivity.this, CalculationActivity.class);
                intent.putParcelableArrayListExtra("contacts", mContactsArray);
                intent.putExtra("tax", mTax);
                startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_PICK_CONTACT) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContacts();
            }
        } else if (requestCode == RESULT_CANCELED) {
            return;
        }
    }

    protected void pickContacts(){
        Intent intent = new Intent (Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    Contact mContact = grabContactInfo(c);
                    // Checking if the contact is already displayed
                    if (mContactsArray != null) {
                        for (int i = 0; i < mContactsArray.size(); i++) {
                            if (mContactsArray.get(i).getName().equals(mContact.getName())
                                    && mContactsArray.get(i).getPhoneNo().equals(mContact.getPhoneNo())) {
                                Toast.makeText(this, "Contact already added", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    mContactsArray.add(mContact);
                    addTap(mContact);
                    break;
                }
            case (RESULT_CANCELED):
                break;
        }
    }

    private Contact grabContactInfo(Cursor c) {
        String mName = null;
        String mNumber = null;
        if (c.moveToFirst()) {
            String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (hasPhone.equalsIgnoreCase("1")) {
                Cursor phones = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                        null, null);
                phones.moveToFirst();
                mNumber = phones.getString(phones.getColumnIndex("data1"));
            }
            mName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        }
        return (new Contact(mName, mNumber));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    pickContacts();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSION_PICK_CONTACT);
                }
                return true;

            case R.id.menu_remove_contact:
                if(tabPosition == 0){
                    Snackbar meSnackbar = Snackbar.make(findViewById(R.id.layout_receipt),
                            "You can't delete yourself", Snackbar.LENGTH_SHORT);
                    meSnackbar.show();
                } else {
                    pagerAdapter.removeFrag(tabPosition);
                    mContactsArray.remove(tabPosition);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, StartingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}


