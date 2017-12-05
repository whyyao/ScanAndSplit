package com.whyyao.scanandsplit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.util.ArrayList;

import com.whyyao.scanandsplit.models.Item;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_CAMERA = 0;
    private final int PERMISSIONS_REQUEST_STORAGE = 2;
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageButton mCam;
    private TextView mText;
    private ImageView mImage;
    private Uri imageUri;
    private File imageFile;
    private TextBlockParser parser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindView();
        init();
    }

    private void bindView(){
        mCam = (ImageButton) findViewById(R.id.cam_button);
        mText = (TextView) findViewById(R.id.textview);
        mImage = (ImageView) findViewById(R.id.imageview);
    }

    private void init(){
        parser = new TextBlockParser();
        mCam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Bitmap test = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.hbc_min);
                ArrayList<Item> itemList = new ArrayList<>(parser.parse(scanText(test)));
                Intent intent = new Intent(MainActivity.this, InteractiveReceiptActivity.class);
                intent.putExtra("Items", itemList);
                startActivity(intent);
                // todo: Uncomment this once we want to use the camera
                // lunchCam();
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
            imageFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "receipt_" + System.currentTimeMillis() + ".jpg");
            imageUri = FileProvider.getUriForFile(this, "com.whyyao.scanandsplit.fileprovider", imageFile);
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(takeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                Uri currentUri = FileProvider.getUriForFile(this, "com.whyyao.scanandsplit.fileprovider", imageFile);
                Bitmap thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                Bitmap test = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.hbc_min);
                // mImage.setImageBitmap(thumbnail);
                ArrayList<Item> itemList = new ArrayList<>(parser.parse(scanText(test)));
                Intent intent = new Intent(MainActivity.this, InteractiveReceiptActivity.class);
                intent.putExtra("Items", itemList);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private SparseArray<TextBlock> scanText(Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        SparseArray<TextBlock> items = null;
        if (!textRecognizer.isOperational()) {
            Log.e("ERROR", "Dependency not available");
        }else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            items = textRecognizer.detect(frame);
//            StringBuilder stringBuilder = new StringBuilder();
//            for (int i = 0; i < items.size(); ++i) {
//                TextBlock item = items.valueAt(i);
//                stringBuilder.append(item.getValue());
//                stringBuilder.append("\n");
//            }
            // mText.setText(stringBuilder);
        }
        return items;
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


