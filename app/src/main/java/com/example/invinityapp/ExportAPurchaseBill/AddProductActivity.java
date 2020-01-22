package com.example.invinityapp.ExportAPurchaseBill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.goods.ProductsModel;
import com.example.invinityapp.goods.receiving_goods_main;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class AddProductActivity extends AppCompatActivity {

    EditText quantity,
            packageing,
            Barcode;
    TextView date,mines,Productname;
    Spinner units;
    LinearLayout container;
    AlertDialog dialog;
    String UnitID, UnitName;
    String ClientID,ClientName;
    String productID;
    InfinityDB db = new InfinityDB(this);
    SwipeMenuListView swiplist;
    LinearLayout linearLayout;
    String baseunit;
    private Dialog datePiker;
    private ArrayList<ProductsModel> productsModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        container = findViewById(R.id.container);
        swiplist = findViewById(R.id.GoodsList);
        Barcode = findViewById(R.id.barcode);
        Productname = findViewById(R.id.ProductName);
        quantity = findViewById(R.id.quantity);
        packageing = findViewById(R.id.packageing);
        date = findViewById(R.id.date);
        units = findViewById(R.id.UnitsSpinner);
        linearLayout = findViewById(R.id.list);
        mines = findViewById(R.id.mines);

        Intent intent = getIntent();

        ClientID = intent.getStringExtra("ID");
        ClientName = intent.getStringExtra("NAME");
    }




    ///// for quantity on click

    public void plus(View view){

        if(quantity.getText().toString().isEmpty()){
            quantity.setText("1");

        }else{
            mines.setVisibility(View.VISIBLE);
            int x = Integer.parseInt(quantity.getText().toString());
            x = x+1;
            quantity.setText(Integer.toString(x));

        }
    }

    @SuppressLint("SetTextI18n")
    public void mines(View view){
        if(quantity.getText().toString().isEmpty()){
            mines.setVisibility(View.INVISIBLE);
            quantity.setText("");

        }if(Integer.parseInt(quantity.getText().toString()) == 1){
            mines.setVisibility(View.INVISIBLE);
            quantity.setText("");

        }else{

            int x = Integer.parseInt(quantity.getText().toString());
            x = x -1;
            quantity.setText(Integer.toString(x));

        }

        if(Integer.parseInt(quantity.getText().toString()) == 1)
            mines.setVisibility(View.INVISIBLE);


    }
    ///////////////



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem cam = menu.findItem(R.id.openCam);
        MenuItem qr = menu.findItem(R.id.QR);
        cam.setVisible(true);
        qr.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                bacToMain(null);

                return true;

            case R.id.openCam:
                Barcode.setText("");
                Barcode.setEnabled(true);
                quantity.setText("");
                packageing.setText("");
                Productname.setText("");
                container.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                OPenCAM();
                return true;

            case R.id.QR:

                Barcode.setText("");
                Barcode.setEnabled(true);
                Productname.setText("");
                quantity.setText("");
                packageing.setText("");
                container.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bacToMain(View view){

        Intent intent = new Intent(this, receiving_goods_main.class);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (Result != null) {

            if (Result.getContents() == null) {

                Toast.makeText(this, "لم يتم إلتقاط باركود", Toast.LENGTH_SHORT).show();

            } else {


                Barcode.setText(Result.getContents());
               // GetByBarcode(Result.getContents());


            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void OPenCAM(){

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("ألتقط الباركود");
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }



}
