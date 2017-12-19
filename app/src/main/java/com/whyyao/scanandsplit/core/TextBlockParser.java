package com.whyyao.scanandsplit.core;

import android.graphics.Point;
import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

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

    private ArrayList<TextBlock> codedItems;
    private ArrayList<Item> itemObjects;

    public TextBlockParser() {
        itemObjects = new ArrayList<>();
    }

    public ArrayList<Item> parse(ArrayList<TextBlock> codedItems) {
        ArrayList<Integer> boxHeights = new ArrayList<Integer>();

        // Parse out heights of boxes
        for (int i = 0; i < codedItems.size(); ++i) {
            TextBlock item = codedItems.get(i);
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
        if (codedItems.get(firstCoodinate).getCornerPoints()[0].x < codedItems.get(secondCoordinate).getCornerPoints()[0].x) {
            rawItems = codedItems.get(firstCoodinate).getValue();
            rawPrices = codedItems.get(secondCoordinate).getValue();
        } else {
            rawItems = codedItems.get(secondCoordinate).getValue();
            rawPrices = codedItems.get(firstCoodinate).getValue();
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
