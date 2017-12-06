package com.whyyao.scanandsplit.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanyao on 11/30/17.
 */

public class Contact implements Parcelable {
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

    public void removeItem(Item item){
        if (! items.contains(item)){
            return;
        }
        else{
            for (int i =0 ; i< items.size(); i++){
                if(items.get(i).equals(item)){
                    items.remove(i);
                    i--;
                }
            }
        }
    }

    public void setItemList(List<Item> item){
        items = item;
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

    protected Contact(Parcel in) {
        name = in.readString();
        phoneNo = in.readString();
        if (in.readByte() == 0x01) {
            items = new ArrayList<Item>();
            in.readList(items, Item.class.getClassLoader());
        } else {
            items = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phoneNo);
        if (items == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(items);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}