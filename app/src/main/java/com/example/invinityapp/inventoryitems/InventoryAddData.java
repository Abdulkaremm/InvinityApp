package com.example.invinityapp.inventoryitems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.invinityapp.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class InventoryAddData extends AppCompatActivity {

    public  TextView name, barcode;

    public static Activity activity;

    public EditText quntity;
    public LinearLayout layout, ExpiredDate;
    public Spinner spinner;
    public String idUOMS, Product_ID, BaseUnit, UOMNAME;
    public InfinityDB mydata;
    private TextView btn;

   private ArrayList<Contact> contacts = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_add_data);

        btn = findViewById(R.id.mines);

        activity = this;

        ExpiredDate = findViewById(R.id.expiredDate);

        mydata = new InfinityDB(this);

        //البيانات الخاصة بالواجهه
        barcode  =  findViewById(R.id.barcode);
        name     =  findViewById(R.id.name);
        quntity  =  findViewById(R.id.quntity);
        spinner = findViewById(R.id.spinner);

        Intent GetData = getIntent();

        Log.i("ID", "BARCODE = " + GetData.getStringExtra("Barcode"));

        Cursor res = mydata.getDataProduct(GetData.getStringExtra("Barcode"));

        while (res.moveToNext()) {


            barcode.setText(GetData.getStringExtra("Barcode"));
            name.setText(res.getString(1));
            Product_ID = res.getString(0);
            BaseUnit = res.getString(4);

            contacts.add(new Contact(res.getString(2), res.getString(3)));
        }


        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) parent.getSelectedItem();

                OnSelectUOM(contact);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Contact contact = (Contact) parent.getSelectedItem();

                OnSelectUOM(contact);

            }
        });



    }


    public void OnSelectUOM(Contact v){

        Contact contact = (Contact) spinner.getSelectedItem();
        idUOMS = contact.contact_id;
        UOMNAME = contact.contact_name;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.back:

                backToScan(null);
                return true;

            default: return super.onOptionsItemSelected(item);
        }


    }



    public void backToScan(View view){

        Intent intent = new Intent(InventoryAddData.this, QuickAdd.class);
        startActivity(intent);
        InventoryAddData.this.finish();
    }


    public void addItem(View view){


            String bar = barcode.getText().toString(),
                    thisTime,
                    ip,
                    username = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        thisTime = df.format(c.getTime());

        Cursor dv = inventroyscan.mydata.ExbordSetting();
        dv.moveToFirst();
        ip = dv.getString(8);


            if (quntity.getText().toString().isEmpty()) {

                Toast.makeText(getApplicationContext(), "لا يمكن ترك حقل الكمية فارغ", Toast.LENGTH_SHORT).show();

            } else {

                int qunt = Integer.parseInt(quntity.getText().toString());

                if (qunt == 0) {

                    Toast.makeText(getApplicationContext(), "اقل قيمة للكمية 1", Toast.LENGTH_SHORT).show();

                } else {


                    ContentValues values = new ContentValues();
                    values.put("Product_ID_PK", Product_ID);
                    values.put("ProductName", name.getText().toString());
                    values.put("UOMName", UOMNAME);
                    values.put("UOMID_PK", idUOMS);
                    values.put("BaseUnitQ", BaseUnit);
                    values.put("barcode", bar);
                    values.put("quantity", qunt);
                    values.put("endDate", "");
                    values.put("dateTime", thisTime);
                    values.put("deviceIP", ip);
                    values.put("userName", username);


                    boolean ifInsert = inventroyscan.mydata.insertData(values);



                    if (ifInsert) {

                        Toast.makeText(getApplicationContext(), "تمت عملية الحفظ", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(InventoryAddData.this, QuickAdd.class);
                        startActivity(intent);
                        InventoryAddData.this.finish();
                    } else {

                        Toast.makeText(getApplicationContext(), "فشلت عملية الحفظ", Toast.LENGTH_SHORT).show();

                    }
                }

            }

    }


    public static class Contact {
        private String contact_name;
        private String contact_id;

        public Contact() {
        }

        public Contact(String contact_name, String contact_id) {
            this.contact_name = contact_name;
            this.contact_id = contact_id;
        }

        public String getContact_name() {
            return contact_name;
        }

        public void setContact_name(String contact_name) {
            this.contact_name = contact_name;
        }

        public String getContact_id() {
            return contact_id;
        }

        public void setContact_id(String contact_id) {
            this.contact_id = contact_id;
        }

        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        @Override
        public String toString() {
            return contact_name;
        }
    }



    public void plus(View view){

        if(quntity.getText().toString().isEmpty()){

            quntity.setText("1");


        }else{

            btn.setVisibility(View.VISIBLE);
            int x = Integer.parseInt(quntity.getText().toString());
            x = x+1;
            quntity.setText(Integer.toString(x));
        }
    }

    public void mines(View view){

        if(quntity.getText().toString().isEmpty()){

            quntity.setText("1");

        }if(Integer.parseInt(quntity.getText().toString()) == 1){

            quntity.setText("1");
            view.setVisibility(View.INVISIBLE);

        }else{

            int x = Integer.parseInt(quntity.getText().toString());
            x = x -1;
            quntity.setText(Integer.toString(x));

            if(Integer.parseInt(quntity.getText().toString()) == 1){

                quntity.setText("1");
                view.setVisibility(View.INVISIBLE);
            }
        }
    }



}
