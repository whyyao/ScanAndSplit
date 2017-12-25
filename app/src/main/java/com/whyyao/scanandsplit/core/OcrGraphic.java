/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whyyao.scanandsplit.core;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;

import java.util.List;

/**
 * Graphic instance for rendering TextBlock position, size, and ID within an associated graphic
 * overlay view.
 */
public class OcrGraphic extends GraphicOverlay.Graphic {

    private int mId;

    private final String TAG = "OcrGraphics";

    public static final int TEXT_COLOR = Color.BLACK;
    public static final int ITEM_COLOR = Color.YELLOW;
    public static final int PRICE_COLOR = Color.RED;
    public static final int TAX_COLOR = Color.GREEN;

    private Paint sRectPaint;
    private Paint sTextPaint;
    private final TextBlock mText;

    OcrGraphic(GraphicOverlay overlay, TextBlock text) {
        super(overlay);

        mText = text;

        if (sRectPaint == null) {
            sRectPaint = new Paint();
            sRectPaint.setColor(TEXT_COLOR);
            sRectPaint.setStyle(Paint.Style.STROKE);
            sRectPaint.setStrokeWidth(7.0f);
        }

        if (sTextPaint == null) {
            sTextPaint = new Paint();
            sTextPaint.setColor(TEXT_COLOR);
            sTextPaint.setTextSize(54.0f);
        }
        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    OcrGraphic(GraphicOverlay overlay, TextBlock text, Integer color) {
        super(overlay);

        int newColor = 0;

        switch(color) {
            case -1:
                newColor = TEXT_COLOR;
                break;
            case 1:
                newColor = ITEM_COLOR;
                break;
            case 2:
                newColor = PRICE_COLOR;
                break;
            case 3:
                newColor = TAX_COLOR;
                break;
        }

        mText = text;

        sRectPaint = new Paint();
        sRectPaint.setColor(newColor);
        sRectPaint.setStyle(Paint.Style.STROKE);
        sRectPaint.setStrokeWidth(7.0f);

        sTextPaint = new Paint();
        sTextPaint.setColor(newColor);
        sTextPaint.setTextSize(54.0f);

        // Redraw the overlay, as this graphic has been added.
        postInvalidate();
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public TextBlock getTextBlock() {
        return mText;
    }

    /**
     * Checks whether a point is within the bounding box of this graphic.
     * The provided point should be relative to this graphic's containing overlay.
     * @param x An x parameter in the relative context of the canvas.
     * @param y A y parameter in the relative context of the canvas.
     * @return True if the provided point is contained within this graphic's bounding box.
     */
    public boolean contains(float x, float y) {
        TextBlock text = mText;
        if (text == null) {
            return false;
        }
        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        return (rect.left < x && rect.right > x && rect.top < y && rect.bottom > y);
    }

    /**
     * Draws the text block annotations for position, size, and raw value on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        TextBlock text = mText;
        if (text == null) {
            return;
        }

        // Draws the bounding box around the TextBlock.
        RectF rect = new RectF(text.getBoundingBox());
        rect.left = translateX(rect.left);
        rect.top = translateY(rect.top);
        rect.right = translateX(rect.right);
        rect.bottom = translateY(rect.bottom);
        canvas.drawRect(rect, sRectPaint);
        // Break the text into multiple lines and draw each one according to its own bounding box.

        /*
        List<? extends Text> textComponents = text.getComponents();
        for(Text currentText : textComponents) {
            float left = translateX(currentText.getBoundingBox().left);
            float bottom = translateY(currentText.getBoundingBox().bottom);
            canvas.drawText(currentText.getValue(), left, bottom, sTextPaint);
        }
        */
    }

    // Makes a copy of current OcrGraphic but with the paint changed...
    public OcrGraphic changeColor(int color) {
        OcrGraphic result = this;
        Paint newPaint = sRectPaint;
        switch (color) {
            case 0:
                newPaint.setColor(ITEM_COLOR);
                break;
            case 1:
                newPaint.setColor(PRICE_COLOR);
                break;
            case 2:
                newPaint.setColor(TAX_COLOR);
                break;
        }
        return result;
    }

    public int getColor() {
        return sRectPaint.getColor();
    }
}
