package com.whyyao.scanandsplit;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.style.TtsSpan;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

/**
 * Created by tristantstarck on 12/1/17.
 */

public class InteractiveReceiptActivity extends AppCompatActivity {
    private ListView list;
    private ArrayList<Item> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        items = intent.getParcelableArrayListExtra("Items");
        setContentView(R.layout.activity_list_view);
        TextView testing = (TextView) findViewById(R.id.text_view_test);
        String huge_String = "";
        for (int i = 0; i < items.size(); i++) {
            huge_String = huge_String + items.get(i).toString();
        }
        testing.setText(huge_String);

        /*
        for (int i = 0; i < 5; i++){
            Item temp = new Item("Item" + i, i + 5);
            items.add(temp.toString());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
        list.setAdapter(arrayAdapter); */
    }
}
