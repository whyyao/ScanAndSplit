package com.whyyao.scanandsplit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mRead;
    private TextView mText;
    private TextBlockParser blockParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        blockParser = new TextBlockParser();
        bindView();
        init();
    }


    private void bindView(){
        mText = (TextView)findViewById(R.id.text_test);
        mRead = (Button) findViewById(R.id.button_read);
    }

    private void init(){
        final Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.hbc_min);
        mRead.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                  Log.e("ERROR", "Dependency not avaliable");
                } else{
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    /*
                        PASSING OF THE TEXTBLOCK OBJECTS BELLOW
                     */
                    ArrayList<Item> itemsList = blockParser.parse(items);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); ++i) {
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    mText.setText(stringBuilder);
                }
            }
        });
    }


}
