package com.example.invinityapp.Transport_of_goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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

public class UpdateProductWihtoutDate extends AppCompatActivity {


    private static String id_data, productID, idUOMS, UOMNAME, BaseUnit;
    private EditText quntity;
    private TextView barcode, name, beforeUpdate;
    private Spinner spinner;
    private InfinityDB db;
    private boolean hasExtra = true;
    private  int qunt_before_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_wihtout_date);

        db = new InfinityDB(this);

        Intent intent = getIntent();

        id_data = intent.getStringExtra("id");

        barcode =  findViewById(R.id.barcode);
        quntity =  findViewById(R.id.quntity);
        beforeUpdate =  findViewById(R.id.beforeUpdate);
        name    = findViewById(R.id.name);
        spinner = findViewById(R.id.spinner);

        barcode.setText(intent.getStringExtra("barcode"));
        qunt_before_update = Integer.parseInt(intent.getStringExtra("qun"));

        if(!intent.hasExtra("updateQuntity")){

            quntity.setText(intent.getStringExtra("qun"));
            hasExtra = false;
        }



        beforeUpdate.setText(intent.getStringExtra("qun"));

        name.setText(intent.getStringExtra("ProductName"));
        productID = intent.getStringExtra("Product_ID_PK");

        ArrayList<Contact> contacts = new ArrayList<>();

        Cursor res = db.getDataUOM(productID);

        while (res.moveToNext()){

            contacts.add(new Contact(res.getString(0), res.getString(1), res.getString(2)));

        }

        ArrayAdapter<Contact> adapter =
                new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);


        Cursor uomResult = db.getDocumentProductByID(id_data, Integer.toString(ScanProduct.DocumentID_PK));

        uomResult.moveToFirst();

        for(int i = 0; i < adapter.getCount() ;i++){

            if(uomResult.getString(6).compareTo(adapter.getItem(i).contact_id) == 0){

                spinner.setSelection(i);
                break;
            }
        }

        uomResult.close();


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

                Intent intent = new Intent(UpdateProductWihtoutDate.this, ScanProduct.class);
                startActivity(intent);
                UpdateProductWihtoutDate.this.finish();


                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void plus(View view){

        if(quntity.getText().toString().isEmpty()){

            quntity.setText("1");
        }else{

            int x = Integer.parseInt(quntity.getText().toString());
            x = x+1;
            quntity.setText(Integer.toString(x));

        }
    }

    public void mines(View view){

        if(quntity.getText().toString().isEmpty()){

            quntity.setText("0");

        }if(Integer.parseInt(quntity.getText().toString()) == 0){

            quntity.setText("0");

        }else{

            int x = Integer.parseInt(quntity.getText().toString());
            x = x -1;
            quntity.setText(Integer.toString(x));

        }
    }


    public void update(View view){

        if(quntity.getText().toString().isEmpty()){

            Toast.makeText(this, "لايمكن ترك حقل الكمية فارغ", Toast.LENGTH_LONG).show();


        }else if(quntity.getText().toString().compareTo("0") == 0) {

            Toast.makeText(this, "اقل قيمة للكمية 1", Toast.LENGTH_LONG).show();


        }else {

            ContentValues values = new ContentValues();
            Calendar c = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");


            String thisTime = df.format(c.getTime());

            int qunt = Integer.parseInt(quntity.getText().toString());


            if(hasExtra){

                qunt += qunt_before_update;
            }

            Contact contact = (Contact) spinner.getSelectedItem();
            UOMNAME = contact.contact_name;
            idUOMS = contact.contact_id;
            BaseUnit = contact.BaseUnit;



            values.put("DocProductID", id_data);
            values.put("UOMName", UOMNAME);
            values.put("UOMID_PK", idUOMS);
            values.put("BaseUnitQ", BaseUnit);
            values.put("quantity", Integer.toString(qunt));
            values.put("endDate", "");
            values.put("dateTime", thisTime);

            int res = db.updateDocumentProduct(values, Integer.toString(ScanProduct.DocumentID_PK));

            if(res > 0){

                Toast.makeText(this, "تمت عملية الحفظ", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(this, ScanProduct.class);
                startActivity(intent);
                UpdateProductWihtoutDate.this.finish();



            }else{

                Toast.makeText(this, "حدث خطأ ما الرجاء اعادة المحاولة", Toast.LENGTH_LONG).show();

            }
        }

    }



    private class Contact {
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

}
