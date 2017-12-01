package com.whyyao.scanandsplit;

import android.graphics.Point;
import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by Chandler on 11/30/17.
 */

public class TextBlockParser {

    public TextBlockParser() {
        items = null;
        current = new ArrayList<String>();
    }

    public void setItems(SparseArray<TextBlock> items) {
        this.items = items;
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
        // Match things with the same relative coordinates horitonzally
        for (int i = 0; i < this.items.size(); ++i) {

            TextBlock item = items.valueAt(i);
            ArrayList<Point> pointList = new ArrayList<Point>();
            Point[] tempPoints = item.getCornerPoints();

            current.add(item.getValue());
            //
            System.out.println(i + " " + item.getValue());

            /*
            System.out.println("TOP-LEFT" + "<" + tempPoints[0].x + ", " + tempPoints[0].y + ">");
            System.out.println("TOP-RIGHT" +  "<" + tempPoints[1].x + ", " + tempPoints[1].y + ">");
            System.out.println("BOTTOM-LEFT" +  "<" + tempPoints[2].x + ", " + tempPoints[2].y + ">");
            System.out.println("BOTTOM-RIGHT" + "<" +tempPoints[3].x + ", " + tempPoints[3].y + ">\n"); */
            System.out.println("Height of current box" + (tempPoints[0].y  - tempPoints[3].y) + "\n");
            //stringBuilder.append("\n");
        }
    }

    private
        SparseArray<TextBlock> items;
        ArrayList<String> current;
}

