package com.whyyao.scanandsplit.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by yuanyao on 11/30/17.
 */

public class Item implements Parcelable {

    private String name;
    private double price;

    public Item() {
        name = null;
        price = 0.00;
    }

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public void setName(String data){
        name = data;
    }
    public void setPrice(double data){
        price = data;
    }
    public String getName(){
        return name;
    }
    public double getPrice(){
        return price;
    }
    public void print() {System.out.println(name + ' ' + price);}
    public String toString() {return "Item: " + getName() + "\t Price: " + getPrice();}


    protected Item(Parcel in) {
        name = in.readString();
        price = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeDouble(price);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
