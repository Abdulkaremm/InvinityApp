package com.example.invinityapp.inventoryitems;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.invinityapp.R;

public class ViewInfoProducts extends AppCompatActivity {

    private TextView barcode,
    name,
    qun,
    baseunit,
    date,
    inNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info_products);

        barcode = findViewById(R.id.barcode);
    }
}
