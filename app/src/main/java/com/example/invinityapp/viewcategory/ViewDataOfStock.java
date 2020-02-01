package com.example.invinityapp.viewcategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class ViewDataOfStock extends AppCompatActivity {


    private ProgressBar progressBar;
    private String ProductName;
    private ListView StockList;
    private ArrayList<StockModel> models;
    private InfinityDB db;
    private TextView errmsg;
    private StockAdapter stockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_of_stock);

        db = new InfinityDB(this);

        errmsg = findViewById(R.id.ErrorMsg);
        progressBar = findViewById(R.id.ProgressBar);
        StockList = findViewById(R.id.stockList);

        models = new ArrayList<>();

        stockAdapter = new StockAdapter(this, models);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                Intent intent = new Intent(ViewDataOfStock.this, DataOrder.class);
                startActivity(intent);
                ViewDataOfStock.this.finish();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
