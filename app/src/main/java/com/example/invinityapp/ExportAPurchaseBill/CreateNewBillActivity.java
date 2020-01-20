package com.example.invinityapp.ExportAPurchaseBill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.invinityapp.R;
import com.example.invinityapp.goods.SuppliersList;
import com.example.invinityapp.goods.receiving_goods_main;
import com.example.invinityapp.inventoryitems.InfinityDB;


public class CreateNewBillActivity extends AppCompatActivity {

    EditText client;
    TextView AddClient;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_bill);

        client = findViewById(R.id.client);
        AddClient = findViewById(R.id.AddClient);

        listeners();
    }






    // a method for any listener event

    private void listeners(){

        AddClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Goadd = new Intent(CreateNewBillActivity.this, SuppliersList.class);
                startActivity(Goadd);
                finish();
            }
        });


        // if the client name is changed or deleted

        client.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().isEmpty())
                    id = -1;

            }
        });

    } //  listeners() method end here




    public void AddNew(View view) {

        InfinityDB db = new InfinityDB(this);


        if (client.getText().toString().isEmpty()) { // f-1
            Toast.makeText(this, "الرجاء ادخال إسم الزبون", Toast.LENGTH_SHORT).show();
        } else {


        }
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

                bacToMain(null);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bacToMain(View view){

        Intent intent = new Intent(this, PurchaseBillActivity.class);
        startActivity(intent);
        finish();
    }
}
