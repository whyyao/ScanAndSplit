package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private String phoneNo;
    private String message;
    private CalculationAdapter mAdapter;

    public double mTotal;
    public ArrayList<Contact> mContacts;
    public ArrayList<Double> mMoney;
    public Map<String, Integer> mItemMap;
    public FloatingActionButton calculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        getSupportActionBar().setTitle("Confirm Items Selections");

        calculate = (FloatingActionButton) findViewById(R.id.calculate_button);
        Intent intent = getIntent();
        mTotal = 0;
        mContacts = intent.getParcelableArrayListExtra("contacts");
        mItemMap = new HashMap<>();
        mMoney = new ArrayList<>();
        initViews();
    }

    private void initViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCalculation);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        prepareData();
        mAdapter = new CalculationAdapter(mContacts, mMoney);
        recyclerView.setAdapter(mAdapter);
        TextView textView = (TextView) findViewById(R.id.textViewTotal2);
        textView.setText(String.format(Locale.CANADA, "$%.2f", mTotal));
        calculate.setOnClickListener(this);
    }

    private void buildMap() {
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
    }

    private void prepareData() {
        buildMap();
        double mSum;
        for (Contact c : mContacts) {
            mSum = 0;
            for (Item i : c.getItemList()) {
                mSum += i.getPrice() / mItemMap.get(i.getName());
            }
            mMoney.add(mSum);
        }
    }

    protected void sendSMSMessage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate_button:
                for (int i = 0; i < mContacts.size(); i++) {
                    phoneNo = mContacts.get(i).getPhoneNo();
                    message = "You owe me $" + String.format(Locale.CANADA, "%.2f", mMoney.get(i)) + ".";
                    sendSMSMessage();
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Snackbar.make(findViewById(R.id.main_layout), "SMS sent", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_layout),
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
