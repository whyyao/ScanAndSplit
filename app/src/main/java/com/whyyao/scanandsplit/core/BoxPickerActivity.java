package com.whyyao.scanandsplit.core;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.UI.InteractiveReceiptActivity;
import com.whyyao.scanandsplit.UI.StartingActivity;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

import static com.whyyao.scanandsplit.core.OcrGraphic.ITEM_COLOR;
import static com.whyyao.scanandsplit.core.OcrGraphic.PRICE_COLOR;
import static com.whyyao.scanandsplit.core.OcrGraphic.TAX_COLOR;

/**
 * Created by Chandler on 12/19/2017.
 */

public class BoxPickerActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{

    private final int INTERACTIVE_RECEIPT = 19;
    private final String TAG = "BoxPickerActivity";
    public static final String ITEM = "Item";
    public static final String PRICE = "Price";
    public static final String TAX = "Tax";

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private SparseArray<TextBlock> mInitialBlocks;
    private ArrayList<TextBlock> mSelectedBlocks;
    private ArrayList<String> mSelectedBlockID;
    private TextBlockParser mParser;
    private Bitmap mBitmap;
    private ImageView mImage;
    private FloatingActionButton mFAB;
    private ToggleButton mFirstToggle;
    private Boolean firstIsOn;
    private ToggleButton mSecondToggle;
    private Boolean secondIsOn;
    private ToggleButton mThirdToggle;
    private Boolean thirdIsOn;
    private GestureDetector gestureDetector;

    public BoxPickerActivity() {

    }

