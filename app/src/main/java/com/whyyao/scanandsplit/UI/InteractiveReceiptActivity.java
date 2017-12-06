package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.adapters.ContactsPagerAdapter;
import com.whyyao.scanandsplit.adapters.ItemListAdapter;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tristantstarck on 12/1/17.
 */

public class InteractiveReceiptActivity extends AppCompatActivity implements View.OnClickListener {

    public ArrayList<Item> items;
    private ArrayList<Contact> contacts;
    private HashMap<Item, Integer> itemMap;

    private FloatingActionButton mFAB;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ContactsPagerAdapter pagerAdapter;
    private Button mComfirmBtn;

    private final int ACTIVITY_PICK_CONTACT = 1;
    private final int PERMISSION_PICK_CONTACT = 2;
    private final int CALCULATION = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        bindViews();
        init();
        setupViewPager();
    }

    private void bindViews(){
        mFAB = (FloatingActionButton) findViewById(R.id.add_contact);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mComfirmBtn = (Button) findViewById(R.id.btn_confirm_calculation);
    }

    private void init(){
        Intent intent = getIntent();
        items = intent.getParcelableArrayListExtra("Items");

        itemMap = new HashMap<>();
        contacts = new ArrayList<Contact>();

        mFAB.setOnClickListener(this);
        mComfirmBtn.setOnClickListener(this);
        mToolbar.setTitle("Picking Shoppers");
        setSupportActionBar(mToolbar);
    }

    private void setupViewPager(){
        pagerAdapter = new ContactsPagerAdapter(getSupportFragmentManager());
        Contact allItem = new Contact("TOTAL","");
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        //addTap(allItem);
    }

    //adding contact to view pager
    private void addTap(Contact contact){
        ContactFragment myFrag = new ContactFragment().newInstance();
        Bundle args = new Bundle();
        args.putParcelableArrayList("items",items);
        args.putParcelable("contact", contact);
        myFrag.setArguments(args);
        pagerAdapter.addFrag(myFrag, contact);
        pagerAdapter.notifyDataSetChanged();
    }

    public void onClick(View view){
        int viewId = view.getId();
        switch(viewId) {
            case R.id.add_contact:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    pickContacts();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSION_PICK_CONTACT);
                }
                break;
            case R.id.btn_confirm_calculation:
                Log.d("taggg","pressed");
                contacts = pagerAdapter.updateContacts();

                Intent intent = new Intent(InteractiveReceiptActivity.this, CalculationActivity.class);
                intent.putParcelableArrayListExtra("contacts", contacts);
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
        Intent intent = new Intent(InteractiveReceiptActivity.this, ContactPicker.class);
        startActivityForResult(intent, ACTIVITY_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (ACTIVITY_PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    contacts = data.getParcelableArrayListExtra("CONTACTS");
                    for(int i =0;i<contacts.size(); i++){
                        addTap(contacts.get(i));
                    }
                }
                break;
            case (RESULT_CANCELED):
                break;
        }
        //makeFakeData();
    }
}
