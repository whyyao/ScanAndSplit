package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.core.BoxPickerActivity;
import com.whyyao.scanandsplit.core.TextBlockParser;
import com.whyyao.scanandsplit.models.Item;

import org.opencv.android.OpenCVLoader;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    static{
        if(!OpenCVLoader.initDebug()){
            Log.d("MainActivity","OpenCV not loaded");
        }
        else{
            Log.d("MainActivity","OpenCV is loaded");
        }
    }

    private final int PERMISSIONS_REQUEST_CAMERA = 0;
    private final int PERMISSIONS_REQUEST_STORAGE = 2;
    int preference = ScanConstants.OPEN_CAMERA;
    int REQUEST_CODE = 99;
    private ImageButton mCam;
    private TextView mText;
    private ImageView mImage;
    private TextBlockParser parser;
    private ArrayList<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        init();
    }

    private void bindView(){
        mCam = (ImageButton) findViewById(R.id.cam_button);
        mImage = (ImageView) findViewById(R.id.imageview);
    }

    private void init(){
        parser = new TextBlockParser();
        mCam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Uri uri = Uri.parse("android.resource://com.whyyao.scanandsplit/drawable/hbc_min");
                //startBoxPickerActivity(uri);
                launchCam();
            }
        });
    }

    private void launchCam(){
        //check camera permission
        if((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_STORAGE );
        }
        else if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        } else{
            Intent intent = new Intent(this, ScanActivity.class);
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String filePath = data.getExtras().getString("FilePath");

            startBoxPickerActivity(filePath);
        }
    }

    private SparseArray<TextBlock> scanText(Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        SparseArray<TextBlock> items = null;
        if (!textRecognizer.isOperational()) {
            Log.e("ERROR", "Dependency not available");
        } else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            items = textRecognizer.detect(frame);
        }
        return items;
    }

    private void startBoxPickerActivity(String filepath) {
        if (filepath != null) {
            Log.i("startBoxPicker", "bitmap not null");
        }
        Intent intent = new Intent(MainActivity.this, BoxPickerActivity.class);
        intent.putExtra("FilePath", filepath);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCam();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_layout),
                            "Camera permission is required to scan your receipt", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("ENABLE", new enableCamListener());
                    mySnackbar.show();
                }
                return;
            case  PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCam();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_layout),
                            "Storage permission is required to scan your receipt", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("ENABLE", new enableStorageListener());
                    mySnackbar.show();
                }
                return;
        }
    }

    public class enableCamListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        }
    }
    public class enableStorageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_STORAGE);
        }
    }


}


