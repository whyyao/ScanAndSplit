package com.whyyao.scanandsplit.core;

import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextBlock;
import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

import static com.whyyao.scanandsplit.core.BoxPickerActivity.ITEM;
import static com.whyyao.scanandsplit.core.BoxPickerActivity.PRICE;
import static com.whyyao.scanandsplit.core.BoxPickerActivity.TAX;
import static java.lang.Math.abs;

/**
 * Created by Chandler on 11/30/17.
 */

public class TextBlockParser {

    /*
        Assumes that the two longest rectanges contain the relevant data.
        Representation
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
     */

    private final String TAG = "TextBlockParser";
    private ArrayList<Item> itemObjects;

    public TextBlockParser() {
        itemObjects = new ArrayList<>();
    }

    public Bundle parse(ArrayList<TextBlock> codedItems, ArrayList<String> codedID) {

        ArrayList<String> rawItemArray = new ArrayList<>();
        ArrayList<String> rawPriceArray = new ArrayList<>();
        String rawTaxes = "";

        // Takes all the codedItems and parses all values into there respective partitions
        for (int i = 0; i < codedItems.size(); i++) {
            if (codedID.get(i) == ITEM) {
                rawItemArray.add(codedItems.get(i).getValue());
            } else if (codedID.get(i) == PRICE) {
                rawPriceArray.add(codedItems.get(i).getValue());
            } else if (codedID.get(i) == TAX) {
                rawTaxes = (codedItems.get(i).getValue());
            }
        }

        // Parses ITEM names into it's respective Strings
        String rawItems = "";
        for (int i = 0; i < rawItemArray.size(); i++) {
            rawItems = rawItems + rawItemArray.get(i);
        }
        ArrayList<String> parsedItems = stringParser(rawItems);


        // Parses PRICES into it's respective Strings
        String rawPrices = "";
        for (int i = 0; i < rawPriceArray.size(); i++) {
            rawPrices = rawPrices + rawPriceArray.get(i);
        }
        ArrayList<String> parsedPrices = stringParser(rawPrices);

        // Creates all the Item objects from the ITEMS and PRICES
        try {
            for (int i = 0; i < parsedItems.size(); i++) {
                Item temp = new Item(parsedItems.get(i), Double.parseDouble(parsedPrices.get(i)));
                itemObjects.add(temp);
                temp.print();
            }
        } catch (java.lang.NumberFormatException e) {
            Log.e(TAG, "Can't figure out numbers");
        }

        /**
         * Parses out TAX box to their respective doubles
         * TODO: Should still grab the actual TAX, and get rid of the junk
         */

        ArrayList<String> parsedTaxes = stringParser(rawTaxes);
        ArrayList<Double> finalTaxes = new ArrayList<>();

        for (int i = 0; i < parsedTaxes.size(); i++) {
            try {
                finalTaxes.add(Double.parseDouble(parsedTaxes.get(i)));
            } catch (Exception e) {
                Log.e(TAG, "Can't figure out numbers");
            }
        }

        Double finalTax = Double.MAX_VALUE;
        // Get's the single TAX double
        for (int i = 0; i < finalTaxes.size(); i++) {
            int returnVal = Double.compare(finalTaxes.get(i), finalTax);
            System.out.println("Return val " + returnVal);
            if (returnVal < 0 ) {
                finalTax = finalTaxes.get(i);
            }
        }

        Bundle newBundle = new Bundle();
        newBundle.putSerializable("Items", itemObjects);
        newBundle.putDouble("Tax", finalTax);

        Log.i(TAG, finalTax.toString());

        return newBundle;
    }

    private ArrayList<String> stringParser(String s) {
        ArrayList<String> result = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String temp = null;
            if (c == '\n') {
                temp = s.substring(position, i).replaceAll("(\\r|\\n)", "");
                System.out.println(temp);
                result.add(temp);
                position = i;
            } else if (i == s.length()-1) {
                temp = s.substring(position, s.length()).replaceAll("(\\r|\\n)", "");
                result.add(temp);
            }
        }
        return result;
    }

    private void ArrayListPrinter(ArrayList<String> a) {
        for (int i = 0; i < a.size(); i++) {
            System.out.println(a.get(i));
        }
    }

}
