package com.whyyao.scanandsplit.helpers;

import android.app.PendingIntent;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;
import com.whyyao.scanandsplit.models.Item;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by Chandler on 11/30/17.
 */

public class TextBlockParser {


    private
        SparseArray<TextBlock> codedItems;
        ArrayList<Item> itemObjects;

    public TextBlockParser() {
        itemObjects = new ArrayList<>();
    }


    /*
        Assumes that the two longest rectanges contain the relevant data.
        Representation
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
     */

    public ArrayList<Item> parse(SparseArray<TextBlock> codedItems) {
        ArrayList<Integer> boxHeights = new ArrayList<Integer>();

        // Parse out heights of boxes
        for (int i = 0; i < codedItems.size(); ++i) {
            TextBlock item = codedItems.valueAt(i);
            Point[] tempPoints = item.getCornerPoints();
            int height = tempPoints[3].y - tempPoints[0].y;
            boxHeights.add(height);
        }

        // Find the heights of the two largest boxes, store their coordinates
        int firstBox = 0;
        int firstCoodinate = 0;
        int secondBox = 0;
        int secondCoordinate = 0;
        for (int i = 0; i < boxHeights.size(); i++) {
            if (firstBox <= boxHeights.get(i)) {
                secondBox = firstBox;
                secondCoordinate = i-1;
                firstBox = boxHeights.get(i);
                firstCoodinate = i;
            } else if (secondBox <= boxHeights.get(i)) {
                secondBox = boxHeights.get(i);
                secondCoordinate = i;
            }
        }

        String rawItems;
        String rawPrices;

        // Items will be on the left side, so its leftmost corner will have a smaller X-coordinate
        if (codedItems.valueAt(firstCoodinate).getCornerPoints()[0].x < codedItems.valueAt(secondCoordinate).getCornerPoints()[0].x) {
            rawItems = codedItems.valueAt(firstCoodinate).getValue();
            rawPrices = codedItems.valueAt(secondCoordinate).getValue();
        } else {
            rawItems = codedItems.valueAt(secondCoordinate).getValue();
            rawPrices = codedItems.valueAt(firstCoodinate).getValue();
        }

        ArrayList<String> parsedItems = stringParser(rawItems);
        ArrayList<String> parsedPrices = stringParser(rawPrices);

        for (int i = 0; i < parsedItems.size(); i++) {
            Item temp = new Item(parsedItems.get(i), Double.parseDouble(parsedPrices.get(i)));
            itemObjects.add(temp);
            temp.print();
        }

        return itemObjects;
    }

    private ArrayList<String> stringParser(String s) {
        ArrayList<String> result = new ArrayList<>();
        int position = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String temp = null;
            if (c == '\n') {
                temp = s.substring(position, i);
                result.add(temp);
                position = i;
            } else if (i == s.length()-1) {
                temp = s.substring(position, s.length());
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

