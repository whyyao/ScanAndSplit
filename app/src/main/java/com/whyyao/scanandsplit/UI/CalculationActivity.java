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
import android.telephony.SmsManager;
import android.view.View;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class CalculationActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PERMISSIONS_REQUEST_SEND_SMS = 0;
    private String phoneNo;
    private String message;
    private ArrayList<Double> mMoney;
    private FloatingActionButton calculate;

    private ArrayList<Contact> mContacts;
    private Map<Item, Integer> mItemMap;

    @Override
    // Interactive Recepit Activity's makeFakeData() method is passing control to this
    // I'll let you figure out what to do with itent's data
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        calculate = (FloatingActionButton) findViewById(R.id.calculate_button);
        // TODO: get mContacts and mItemMap from intent
        double mSum;
        mMoney = new ArrayList<>();
        for (Contact c : mContacts) {
            mSum = 0;
            for (Item i : c.getItemList()) {
                mSum += i.getPrice() / mItemMap.get(i);
            }
            mMoney.add(mSum);
            // TODO: put c.getName and String.format("%.2f", mSum) in textViews
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
}
