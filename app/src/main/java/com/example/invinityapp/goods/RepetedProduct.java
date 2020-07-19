package com.example.invinityapp.goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;
import java.util.Objects;

public class RepetedProduct extends AppCompatActivity {

    private InfinityDB db;
    private ListView swiplist;
    private String SuppllierID;
    private String Barcode;
    ArrayList<ProductsModel> productsModels;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeted_product);

        db = new InfinityDB(this);

        swiplist = findViewById(R.id.list);

        Intent intent = getIntent();
        SuppllierID = intent.getStringExtra("SupplierID");
        Barcode = intent.getStringExtra("barcode");
        GoodsList();


    }

    private void GoodsList() {

        Cursor result;

        result = db.getListOfGoods(SuppllierID, Barcode);
        productsModels = new ArrayList<>();

        final GoodsListAdaptare productAdabter = new GoodsListAdaptare(this, productsModels);

        while (result.moveToNext()) {

            Cursor res1 = db.LastGoodUnit(result.getString(4), result.getString(5));
            if (res1.getCount() > 0) {
                res1.moveToFirst();
            }
            productAdabter.add(new ProductsModel(result.getString(0), result.getString(3), result.getString(1), result.getString(2), res1.getString(0), result.getString(6)));
        }


        swiplist.setAdapter(productAdabter);

        swiplist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(RepetedProduct.this, UpdateProductActivity.class);
                intent.putExtra("SupplierID",SuppllierID);
                intent.putExtra("ProductID", Objects.requireNonNull(productAdabter.getItem(position)).id);
                intent.putExtra("ACTIVITY","Receive");
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.back) {
            bacToMain(null);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void bacToMain(View view){

        Intent intent = new Intent(this, receiving_goods_main.class);
        startActivity(intent);
        finish();
    }


}
