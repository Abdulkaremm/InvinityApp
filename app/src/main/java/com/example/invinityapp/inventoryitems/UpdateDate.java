package com.example.invinityapp.inventoryitems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.invinityapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class UpdateDate extends AppCompatActivity {

    public static String id_data, productID, idUOMS, UOMNAME;
    public EditText quntity;
    public TextView  barcode, name, beforeUpdate;
    public Spinner spinner;
    public InfinityDB mydp;
    public int Activity;
    private boolean hasExtra = true;
    private  int qunt_before_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_date);



        mydp = new InfinityDB(this);

        Intent intent = getIntent();

        Activity = Integer.parseInt(intent.getStringExtra("activity"));

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

        Cursor res = mydp.getDataUOM(productID);

        while (res.moveToNext()){

            contacts.add(new Contact(res.getString(0), res.getString(1)));

        }
        res.close();

        ArrayAdapter<Contact> adapter =
                new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        res = mydp.selectDataByID(id_data);
        res.moveToFirst();

        for(int i = 0; i < adapter.getCount() ;i++){

            if(res.getString(4).compareTo(adapter.getItem(i).contact_id) == 0){

                spinner.setSelection(i);
                break;
            }
        }

        res.close();




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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem item1 = menu.findItem(R.id.sync);
        MenuItem item2 = menu.findItem(R.id.openCam);

        item1.setVisible(false);
        item2.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                backToScan(null);

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void backToScan(View view){
        Intent intent;

        if(Activity == 1){

            intent = new Intent(this, inventroyscan.class);
            startActivity(intent);
            finish();
        }else if(Activity == 2){

            intent = new Intent(this, QuickAdd.class);
            startActivity(intent);
            finish();

        }else {

            intent = new Intent(this, DataRepeted.class);
            if(hasExtra){

                intent.putExtra("updateQuntity", 0);
            }
            startActivity(intent);
            finish();
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
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");


            String thisTime = df.format(c.getTime());

            int qunt = Integer.parseInt(quntity.getText().toString());

            if(hasExtra){

                qunt += qunt_before_update;
            }


            values.put("cat_id", id_data);
            values.put("UOMName", UOMNAME);
            values.put("UOMID_PK", idUOMS);
            values.put("quantity", Integer.toString(qunt));
            values.put("endDate", "");
            values.put("dateTime", thisTime);

            int res = mydp.updateData(values);

            if(res > 0){

                Toast.makeText(this, "تمت عملية الحفظ", Toast.LENGTH_LONG).show();

                Intent intent;

                if(Activity == 1){

                    intent = new Intent(this, inventroyscan.class);
                    startActivity(intent);
                    finish();
                }else if(Activity == 2){

                    intent = new Intent(this, QuickAdd.class);
                    startActivity(intent);
                    finish();

                }else {

                    intent = new Intent(this, DataRepeted.class);
                    if(hasExtra){

                        intent.putExtra("updateQuntity", 0);
                    }
                    startActivity(intent);
                    finish();
                }


            }else{

                Toast.makeText(this, "حدث خطأ ما الرجاء اعادة المحاولة", Toast.LENGTH_LONG).show();

            }
        }

    }

    public void OnSelectUOM(Contact v){

        UpdateDate.Contact contact = (UpdateDate.Contact) spinner.getSelectedItem();
        idUOMS = contact.contact_id;
        UOMNAME = contact.contact_name;
    }

    private class Contact {
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


}

