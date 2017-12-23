package com.whyyao.scanandsplit.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;
import com.whyyao.scanandsplit.R;
import com.whyyao.scanandsplit.core.BoxPickerActivity;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class StartingActivity extends AppCompatActivity implements View.OnClickListener {

    private final int PERMISSIONS_REQUEST_CAMERA = 0;
    private final int PERMISSIONS_REQUEST_STORAGE = 1;
    private final int GALLERY_PERMS = 2;
    private final int ACTIVITY_SELECT_IMAGE = 3;
    private final int ACTIVITY_TAKE_IMAGE = 4;
    private final int PREFERENCE = ScanConstants.OPEN_CAMERA;
    private final String TAG = "Starting Activity";

    private FloatingActionButton mCam;
    private FloatingActionButton mGal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        bindView();
        init();
    }

    private void bindView(){
        mCam = (FloatingActionButton) findViewById(R.id.fab_follow);
        mGal = (FloatingActionButton) findViewById(R.id.fab);
    }

    private void init(){
        mCam.setOnClickListener(this);
        mGal.setOnClickListener(this);
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
            intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, PREFERENCE);
            startActivityForResult(intent, ACTIVITY_TAKE_IMAGE);
        }
    }

    public void launchGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                switch (requestCode) {
                    case ACTIVITY_TAKE_IMAGE:
                        String filePath = data.getExtras().getString("FilePath");
                        startBoxPickerActivity(filePath, null);
                        break;
                    case ACTIVITY_SELECT_IMAGE:
                        Uri uri = data.getData();
                        startBoxPickerActivity(null, uri);
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error retrieving photo from camera or gallery");
            }
        }
    }

    /*
        filepath -> photo taken with camera
        Uri  -> scanned taken from gallery

     */
    private void startBoxPickerActivity(String filepath, Uri uri) {
        Intent intent = new Intent(StartingActivity.this, BoxPickerActivity.class);
        if (filepath != null) {
            intent.putExtra("FilePath", filepath);
            startActivity(intent);
        } else if (uri != null) {
            intent.putExtra("GalUri", uri.toString());
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCam();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.activity_start),
                            "Camera permission is required to scan your receipt", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("ENABLE", new enableCamListener());
                    mySnackbar.show();
                }
                return;
            case  PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCam();
                } else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.activity_start),
                            "Storage permission is required to scan your receipt", Snackbar.LENGTH_LONG);
                    mySnackbar.setAction("ENABLE", new enableStorageListener());
                    mySnackbar.show();
                }
                return;
        }
    }

    public void onClick(View view){
        int viewId = view.getId();
        switch(viewId) {
            case R.id.fab_follow:
                Uri uri = Uri.parse("android.resource://com.whyyao.scanandsplit/drawable/hbc_min");
                //startBoxPickerActivity(uri);
                launchCam();
                break;
            case R.id.fab:
                if (ContextCompat.checkSelfPermission(StartingActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    launchGallery();
                } else {
                    ActivityCompat.requestPermissions(StartingActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMS);
                }
                break;
        }
    }

    public class enableCamListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(StartingActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        }
    }

    public class enableStorageListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            ActivityCompat.requestPermissions(StartingActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_STORAGE);
        }
    }


}


