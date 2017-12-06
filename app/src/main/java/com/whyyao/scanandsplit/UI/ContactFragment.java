package com.whyyao.scanandsplit.UI;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.adapters.ItemListAdapter;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

/**
 * Created by yuanyao on 12/5/17.
 */

public class ContactFragment extends Fragment {
    private RecyclerView itemList;
    private ItemListAdapter mAdapter;


    public ContactFragment(){

    }
    public static ContactFragment newInstance() {
        Bundle args = new Bundle();
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_contact, container, false);
        itemList = fragmentView.findViewById(R.id.rv_item_list);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        ArrayList<Item> items = args.getParcelableArrayList("items");
        mAdapter = new ItemListAdapter(items);
        itemList.setLayoutManager(new LinearLayoutManager(getActivity()));
        itemList.setAdapter(mAdapter);
    }

    public Contact generateUpdatedContact(){
        return
    }
}
