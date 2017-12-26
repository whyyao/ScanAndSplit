package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.adapters.CalculationAdapter;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalculationActivity extends AppCompatActivity implements View.OnClickListener {
    private final int PERMISSIONS_REQUEST_SEND_SMS = 0;
    private final String TAG = "CalculationActivity";

    private String phoneNo;
    private String message;
    private CalculationAdapter mAdapter;

    private double mTotal;
    private double mTax;
    private ArrayList<Contact> mContacts;
    private ArrayList<Double> mContactsMoney;
    private Map<String, Integer> mItemMap;

    private FloatingActionButton calculate;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        getSupportActionBar().setTitle("Confirm Selections");
        Intent intent = getIntent();
        mContacts = intent.getParcelableArrayListExtra("contacts");
        mTax = intent.getDoubleExtra("tax", 0.0);
        mTotal = 0;
        mItemMap = new HashMap<>();
        mContactsMoney = new ArrayList<>();
        init();
    }

    private void init() {
        calculate = (FloatingActionButton) findViewById(R.id.calculate_button);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCalculation);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        prepareData();

        mAdapter = new CalculationAdapter(mContacts, mContactsMoney);
        recyclerView.setAdapter(mAdapter);

        TextView textView = (TextView) findViewById(R.id.textViewTotal2);
        textView.setText(String.format(Locale.CANADA, "$%.2f", mTotal));

        TextView textView2 = (TextView) findViewById(R.id.textViewTotal4);
        textView2.setText(String.format(Locale.CANADA, "$%.2f", mTax));

        calculate.setOnClickListener(this);
    }

    private void prepareData() {
        buildItemMap();
        double mSum = 0.0;
        for (Contact c : mContacts) {
            mSum = 0;
            for (Item i : c.getItemList()) {
                // Sum = item Price / Total amount of people who also have that item
                mSum += i.getPrice() / mItemMap.get(i.getName());
            }
        }
        mContactsMoney.add(mSum + (mTax/mContacts.size()));
    }

    private void buildItemMap() {
        int n;
        for (Contact c : mContacts) {
            for (Item i : c.getItemList()) {
                if (mItemMap.containsKey(i.getName())) {
                    n = mItemMap.get(i.getName());
                    mItemMap.put(i.getName(), n + 1);
                } else {
                    mItemMap.put(i.getName(), 1);
                    mTotal += i.getPrice();
                }
            }
        }
        mTotal = mTotal + mTax;
    }

    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        }else{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Snackbar.make(findViewById(R.id.layout_calculation), "SMS sent", Snackbar.LENGTH_LONG).show();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(CalculationActivity.this, StartingActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }, 3000);

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate_button:
                for (int i = 0; i < mContacts.size(); i++) {
                    Contact contact = mContacts.get(i);
                    if(!contact.getName().equals("ME")) {
                        phoneNo = contact.getPhoneNo();
                        message = "You owe me $" + String.format(Locale.CANADA, "%.2f", mContactsMoney.get(i)) + ".";
                        sendSMSMessage();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   sendSMSMessage();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.layout_calculation),
                            "Send SMS permission is required to send a text message", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("ENABLE", new enableSendSMSListener());
                    mySnackbar.show();
                }
                break;
        }
    }

    public class enableSendSMSListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.clearAdapter();
        mAdapter.notifyDataSetChanged();
    }
}
