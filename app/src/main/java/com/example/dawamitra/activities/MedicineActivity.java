package com.example.dawamitra.activities;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dawamitra.R;

import java.util.ArrayList;

public class MedicineActivity extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        listView = findViewById(R.id.listView);

        ArrayList<String> meds = getIntent().getStringArrayListExtra("medicines");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                meds
        );

        listView.setAdapter(adapter);
    }
}