package com.example.invinityapp.registration_of_deficiencies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

public class UpdatePurchaseProduct extends AppCompatActivity {


    private static String id_data, productID, idUOMS, UOMNAME, bar;
    private EditText quntity, dis;
    private TextView barcode, name, beforeUpdate, SrockOnHand;
    private Spinner spinner;
    private int Activity;
    private InfinityDB db;
    private boolean hasExtra = true;
    private int qunt_before_update;
    private Dialog dialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_purchase_product);

        db = new InfinityDB(this);

        Intent intent = getIntent();

        Activity = Integer.parseInt(intent.getStringExtra("activity"));

        id_data = intent.getStringExtra("id");

        barcode =  findViewById(R.id.barcode);
        quntity =  findViewById(R.id.quntity);
        dis =  findViewById(R.id.discription);
        beforeUpdate =  findViewById(R.id.beforeUpdate);
        name    = findViewById(R.id.name);
        spinner = findViewById(R.id.spinner);

        barcode.setText(intent.getStringExtra("barcode"));
        bar = barcode.getText().toString();

        dis.setText(intent.getStringExtra("discription"));

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

            contacts.add(new Contact(res.getString(0), res.getString(1)));

        }

        ArrayAdapter<Contact> adapter =
                new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        Cursor uomResult = db.getPurchaseProductById(id_data);

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

        MenuItem stock = menu.findItem(R.id.getStock);
        stock.setVisible(true);


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                backToScan(null);

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



            default:
                return super.onOptionsItemSelected(item);
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


    public void backToScan(View view){
        Intent intent;

        if(Activity == 1){

            intent = new Intent(this, RegistrationOfDeficiencies.class);
            startActivity(intent);
            finish();
        }else if(Activity == 2) {

            intent = new Intent(this, ScanPurchaseProduct.class);
            startActivity(intent);
            finish();

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

            Contact contact = (Contact) spinner.getSelectedItem();

            UOMNAME = contact.contact_name;
            idUOMS = contact.contact_id;

            values.put("purchaseID", id_data);
            values.put("UOMName", UOMNAME);
            values.put("UOMID_PK", idUOMS);
            values.put("quantity", Integer.toString(qunt));
            values.put("discription", dis.getText().toString());
            values.put("dateTime", thisTime);

            int res = db.updatePurchaseData(values);

            if(res > 0){

                Toast.makeText(this, "تمت عملية الحفظ", Toast.LENGTH_LONG).show();

                Intent intent;

                if(Activity == 1){

                    intent = new Intent(this, RegistrationOfDeficiencies.class);
                    startActivity(intent);
                    finish();
                }else if(Activity == 2){

                    intent = new Intent(this, ScanPurchaseProduct.class);
                    startActivity(intent);
                    finish();

                }


            }else{

                Toast.makeText(this, "حدث خطأ ما الرجاء اعادة المحاولة", Toast.LENGTH_LONG).show();

            }
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


    private class getStock extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {



            InfinityDB db = new InfinityDB(UpdatePurchaseProduct.this);
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
