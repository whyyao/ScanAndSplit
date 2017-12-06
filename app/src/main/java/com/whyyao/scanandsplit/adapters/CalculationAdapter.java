package com.whyyao.scanandsplit.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.models.Contact;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by jeffreyliu on 12/6/17.
 */

public class CalculationAdapter extends RecyclerView.Adapter<CalculationAdapter.ViewHolder> {
    private ArrayList<Contact> mContacts;
    private ArrayList<Double> mMoney;

    public CalculationAdapter(ArrayList<Contact> mContacts, ArrayList<Double> mMoney) {
        this.mContacts = mContacts;
        this.mMoney = mMoney;
    }

    @Override
    public CalculationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_calculation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalculationAdapter.ViewHolder holder, int position) {
        holder.tv_contact.setText(mContacts.get(position).getName());
        holder.tv_amount.setText(String.format(Locale.CANADA, "$%.2f", mMoney.get(position)));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_contact;
        TextView tv_amount;
        public ViewHolder(View view) {
            super(view);
            tv_contact = (TextView) view.findViewById(R.id.textViewContact);
            tv_amount = (TextView) view.findViewById(R.id.textViewAmount);
        }
    }
}
