package com.example.invinityapp.goods;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;
import java.util.Calendar;

public class UpdateProductActivity extends AppCompatActivity {

    EditText quantity,
            packageing,
            Productname,
            Barcode;
    TextView date,mines;
    Spinner units;
    String UnitID, UnitName;
    private DatePickerDialog.OnDateSetListener mydate;
    String SuppllierID,ProductID,SupplierName;
    String baseunit;
    String AnyActivity;
    InfinityDB db = new InfinityDB(this);
    LinearLayout Exdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);


        Barcode = findViewById(R.id.barcode);
        Productname = findViewById(R.id.ProductName);
        quantity = findViewById(R.id.quantity);
        packageing = findViewById(R.id.packageing);
        date = findViewById(R.id.date);
        units = findViewById(R.id.UnitsSpinner);
        Exdate = findViewById(R.id.Exdate);
        mines = findViewById(R.id.mines);

        Intent intent = getIntent();
        SuppllierID = intent.getStringExtra("SupplierID");
        ProductID = intent.getStringExtra("ProductID");
        AnyActivity = intent.getStringExtra("ACTIVITY");
        if(intent.hasExtra("NAME"))
            SupplierName = intent.getStringExtra("NAME");

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int yer = cal.get(Calendar.YEAR);
                int mon = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UpdateProductActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mydate,
                        yer,mon,day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        mydate = new DatePickerDialog.OnDateSetListener(){


            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String DataDate = dayOfMonth + "/" + month + "/" + year;
                date.setText(DataDate);



            }
        }; /// date end here


        GetdateToUpdate();

    }



    private void GetdateToUpdate(){
        String ID;
        Cursor res;
        ArrayList<Contact> contacts = new ArrayList<>();
        res = db.getDateToUpdate(ProductID);
        if(res.getCount() > 0){
            res.moveToFirst();
            Barcode.setText(res.getString(2));
            Productname.setText(res.getString(0));
            quantity.setText(res.getString(1));
            packageing.setText(res.getString(6));
            ID = res.getString(5);
            if(Integer.parseInt(ID) > 0){

                  if(!db.IfSupDate(res.getString(5)))
                      Exdate.setVisibility(View.GONE);
                  else
                      date.setText(res.getString(4));



            }else
                date.setText(res.getString(4));

            if(res.getString(3).compareTo("1") == 0){

                res =db.GetAllUnits();
                if(res.getCount() > 0)
                    while (res.moveToNext())
                        contacts.add(new Contact(res.getString(0), res.getString(1),"new"));

            }else{

                res = db.GetBarUnits(Integer.parseInt(ID));
                if(res.getCount() > 0)
                    while (res.moveToNext())
                        contacts.add(new Contact(res.getString(0), res.getString(1),res.getString(2)));

            }

        }


        ArrayAdapter<Contact> adapter;
        adapter = new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        units.setAdapter(adapter);


        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) parent.getSelectedItem();
                UnitName = contact.contact_name;
                UnitID = contact.contact_id;
                baseunit =contact.BaseUnit;
                if(baseunit.compareTo("new") != 0){
                    packageing.setText(baseunit);
                    packageing.setEnabled(false);
                }else
                    packageing.setEnabled(true);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Contact contact = (Contact) parent.getSelectedItem();
                UnitName = contact.contact_name;
                UnitID = contact.contact_id;
                baseunit =contact.BaseUnit;
                if(baseunit.compareTo("new") != 0){
                    packageing.setText(baseunit);
                    packageing.setEnabled(false);
                }else
                    packageing.setEnabled(true);
            }
        });


        if(Integer.parseInt(quantity.getText().toString()) > 1)
            mines.setVisibility(View.VISIBLE);
    }







    public void UpdateProduct(View view){

        if(Productname.getText().toString().isEmpty())
            Toast.makeText(this,"الرجاء تعبة اسم الصنف",Toast.LENGTH_SHORT).show();
        else if(quantity.getText().toString().isEmpty())
            Toast.makeText(this,"الرجاء تعبة الكمية",Toast.LENGTH_SHORT).show();
        else if(packageing.getText().toString().isEmpty())
            Toast.makeText(this,"الرجاء تعبة العبوة",Toast.LENGTH_SHORT).show();

        else{

            ContentValues values = new ContentValues();

            values.put("ProductUOMID_FK",Integer.parseInt(UnitID));
            values.put("Product_Name",Productname.getText().toString());
            values.put("Quantity",quantity.getText().toString());
            values.put("BaseUnitQ",packageing.getText().toString());
            values.put("ProductBarcode",Barcode.getText().toString());
            if(!date.getText().toString().isEmpty())
                values.put("EndDate",date.getText().toString());


            if(db.updateAddedProduct(values,ProductID)) {
                Toast.makeText(this,"تم التعديل",Toast.LENGTH_SHORT).show();
                if(AnyActivity.compareTo("View") == 0) {
                    Intent intent = new Intent(this, ViewGoodsActivity.class);
                    intent.putExtra("NAME",SupplierName);
                    intent.putExtra("ID",SuppllierID);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(this, ReceiveGoods.class);
                    startActivity(intent);
                    finish();
                }
            }else
                Toast.makeText(this,"حدث خطأ الرجاء إعادة المحاولة",Toast.LENGTH_SHORT).show();

        }
    }












    public class Contact {
        private String contact_name;
        private String contact_id;
        private String BaseUnit = "new";

        public Contact() {
        }

        public Contact(String contact_id,String contact_name,String BaseUnit) {
            this.contact_id = contact_id;
            this.contact_name = contact_name;
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

        if(AnyActivity.compareTo("View") == 0) {
            Intent intent = new Intent(this, ViewGoodsActivity.class);
            intent.putExtra("NAME",SupplierName);
            intent.putExtra("ID",SuppllierID);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, ReceiveGoods.class);
            intent.putExtra("NAME",SupplierName);
            intent.putExtra("ID",SuppllierID);
            startActivity(intent);
            finish();
        }
    }
}
