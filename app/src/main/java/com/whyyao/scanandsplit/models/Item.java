package com.whyyao.scanandsplit.models;

/**
 * Created by yuanyao on 11/30/17.
 */

public class Item {

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

    public void print() {
        System.out.println(name + " " + price);
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

}
