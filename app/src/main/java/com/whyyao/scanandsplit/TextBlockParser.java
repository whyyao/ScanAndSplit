package com.whyyao.scanandsplit;

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
        // Match things with the same relative coordinates horitonzally
        // Parse out heights of boxes
        for (int i = 0; i < this.codedItems.size(); ++i) {
            TextBlock item = codedItems.valueAt(i);
            ArrayList<Point> pointList = new ArrayList<Point>();
            Point[] tempPoints = item.getCornerPoints();
            current.add(item.getValue());

            /*
            System.out.println(i + " " + item.getValue());
            System.out.println("TOP-LEFT" + "<" + tempPoints[0].x + ", " + tempPoints[0].y + ">");
            System.out.println("TOP-RIGHT" +  "<" + tempPoints[1].x + ", " + tempPoints[1].y + ">");
            System.out.println("BOTTOM-LEFT" +  "<" + tempPoints[2].x + ", " + tempPoints[2].y + ">");
            System.out.println("BOTTOM-RIGHT" + "<" +tempPoints[3].x + ", " + tempPoints[3].y + ">\n");
            System.out.println("Height of current box" + (tempPoints[0].y  - tempPoints[3].y) + "\n"); */

            int height = tempPoints[3].y - tempPoints[0].y;
            Log.d("Original Heights", "" + height);
            boxHeights.add(height);

            //stringBuilder.append("\n");
        }

        int firstBox = 0;
        int secondBox = 0;
        for (int i = 0; i < boxHeights.size(); i++) {
            if (firstBox <= boxHeights.get(i)) {
                secondBox = firstBox;
                firstBox = boxHeights.get(i);
            } else if (secondBox <= boxHeights.get(i)) {
                secondBox = boxHeights.get(i);
            }
        }
        
        Log.d("Box 1", "" + firstBox);
        Log.d("Box 2", "" + secondBox);


    }


}

