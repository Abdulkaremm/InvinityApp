package com.example.invinityapp.Transport_of_goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddProdutWihtoutDate extends AppCompatActivity {


    public TextView name, barcode;
    public EditText quntity;
    public Spinner spinner;
    public InfinityDB db;
    private TextView btn;
    public String idUOMS, Product_ID, BaseUnit, UOMNAME;
    private ArrayList<Contact> contacts = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_produt_wihtout_date);


        db = new InfinityDB(this);

        btn = findViewById(R.id.mines);

        //البيانات الخاصة بالواجهه
        barcode  =  findViewById(R.id.barcode);
        name     =  findViewById(R.id.name);
        quntity  =  findViewById(R.id.quntity);
        spinner = findViewById(R.id.spinner);

        Intent GetData = getIntent();

        Log.i("ID", "BARCODE = " + GetData.getStringExtra("Barcode"));

        Cursor res = db.getDataProduct(GetData.getStringExtra("Barcode"));

        while (res.moveToNext()) {


            barcode.setText(GetData.getStringExtra("Barcode"));
            name.setText(res.getString(1));
            Product_ID = res.getString(0);

            contacts.add(new Contact(res.getString(2), res.getString(3), res.getString(4)));
        }


        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }


    public void addItem(View v){

        String bar = barcode.getText().toString(),
                thisTime,
                ip,
                username = "";

        Contact contact = (Contact) spinner.getSelectedItem();

        UOMNAME = contact.contact_name;
        idUOMS = contact.contact_id;
        BaseUnit = contact.BaseUnit;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        thisTime = df.format(c.getTime());

        Cursor dv = db.ExbordSetting();
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
                values.put("DocumentID_FK", ScanProduct.DocumentID_PK);
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


                boolean ifInsert = db.inserProduToDocument(values);

                if (ifInsert) {

                    Toast.makeText(getApplicationContext(), "تمت عملية الحفظ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProdutWihtoutDate.this, ScanProduct.class);
                    startActivity(intent);
                    AddProdutWihtoutDate.this.finish();
                } else {

                    Toast.makeText(getApplicationContext(), "فشلت عملية الحفظ", Toast.LENGTH_SHORT).show();

                }
            }

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

                Intent intent = new Intent(this, ScanProduct.class);
                startActivity(intent);
                AddProdutWihtoutDate.this.finish();

                return true;




            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static class Contact {
        private String contact_name;
        private String contact_id;
        private String BaseUnit;

        public Contact() {
        }

        public Contact(String contact_name, String contact_id, String BaseUnit) {
            this.contact_name = contact_name;
            this.contact_id = contact_id;
            this.BaseUnit = BaseUnit;
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
