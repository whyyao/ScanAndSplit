package com.whyyao.scanandsplit;

import android.app.PendingIntent;
import android.graphics.Point;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by Chandler on 11/30/17.
 */

public class TextBlockParser {

    private
    SparseArray<TextBlock> codedItems;
    ArrayList<String> current;

    public TextBlockParser() {
        codedItems = null;
        current = new ArrayList<String>();
    }

    public void setItems(SparseArray<TextBlock> items) {
        this.codedItems = items;
    }

    /*
        Assumes that the two longest rectanges contain the relevant data.
        Representation
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
        |  ITEM  |   | $$ |
     */
    public void test() {
        ArrayList<Integer> boxHeights = new ArrayList<Integer>();

        // Parse out heights of boxes
        for (int i = 0; i < this.codedItems.size(); ++i) {
            TextBlock item = codedItems.valueAt(i);
            Point[] tempPoints = item.getCornerPoints();
            current.add(item.getValue());
            int height = tempPoints[3].y - tempPoints[0].y;
            boxHeights.add(height);
        }

        // Find the heights of the two largest boxes
        int firstBox = 0, firstCoodinate = 0;
        int secondBox = 0, secondCoordinate = 0;
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

        // Log.d("Box 1", "" + firstBox);
        // Log.d("Box 2", "" + secondBox);

        //Log.d("Item List", itemListString);
        //Log.d("Cost List", costListString);

        String s2 = codedItems.valueAt(secondCoordinate).getValue();

        ArrayList<String> firstList = stringParser(codedItems.valueAt(firstCoodinate).getValue());
        //ArrayList<String> secondList = stringParser(s2);
        Log.d("S2", s2);
        // ArrayListPrinter(firstList);
        //ArrayListPrinter(secondList);


    }

    private ArrayList<String> stringParser(String s) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String temp = null;
            if (c == '\n') {
                temp = s.substring(0, i);
                result.add(temp);
                s.substring(i, s.length());
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

