package com.whyyao.scanandsplit.core;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.GestureDetector;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.UI.CalculationActivity;
import com.whyyao.scanandsplit.UI.InteractiveReceiptActivity;
import com.whyyao.scanandsplit.UI.MainActivity;
import com.whyyao.scanandsplit.models.Contact;
import com.whyyao.scanandsplit.models.Item;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Chandler on 12/19/2017.
 */



public class BoxPickerActivity extends AppCompatActivity implements View.OnClickListener{
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private SparseArray<TextBlock> mInitialBlocks;
    private ArrayList<TextBlock> mSelectedBlocks;
    private TextBlockParser mParser;
    private ArrayList<Item> mItemList;
    private Bitmap mBitmap;
    private ImageView mImage;
    private FloatingActionButton mFAB;
    private String TAG = "BoxPickerActivity";
    private GestureDetector gestureDetector;

    public BoxPickerActivity() {

    }

    // TODO: Fix this to make it line up?
    public BoxPickerActivity(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_picking);
        mSelectedBlocks = new ArrayList<>();
        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        bindViews();
        init();
    }

    private void bindViews(){
        mImage = (ImageView) findViewById(R.id.parsedImage);
        mFAB = (FloatingActionButton) findViewById(R.id.finished);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);
    }

    private void init(){
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.hbc_min);
            /**
             *  * TODO: Uncomment and figure out how to get full res bitmaps from the camera...
             String extraString = extras.getString("Uri");
                try {


                    Log.i("String", extraString);
                    Uri uri = Uri.parse(extraString);
                    Log.i("init Uri", uri.toString());
                    mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);



                } catch (IOException e) {
                    e.printStackTrace();
                }
             **/
        } else {
            Log.e("Box init()", "Extras are null");
        }

        mImage.setImageBitmap(mBitmap);
        mFAB.setOnClickListener(this);
        mParser = new TextBlockParser();
        mInitialBlocks = scanText(mBitmap);
        putGraphic(mInitialBlocks);
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
        boolean c = gestureDetector.onTouchEvent(e);
        return c || super.onTouchEvent(e);
    }

    public void onClick(View view){
        int viewId = view.getId();
        switch(viewId) {
            case R.id.finished:
                Log.d("FAB","pressed");
                if (mSelectedBlocks == null || mSelectedBlocks.size() > 2 || mSelectedBlocks.get(0).equals(mSelectedBlocks.get(1))) {
                    Snackbar meSnackbar = Snackbar.make(findViewById(R.id.activity_box_picking),
                            "Please pick ONLY two boxes containing your ITEM NAMES and PRICES :)", Snackbar.LENGTH_LONG);
                    meSnackbar.show();
                } else {
                    Intent intent = new Intent(this, InteractiveReceiptActivity.class);
                    mItemList = new ArrayList<>(mParser.parse(mSelectedBlocks));
                    intent.putExtra("Items", mItemList);
                    startActivity(intent);
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
                Log.d(TAG, "text data is being spoken! " + text.getValue());
                mSelectedBlocks.add(text);
                int selectedSize = mSelectedBlocks.size();

                // This is just for the demo app...
                if (selectedSize == 1) {
                    Toast.makeText(this, "You picked the " + Integer.toString(mSelectedBlocks.size()) + "st box", Toast.LENGTH_SHORT).show();
                }
                else if (selectedSize == 2) {
                    Toast.makeText(this, "You picked the " + Integer.toString(mSelectedBlocks.size()) + "nd box", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "You picked the " + Integer.toString(mSelectedBlocks.size()) + "rd box",Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "Please pick ONLY two boxes containing your ITEM NAMES and PRICES :)",Toast.LENGTH_SHORT).show();
                    mSelectedBlocks.clear();
                }

            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Toast.makeText(this, "Nothing picked, the tap is very sensitive to the region as of right now :(", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

}
