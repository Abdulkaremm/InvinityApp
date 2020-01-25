package com.example.invinityapp.viewcategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.invinityapp.R;


public class ProductPaymentDetails extends AppCompatActivity {


    LinearLayout viewDetails1,
            viewDetails2,
            viewDetails3,
            viewDetails4,
            viewDetails5;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_payment_details);

        viewDetails1 = findViewById(R.id.viewDetails1);
        viewDetails2 = findViewById(R.id.viewDetails2);
        viewDetails3 = findViewById(R.id.viewDetails3);
        viewDetails4 = findViewById(R.id.viewDetails4);
        viewDetails5 = findViewById(R.id.viewDetails5);


        viewDetails1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pricedetails1FN();
            }
        });

        Listeners();


    }



    private void Listeners(){



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
