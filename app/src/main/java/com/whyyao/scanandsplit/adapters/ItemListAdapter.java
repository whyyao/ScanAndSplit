package com.whyyao.scanandsplit.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.UI.ContactFragment;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by tristantstarck on 12/1/17.
 */

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ViewHolder> {
    private ArrayList<Item> mItems;
    private Context mContext;
    private ContactFragment mFrag;
    private ArrayList<Item> mContactItems;

    public ItemListAdapter(Context context, ArrayList<Item> data, ArrayList<Item> contactItemData, ContactFragment fragment)
    {
        mContext = context;
        mItems = new ArrayList<>(data);
        mContactItems = new ArrayList<>(contactItemData);
        mFrag = fragment;
    }

    @Override
    public ItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listitem, parent, false);
        return new ViewHolder(view);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName,itemPrice;
        ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView) itemView.findViewById(R.id.tv_item_name);
            itemPrice = itemView.findViewById(R.id.tv_item_price);
        }
    }

    @Override
    public void onBindViewHolder(final ItemListAdapter.ViewHolder holder, final int position) {
        Item item = mItems.get(position);
        holder.itemName.setText(item.getName());

        //TODO: Add support for different currencies
        holder.itemPrice.setText("$" + String.valueOf(new DecimalFormat("#0.00").format(item.getPrice())));

        //remember the positon that has already been selected
        if(mContactItems.contains(mItems.get(position))){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPicked));
        }else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorUnpicked));
        }
        //if clicked, detect if the item is selected or not. If yes, unselect it, if no, select it.
        //Update Fragment's contact for furture calculation purpose
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mContactItems.contains(mItems.get(position))){
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorUnpicked));
                    mFrag.removeItem(mItems.get(position));
                    mContactItems.remove(mItems.get(position));
                }else{
                    holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPicked));
                    mFrag.addItem(mItems.get(position));
                    mContactItems.add(mItems.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }



}
