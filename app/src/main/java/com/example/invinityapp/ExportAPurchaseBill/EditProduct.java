package com.example.invinityapp.ExportAPurchaseBill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.invinityapp.R;

public class EditProduct extends AppCompatActivity {

    String PurchaseID,ProductID,Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Intent intent = getIntent();
        PurchaseID = intent.getStringExtra("ID");
        ProductID = intent.getStringExtra("ProductID");
        Activity = intent.getStringExtra("ACTIVITY");
    }
}