    public BoxPickerActivity(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_picking_2);
        mSelectedBlocks = new ArrayList<>();
        mSelectedBlockID = new ArrayList<>();
        firstIsOn = false;
        secondIsOn = false;
        thirdIsOn = false;
        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        bindViews();
        init();
    }

    private void bindViews(){
        mImage = (ImageView) findViewById(R.id.parsedImage);
        mFAB = (FloatingActionButton) findViewById(R.id.box_finished);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);
        mFirstToggle = (ToggleButton) findViewById(R.id.toggle_1);
        mSecondToggle = (ToggleButton) findViewById(R.id.toggle_2);
        mThirdToggle = (ToggleButton) findViewById(R.id.toggle_3);
    }

    private void init(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String filepath = extras.getString("FilePath");
            Uri galUri = Uri.parse(extras.getString("GalUri"));
            if (filepath != null || galUri != null) {
                if (filepath != null) {
                    mBitmap = BitmapFactory.decodeFile(filepath);
                    mImage.setImageBitmap(mBitmap);
                } else if (galUri != null) {
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), galUri);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
                mImage.setImageBitmap(mBitmap);
                mFAB.setOnClickListener(this);
                mFirstToggle.setOnCheckedChangeListener(this);
                mSecondToggle.setOnCheckedChangeListener(this);
                mThirdToggle.setOnCheckedChangeListener(this);
                mParser = new TextBlockParser();
                mInitialBlocks = scanText(mBitmap);
                putGraphic(mInitialBlocks);
            }
        } else {
            Toast.makeText(this, "Error, please pick image again", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, StartingActivity.class); // New activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish(); // Call once you redirect to another activity
        }
    }

    public void putGraphic(SparseArray<TextBlock> items) {
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                Log.d("Processor", "Text detected! " + item.getValue());
            }
            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e) || super.onTouchEvent(e);
    }

    public void onClick(View view){
        int viewId = view.getId();
        switch(viewId) {
            case R.id.box_finished:
                if (mSelectedBlocks == null || mSelectedBlocks.size() < 3) {
                    Snackbar meSnackbar = Snackbar.make(findViewById(R.id.activity_box_picking),
                            "Please pick ONLY two boxes containing your ITEM NAMES and PRICES :)", Snackbar.LENGTH_LONG);
                    meSnackbar.show();
                    break;
                } else {
                    Bundle tempBundle = new Bundle(mParser.parse(mSelectedBlocks, mSelectedBlockID));
                    Intent intent = new Intent(this, InteractiveReceiptActivity.class);
                    intent.putExtra("Items", tempBundle.getParcelableArrayList("Items"));
                    intent.putExtra("Tax", tempBundle.getDouble("Tax"));
                    Log.i(TAG, "Tax = " + Double.toString(tempBundle.getDouble("Tax")));
                    startActivityForResult(intent, INTERACTIVE_RECEIPT);
                    break;
                }
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int buttonId = buttonView.getId();
        if (isChecked) {
            switch (buttonId) {
                case R.id.toggle_1:
                    setToggles(mFirstToggle, mSecondToggle, mThirdToggle);
                    mFirstToggle.setTextColor(ContextCompat.getColor(this, R.color.item_color));
                    firstIsOn = true; secondIsOn = false; thirdIsOn = false;
                    // TODO: Logic for color changes and onTap
                    break;
                case R.id.toggle_2:
                    setToggles(mSecondToggle, mFirstToggle, mThirdToggle);
                    mSecondToggle.setTextColor(ContextCompat.getColor(this, R.color.price_color));
                    firstIsOn = false; secondIsOn = true; thirdIsOn = false;
                    // TODO: Logic for color changes and onTap
                    break;
                case R.id.toggle_3:
                    setToggles(mThirdToggle, mFirstToggle, mSecondToggle);
                    mThirdToggle.setTextColor(ContextCompat.getColor(this, R.color.tax_color));
                    firstIsOn = false; secondIsOn = false; thirdIsOn = true;
                    // TODO: Logic for color changes and onTap
                    break;
            }
        }
    }

    private SparseArray<TextBlock> scanText(Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        SparseArray<TextBlock> items = null;
        if (!textRecognizer.isOperational()) {
            Log.e("ERROR", "Dependency not available");
            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;
            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                //Log.w(TAG, getString(R.string.low_storage_error));
            }
        } else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            items = textRecognizer.detect(frame);
        }
        return items;
    }

    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                if (firstIsOn) {
                    touchHandler(graphic, 1, ITEM);
                } else if (secondIsOn) {
                    touchHandler(graphic, 2, PRICE);
                } else if (thirdIsOn) {
                    touchHandler(graphic, 3, TAX);
                } else {
                    Toast.makeText(this, "Please pick ITEMS, PRICES, or TAX (at the top)", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        if (isFabReady()) {
            mFAB.show();
        } else {
            mFAB.hide();
        }
        return text != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == INTERACTIVE_RECEIPT) {
            // Make sure the request was successful
            if (resultCode == RESULT_CANCELED ) {
                mSelectedBlocks.clear();
                mSelectedBlocks = new ArrayList<>();
            }
        }
    }

    private void setToggles(ToggleButton activeButton, ToggleButton firstToggleButton, ToggleButton secondToggleButton) {
        activeButton.setTypeface(null, Typeface.BOLD_ITALIC);
        firstToggleButton.setChecked(false);
        firstToggleButton.setTypeface(null, Typeface.NORMAL);
        firstToggleButton.setTextColor(ContextCompat.getColor(this, R.color.default_color));
        secondToggleButton.setChecked(false);
        secondToggleButton.setTypeface(null, Typeface.NORMAL);
        secondToggleButton.setTextColor(ContextCompat.getColor(this, R.color.default_color));
    }

    /*
    * touchHandler is called when a user touches a TextBlock with one of the options selected
    *
    * @param oldGraphic - OcrGraphic to be replaced with the new one
    * @param colorId - color of the type the user is currently choosing (1, 2, 3)
    * @param type - which type of field the user is choosing (ITEM, PRICE, TAX)
    *
    */
    private void touchHandler(OcrGraphic oldGraphic, int colorId, String type) {
        OcrGraphic newGraphic;
        int colorInt = -100;
        TextBlock textblock = oldGraphic.getTextBlock();
        switch(colorId) {
            case(1):
                colorInt = ITEM_COLOR;
                break;
            case(2):
                colorInt = PRICE_COLOR;
                break;
            case(3):
                colorInt = TAX_COLOR;
                break;
        }
        // Hasn't been touched (ADD IT)
        if (oldGraphic.getColor() == oldGraphic.TEXT_COLOR ) {
            newGraphic = new OcrGraphic(mGraphicOverlay, oldGraphic.getTextBlock(), colorId);
            mGraphicOverlay.remove(oldGraphic);
            mGraphicOverlay.add(newGraphic);

            mSelectedBlocks.add(textblock);
            mSelectedBlockID.add(type);

            Log.i(TAG, "Isn't contained adding " + type);
        }
        // Same color (REMOVE IT)
        else if (oldGraphic.getColor() == colorInt) {
            newGraphic = new OcrGraphic(mGraphicOverlay, oldGraphic.getTextBlock(), -1);
            mGraphicOverlay.remove(oldGraphic);
            mGraphicOverlay.add(newGraphic);

            int position = mSelectedBlocks.indexOf(textblock);
            mSelectedBlocks.remove(textblock);
            mSelectedBlockID.remove(position);

            Log.i(TAG, "Removing " + type);
        }
        // Different color (CHANGE IT)
        else if (oldGraphic.getColor() != colorInt) {
            newGraphic = new OcrGraphic(mGraphicOverlay, oldGraphic.getTextBlock(), colorId);
            mGraphicOverlay.remove(oldGraphic);
            mGraphicOverlay.add(newGraphic);

            int position = mSelectedBlocks.indexOf(textblock);
            mSelectedBlockID.set(position, type);

            Log.i(TAG, "Changing to " + type);
        }
    }

    /*
     * @return Boolean which checks if there's at least one Iem, Price, and Tax TextBlock
     */
    private Boolean isFabReady() {
        int item = 0, price = 0, tax = 0;
        for (int i = 0; i < mSelectedBlockID.size(); i++) {
            if (mSelectedBlockID.get(i) == ITEM) {
                item++;
            } else if (mSelectedBlockID.get(i) == PRICE) {
                price++;
            } else if (mSelectedBlockID.get(i) == TAX) {
                tax++;
            }
        }
        if (item > 0 && price > 0 && tax == 1) {
            return true;
        }
        return false;
    }

}
