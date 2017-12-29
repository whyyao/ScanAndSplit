package com.whyyao.scanandsplit.UI;


import android.app.DialogFragment;
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
import android.widget.EditText;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.whyyao.scanandsplit.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class TipFragment extends DialogFragment implements View.OnFocusChangeListener {
    public double mTax;
    private String TAG = "TipFragment";

    private CurrencyEditText mTaxInput;

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static TipFragment newInstance(int num) {
        TipFragment f = new TipFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();

        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tip, container, false);

        mTaxInput = (CurrencyEditText)v.findViewById(R.id.tip_input);
        mTaxInput.setOnFocusChangeListener(this);
        return v;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()) {
                case R.id.tip_input:
                    String rawVal = mTaxInput.getText().toString();
                    Log.i(TAG, "RawVal = " + rawVal);
                    rawVal = new StringBuffer(rawVal).insert(rawVal.length()-2, ".").toString();
                    mTax = Double.parseDouble(rawVal);
                    Log.i(TAG, "Tip = " + Double.toString(mTax));
                    break;
            }
        }
    }
}