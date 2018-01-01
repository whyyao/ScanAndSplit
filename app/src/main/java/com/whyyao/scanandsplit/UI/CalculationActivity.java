package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.adapters.CalculationAdapter;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_NEUTRAL;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class CalculationActivity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener, TextWatcher {
    private final int PERMISSIONS_REQUEST_SEND_SMS = 98;
    private final String TAG = "Calculation";
    private Boolean mFlag;
    private String phoneNo;
    private String message;
    private CalculationAdapter mAdapter;

    public double mTotal;
    public double mTax;
    public double mTip;
    public static int mStackLevel;
    public ArrayList<Contact> mContacts;
    public ArrayList<Double> mMoney;
    public Map<String, Integer> mItemMap;
    public FloatingActionButton calculate;
    private FloatingActionButton mAddTip;
    private CurrencyEditText mTipInput;


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
        }else if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
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
                createTipDialog();
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_NEGATIVE:
                Log.i(TAG, "Button negative");
                dialog.dismiss();
                break;
            case BUTTON_POSITIVE:
                Log.i(TAG, "Button positive");
                dialog.dismiss();
                break;
        }
    }

    // Yuck... Wish the fragment manager would work so all these listeners wouldn't be inside this class
    // Only issue with the fragment manager is that the CurrencyEditText isn't automatically in focus
    public void createTipDialog() {

        //        FragmentManager fm = this.getFragmentManager();
        //        TipFragment dialog = new TipFragment();
        //        dialog.show(fm, "TipFragment");
        
        // Doing the above method doesn't have the Tax already in focus... maybe this can be fixed
        LayoutInflater inflater = LayoutInflater.from(this);
        View newFileView = inflater.inflate(R.layout.fragment_tip, null);

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Add a tip?");
        builder.setView(newFileView);
        mTipInput = newFileView.findViewById(R.id.tip_input);
        mTipInput.addTextChangedListener(this);
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("Cancel", this);
        builder.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            String rawVal = mTipInput.getText().toString();
            Log.i(TAG, "RawVal = " + rawVal);
            rawVal = new StringBuffer(rawVal).insert(rawVal.length()-2, ".").toString();
            mTip = Double.parseDouble(rawVal);
            Log.i(TAG, "Tip = " + Double.toString(mTip));
        } catch (Exception e) {
            // Need try catch so even if you're looking at the null value, it doesn't bug out...
        }
    }
}