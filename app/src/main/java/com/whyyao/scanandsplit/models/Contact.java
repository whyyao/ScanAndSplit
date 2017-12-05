package com.whyyao.scanandsplit.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyao on 11/30/17.
 */

public class Contact {
    private String name;
    private String phoneNo;
    private List<Item> items;

    public Contact() {
        name = "";
        phoneNo = "";
        items = new ArrayList<>();
    }

    public Contact(String name, String phoneNo) {
        this.name = name;
        this.phoneNo = phoneNo;
        items = new ArrayList<>();
    }


    public void addItem(Item item){
        items.add(item);
    }

    public String getName(){
        return name;
    }

    public String getPhoneNo(){
        return phoneNo;
    }

    public List<Item>getItemList(){
        return items;
    }

    public int getListSize(){
        return items.size();
    }
}
