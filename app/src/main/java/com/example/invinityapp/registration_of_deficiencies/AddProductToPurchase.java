package com.example.invinityapp.registration_of_deficiencies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddProductToPurchase extends AppCompatActivity {


    private TextView name, barcode, SrockOnHand;
    private EditText quntity, discription;
    private LinearLayout layout, ExpiredDate;
    private Spinner spinner;
    private String idUOMS, Product_ID, BaseUnit, UOMNAME, bar;
    private InfinityDB mydata;
    private TextView btn;

    private Dialog dialog;



    private ArrayList<Contact> contacts = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_to_purchase);

        btn = findViewById(R.id.mines);

        ExpiredDate = findViewById(R.id.expiredDate);

        mydata = new InfinityDB(this);

        //البيانات الخاصة بالواجهه
        barcode  =  findViewById(R.id.barcode);
        name     =  findViewById(R.id.name);
        quntity  =  findViewById(R.id.quntity);
        discription  =  findViewById(R.id.discription);
        spinner = findViewById(R.id.spinner);

        Intent GetData = getIntent();

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

        bar = barcode.getText().toString();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem stock = menu.findItem(R.id.getStock);
        stock.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.back:

                Intent intent = new Intent(AddProductToPurchase.this, ScanPurchaseProduct.class);
                startActivity(intent);
                AddProductToPurchase.this.finish();
                return true;

            case R.id.getStock:

                if(checkConnect()){

                    createDilog();

                    dialog.show();

                    new getStock().execute();


                }else{

                    Toast.makeText(this, "لايوجد اتصال بالشبكة الرجاء التحقق من الشبكة و اعادة المحاولة", Toast.LENGTH_LONG).show();
                }


                return true;



            default: return super.onOptionsItemSelected(item);
        }


    }

    private void createDilog(){

        dialog = new Dialog(this);

        dialog.setContentView(R.layout.stockonhand);

        SrockOnHand =  dialog.findViewById(R.id.stock);
    }


    private boolean checkConnect(){

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }




    public void addItem(View view){


        String bar = barcode.getText().toString(),
                thisTime,
                ip,
                username = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        thisTime = df.format(c.getTime());

        Cursor dv = mydata.ExbordSetting();
        dv.moveToFirst();
        ip = dv.getString(8);


        Contact contact = (Contact) spinner.getSelectedItem();

        UOMNAME = contact.contact_name;
        idUOMS = contact.contact_id;

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
                values.put("discription", discription.getText().toString());
                values.put("dateTime", thisTime);
                values.put("deviceIP", ip);
                values.put("userName", username);


                boolean ifInsert = mydata.InsertPurchaseProduct(values);



                if (ifInsert) {

                    Toast.makeText(getApplicationContext(), "تمت عملية الحفظ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddProductToPurchase.this, ScanPurchaseProduct.class);
                    startActivity(intent);
                    AddProductToPurchase.this.finish();
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



    private class getStock extends AsyncTask<String, Void, String>{


        @Override
        protected String doInBackground(String... strings) {



            InfinityDB db = new InfinityDB(AddProductToPurchase.this);
            Cursor result = db.ExbordSetting();
            result.moveToFirst();


            String url = "http://"+result.getString(7)+"/api/InfinityRetail?Barcode=" + bar;
            String res = "false";


            HttpURLConnection urlConnection;
            InputStream inputStream;
            BufferedReader bufferedReader;
            URL urlIfSup;

            try {

                    urlIfSup = new URL(url);
                    urlConnection = (HttpURLConnection) urlIfSup.openConnection();
                    inputStream = urlConnection.getInputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));

                    StringBuilder data = new StringBuilder(bufferedReader.readLine());

                    while(bufferedReader.readLine() != null){

                        data.append(bufferedReader.readLine());
                    }


                JSONObject ob = new JSONObject(data.toString());

                    return  ob.getString("StockOnHand");



            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }




            return res;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.compareTo("false") == 0){

                SrockOnHand.setText("فشلت عملية جلب المخزون !");

            }else{

                SrockOnHand.setText(s);
                SrockOnHand.setTextSize(30);
            }
        }
    }
}
