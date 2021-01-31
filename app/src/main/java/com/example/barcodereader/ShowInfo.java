package com.example.barcodereader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowInfo extends AppCompatActivity {

    ListView defaultListView;
    ArrayList<HashMap<String, String>> defaultList;
    DefaultDisplayAdaptater adaptater;
    TextView title;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_info);

        defaultListView = (ListView) findViewById(R.id.listURL);
        title = (TextView) findViewById(R.id.titleView);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        defaultList = (ArrayList<HashMap<String, String>>) this.getIntent().getExtras().get("list");
        adaptater = new DefaultDisplayAdaptater(ShowInfo.this, defaultList);
        title.setText( defaultList.size() + " Barcodes ");
        defaultListView.setAdapter(adaptater);
    }
}