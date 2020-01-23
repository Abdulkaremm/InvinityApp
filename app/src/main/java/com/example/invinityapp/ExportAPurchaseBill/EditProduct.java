package com.example.invinityapp.ExportAPurchaseBill;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.goods.ReceiveGoods;
import com.example.invinityapp.goods.UpdateProductActivity;
import com.example.invinityapp.goods.ViewGoodsActivity;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class EditProduct extends AppCompatActivity {

    String   PurchaseID,ProductID,Activity,
             UnitID, UnitName,baseunit,CleintName;

    EditText quantity,
            packageing,
            Barcode;
    TextView mines,Productname;
    Spinner units;
    LinearLayout container;
    SwipeMenuListView swiplist;
    InfinityDB db = new InfinityDB(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        container = findViewById(R.id.container);
        swiplist = findViewById(R.id.ProductsList);
        Barcode = findViewById(R.id.barcode);
        Productname = findViewById(R.id.ProductName);
        quantity = findViewById(R.id.quantity);
        packageing = findViewById(R.id.packageing);
        units = findViewById(R.id.UnitsSpinner);
        mines = findViewById(R.id.mines);

        Intent intent = getIntent();
        PurchaseID = intent.getStringExtra("ID");
        ProductID = intent.getStringExtra("ProductID");
        Activity = intent.getStringExtra("ACTIVITY");
        CleintName = intent.getStringExtra("NAME");

        GetdateToUpdate(ProductID);
    }





    private void GetdateToUpdate(String ID){

        Cursor res;
        ArrayList<Contact> contacts = new ArrayList<>();
        res = db.getDateToEdit(ID);
        if(res.getCount() > 0){
            res.moveToFirst();
            Barcode.setText(res.getString(2));
            Barcode.setEnabled(false);
            Productname.setText(res.getString(0));
            quantity.setText(res.getString(1));
            String UOMID = res.getString(3);
            res.close();
            res = db.GetProductUnits(Integer.parseInt(UOMID));
            if(res.getCount() > 0)
                while (res.moveToNext())
                    contacts.add(new Contact(res.getString(0), res.getString(1),res.getString(2)));

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
                packageing.setText(baseunit);
                packageing.setEnabled(false);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Contact contact = (Contact) parent.getSelectedItem();
                UnitName = contact.contact_name;
                UnitID = contact.contact_id;
                baseunit =contact.BaseUnit;
                packageing.setText(baseunit);
                packageing.setEnabled(false);

            }
        });


        if(Integer.parseInt(quantity.getText().toString()) > 1)
            mines.setVisibility(View.VISIBLE);
    }


    public void UpdateBillProduct(View v){

        if(quantity.getText().toString().isEmpty())
            Toast.makeText(this,"الرجاء اخال الكمية ",Toast.LENGTH_SHORT).show();
        else {

            ContentValues values = new ContentValues();
            values.put("Product_Name",Productname.getText().toString());
            values.put("Quantity",quantity.getText().toString());
            values.put("ProductBarcode",Barcode.getText().toString());
            values.put("ProductUOMID_FK",UnitID);
            values.put("BaseUnitQ",packageing.getText().toString());

            if(db.UpdateBillProduct(values,ProductID)) {

                Toast.makeText(this, "تم تعديل الصنف", Toast.LENGTH_SHORT).show();
                if (Activity.compareTo("View") == 0) {
                    Intent intent = new Intent(this, ViewBillProducts.class);
                    intent.putExtra("NAME", CleintName);
                    intent.putExtra("ID", PurchaseID);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, AddProductActivity.class);
                    intent.putExtra("NAME", CleintName);
                    intent.putExtra("ID", PurchaseID);
                    startActivity(intent);
                    finish();
                }

            }else
                Toast.makeText(this,"حدث خطأ ! لم يتم التعديل ",Toast.LENGTH_SHORT).show();
        }
    }








    ////// spinner structure


    public class Contact {
        private String contact_name;
        private String contact_id;
        private String BaseUnit;

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

        if(Activity.compareTo("View") == 0) {
            Intent intent = new Intent(this, ViewBillProducts.class);
            intent.putExtra("NAME",CleintName);
            intent.putExtra("ID",PurchaseID);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(this, AddProductActivity.class);
            intent.putExtra("NAME",CleintName);
            intent.putExtra("ID",PurchaseID);
            startActivity(intent);
            finish();
        }
    }
}
