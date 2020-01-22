package com.example.invinityapp.ExportAPurchaseBill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
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
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


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
                Intent Goadd = new Intent(CreateNewBillActivity.this, ClientsList.class);
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

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String date = simpleDateFormat.format(new Date());

            ContentValues values = new ContentValues();

            values.put("ClientID_FK",id);
            values.put("Client_Name",client.getText().toString());
            values.put("CreateDate",date);
            if(db.InsertNewBill(values)){
                Toast.makeText(this, "تمت الاضافة", Toast.LENGTH_SHORT).show();
                
            }else
                Toast.makeText(this, "حدث خطأ", Toast.LENGTH_SHORT).show();

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
