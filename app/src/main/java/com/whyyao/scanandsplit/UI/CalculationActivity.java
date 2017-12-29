package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
    private String phoneNo;
    private String message;
    private CalculationAdapter mAdapter;

    public double mTotal;
    public double mTax;
    public static int mStackLevel;
    public ArrayList<Contact> mContacts;
    public ArrayList<Double> mMoney;
    public Map<String, Integer> mItemMap;
    public FloatingActionButton calculate;
    private FloatingActionButton mAddTip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStackLevel = 0;
        setContentView(R.layout.activity_calculation);
        getSupportActionBar().setTitle("Confirm Items Selections");
        calculate = (FloatingActionButton) findViewById(R.id.calculate_button);
        Intent intent = getIntent();
        mTotal = 0;
        mTax = intent.getDoubleExtra("tax", 0.0);
        Log.i("Tax", Double.toString(mTax));
        mContacts = intent.getParcelableArrayListExtra("contacts");
        mItemMap = new HashMap<>();
        mMoney = new ArrayList<>();
        initViews();
    }

    private void initViews() {
        mAddTip = (FloatingActionButton) findViewById(R.id.add_tip);
        mAddTip.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCalculation);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        prepareData();

        mAdapter = new CalculationAdapter(mContacts, mMoney);

        recyclerView.setAdapter(mAdapter);
        TextView textView = (TextView) findViewById(R.id.textViewTotal2);
        textView.setText(String.format(Locale.CANADA, "$%.2f", mTotal));
        TextView taxView = (TextView) findViewById(R.id.textViewTotal4);
        taxView.setText(String.format(Locale.CANADA, "$%.2f", mTax));
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
        mTotal = mTotal + mTax;
    }

    private void prepareData() {
        buildMap();
        double mSum;
        for (Contact c : mContacts) {
            mSum = 0;
            for (Item i : c.getItemList()) {
                mSum += i.getPrice() / mItemMap.get(i.getName());
            }
            Double individualTax = mTax/mContacts.size();
            mMoney.add(mSum + individualTax);
        }
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
                    if (!contact.getName().equals("ME")) {
                        phoneNo = contact.getPhoneNo();
                        message = "You owe me $" + String.format(Locale.CANADA, "%.2f", mMoney.get(i)) + ".";
                        sendSMSMessage();
                    }
                }
                break;
            case R.id.add_tip:
                showTip();
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

    void showTip() {
        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = TipFragment.newInstance(mStackLevel);
        newFragment.show(ft, "dialog");
    }
}