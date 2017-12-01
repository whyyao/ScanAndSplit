package com.whyyao.scanandsplit.models;

/**
 * Created by yuanyao on 11/30/17.
 */

public class Item {

    private String name;
    private double price;


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

}
