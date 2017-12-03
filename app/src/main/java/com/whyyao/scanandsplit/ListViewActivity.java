package com.whyyao.scanandsplit;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.whyyao.scanandsplit.models.Item;

import java.util.ArrayList;

/**
 * Created by tristantstarck on 12/1/17.
 */

public class ListViewActivity extends AppCompatActivity {
    ListView list;
    ArrayList<String> items = new ArrayList<String>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_list_view);
        list = (ListView) findViewById(R.id.list_view);

        for(int i = 0; i < 5; i++){
            Item temp = new Item("Item" + i, i + 5);
            items.add(temp.toString());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
        list.setAdapter(arrayAdapter);
    }
}
