package com.example.invinityapp.viewcategory;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;


public class ProductPaymentDetails extends AppCompatActivity {


    LinearLayout viewDetails1,
            viewDetails2,
            viewDetails3,
            viewDetails4,
            viewDetails5;
    TextView name,supplier,unit,qun,price,date,tilte;

    String ProductName,ProductID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_payment_details);

        // liner layout
        viewDetails1 = findViewById(R.id.viewDetails1);
        viewDetails2 = findViewById(R.id.viewDetails2);
        viewDetails3 = findViewById(R.id.viewDetails3);
        viewDetails4 = findViewById(R.id.viewDetails4);
        viewDetails5 = findViewById(R.id.viewDetails5);

        //text view
        tilte = findViewById(R.id.title);
        name = findViewById(R.id.name);



        Listeners();

        Intent intent = getIntent();
        ProductName = intent.getStringExtra("ProductName");
        ProductID = intent.getStringExtra("ProductID");

        name.setText(ProductName);

        InfinityDB db = new InfinityDB(this);
        Cursor res =  db.SelectProductPurchase(ProductID);
        tilte.setText(" اخر "+res.getCount()+" عملية/عمليات شراء ");
        int callNum = 1;
        ArrayList<String> list = new ArrayList<>();

        while (res.moveToNext()){

            list.clear();
            list.add(res.getString(1));
            list.add(res.getString(2));
            list.add(res.getString(3));
            list.add(res.getString(4));
            list.add(res.getString(5));

            switch (callNum){

                case 1 :    price1(list);
                            viewDetails1.setVisibility(View.VISIBLE);
                            break;

                case 2 :    price2(list);
                            viewDetails2.setVisibility(View.VISIBLE);
                            break;

                case 3 :    price3(list);
                            viewDetails3.setVisibility(View.VISIBLE);
                            break;

                case 4 :    price4(list);
                            viewDetails4.setVisibility(View.VISIBLE);
                            break;

                case 5 :    price5(list);
                            viewDetails5.setVisibility(View.VISIBLE);
                            break;
            }
            list.clear();
            callNum++;
        }

    }



    private void Listeners(){

        viewDetails1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricedetails1FN();
            }
        });

        viewDetails2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricedetails2FN();
            }
        });

        viewDetails3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricedetails3FN();
            }
        });

        viewDetails4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricedetails4FN();
            }
        });

        viewDetails5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricedetails5FN();
            }
        });

    }






    private void pricedetails1FN(){

        LinearLayout pricedetails;
        TextView arrow = findViewById(R.id.arrow1);
        pricedetails = findViewById(R.id.pricedetails1);

        if (pricedetails.getVisibility() == View.VISIBLE) {
            pricedetails.setVisibility(View.GONE);
            arrow.animate().rotationX(0);

        } else {
            pricedetails.setVisibility(View.VISIBLE);
            arrow.animate().rotationX(180);

        }

    }


    private void price1(ArrayList<String> list){
        supplier = findViewById(R.id.supplier);
        unit = findViewById(R.id.untit);
        qun = findViewById(R.id.qun);
        price = findViewById(R.id.price1);
        date = findViewById(R.id.date);

        supplier.setText(list.get(0));
        unit.setText(list.get(1));
        qun.setText(list.get(2));
        price.setText(list.get(3));
        date.setText(list.get(4));

    }

    private void pricedetails2FN(){

        TextView arrow = findViewById(R.id.arrow2);
        LinearLayout pricedetails;
        pricedetails = findViewById(R.id.pricedetails2);

        if (pricedetails.getVisibility() == View.VISIBLE) {
            pricedetails.setVisibility(View.GONE);
            arrow.animate().rotationX(0);

        } else {
            pricedetails.setVisibility(View.VISIBLE);
            arrow.animate().rotationX(180);

        }
    }


    private void price2(ArrayList<String> list){
        supplier = findViewById(R.id.supplier2);
        unit = findViewById(R.id.untit2);
        qun = findViewById(R.id.qun2);
        price = findViewById(R.id.price2);
        date = findViewById(R.id.date2);

        supplier.setText(list.get(0));
        unit.setText(list.get(1));
        qun.setText(list.get(2));
        price.setText(list.get(3));
        date.setText(list.get(4));

    }

    private void pricedetails3FN(){

        TextView arrow = findViewById(R.id.arrow3);
        LinearLayout pricedetails;
        pricedetails = findViewById(R.id.pricedetails3);

        if (pricedetails.getVisibility() == View.VISIBLE) {
            pricedetails.setVisibility(View.GONE);
            arrow.animate().rotationX(0);

        } else {
            pricedetails.setVisibility(View.VISIBLE);
            arrow.animate().rotationX(180);

        }
    }


    private void price3(ArrayList<String> list){
        supplier = findViewById(R.id.supplier3);
        unit = findViewById(R.id.untit3);
        qun = findViewById(R.id.qun3);
        price = findViewById(R.id.price3);
        date = findViewById(R.id.date3);

        supplier.setText(list.get(0));
        unit.setText(list.get(1));
        qun.setText(list.get(2));
        price.setText(list.get(3));
        date.setText(list.get(4));

    }

    private void pricedetails4FN(){

        TextView arrow = findViewById(R.id.arrow4);
        LinearLayout pricedetails;
        pricedetails = findViewById(R.id.pricedetails4);

        if (pricedetails.getVisibility() == View.VISIBLE) {
            pricedetails.setVisibility(View.GONE);
            arrow.animate().rotationX(0);

        } else {
            pricedetails.setVisibility(View.VISIBLE);
            arrow.animate().rotationX(180);

        }
    }


    private void price4(ArrayList<String> list){
        supplier = findViewById(R.id.supplier4);
        unit = findViewById(R.id.untit4);
        qun = findViewById(R.id.qun4);
        price = findViewById(R.id.price4);
        date = findViewById(R.id.date4);

        supplier.setText(list.get(0));
        unit.setText(list.get(1));
        qun.setText(list.get(2));
        price.setText(list.get(3));
        date.setText(list.get(4));

    }

    private void pricedetails5FN(){

        TextView arrow = findViewById(R.id.arrow5);
        LinearLayout pricedetails;
        pricedetails = findViewById(R.id.pricedetails5);

        if (pricedetails.getVisibility() == View.VISIBLE) {
            pricedetails.setVisibility(View.GONE);
            arrow.animate().rotationX(0);

        } else {
            pricedetails.setVisibility(View.VISIBLE);
            arrow.animate().rotationX(180);

        }
    }


    private void price5(ArrayList<String> list){
        supplier = findViewById(R.id.supplier5);
        unit = findViewById(R.id.untit5);
        qun = findViewById(R.id.qun5);
        price = findViewById(R.id.price5);
        date = findViewById(R.id.date5);

        supplier.setText(list.get(0));
        unit.setText(list.get(1));
        qun.setText(list.get(2));
        price.setText(list.get(3));
        date.setText(list.get(4));

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.inventorymenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){


            case R.id.back:

                Intent intent = new Intent(this, DataOrder.class);
                startActivity(intent);
                finish();

                return true;



            default: return super.onOptionsItemSelected(item);

        }
    }
}
