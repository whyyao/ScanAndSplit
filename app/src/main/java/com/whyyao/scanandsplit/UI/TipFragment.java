package com.whyyao.scanandsplit.UI;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.whyyao.scanandsplit.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TipFragment extends DialogFragment implements TextWatcher, DialogInterface.OnClickListener {
    public static double mTip;
    public static Boolean mFlag;
    private String TAG = "TipFragment";

    private CurrencyEditText mTipInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View newFileView = inflater.inflate(R.layout.fragment_tip, null);
        android.support.v7.app.AlertDialog.Builder builder =
                new android.support.v7.app.AlertDialog.Builder(getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("Add a tip?");
        builder.setView(newFileView);
        mTipInput = newFileView.findViewById(R.id.tip_input);
        mTipInput.addTextChangedListener(this);
        builder.setPositiveButton("OK", this);
        builder.setNegativeButton("Cancel", this);
        builder.create();
        builder.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.i(TAG, "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.i(TAG, "onTextChanged");
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