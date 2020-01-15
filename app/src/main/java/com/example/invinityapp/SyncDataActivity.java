package com.example.invinityapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.example.invinityapp.inventoryitems.inventroyscan;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;


public class SyncDataActivity extends AppCompatActivity {


    Switch  setting,
            prudects,
            units,
            suppliers,
            invetory,
            goods,
            Shortfalls;

    TextView    settingDate,
                productsDate,
                unitsDate,
                supDate,
                invetoryDate,
                goodsDate,
                shortDate,
                invetoryCount;

    public ProgressDialog progressDialog;
    String title,msg;
    InfinityDB db;
    LinearLayout SyncInvetory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        db = new InfinityDB(this);

        SyncInvetory = findViewById(R.id.SyncInvetory);

        progressDialog = new ProgressDialog(SyncDataActivity.this);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);



        setting = findViewById(R.id.Setting);
        settingDate = findViewById(R.id.settingDate);

        prudects = findViewById(R.id.productsData);
        productsDate = findViewById(R.id.productsDate);

        units = findViewById(R.id.UnitsDate);
        unitsDate= findViewById(R.id.unitsDate);

        suppliers = findViewById(R.id.SuppliersDate);
        supDate = findViewById(R.id.supgDate);

        goods = findViewById(R.id.GoodsData);
        Shortfalls = findViewById(R.id.Shortfalls);

        invetoryCount = findViewById(R.id.invetoryCount);
        int count = db.getInvetory();
        if(count > 0){
            invetoryCount.setText(" تم جرد "+Integer.toString(count)+" صنف/أصناف ");
            invetoryCount.setVisibility(View.VISIBLE);
        }

        CheckSyncedData();

        SyncInvetory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog dialog = new AlertDialog.Builder(SyncDataActivity.this)
                        .setTitle("مزامنة الاصناف !")
                        .setMessage("هل انت متأكد من عملية المزامنة ؟")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                title = "مزامنة الاصناف";
                                msg = "الرجاء الانتظار حتى يتم تصدير الاصناف..";
                                progressDialog.show();
                                Cursor result = db.ExbordSetting();
                                result.moveToFirst();
                                new SyncData().execute(result.getString(7));
                            }
                        })
                        .setNegativeButton("لا", null)
                        .show();

            }
        });

    }


    private void CheckSyncedData(){
        InfinityDB db = new InfinityDB(this);

        Cursor res;

        res = db.checkIfproductsSynced();
            if(res.getCount() > 0){
            res.moveToFirst();
            prudects.setChecked(true);
        }else
            prudects.setChecked(false);
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

        MainActivity.This.finish();
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
        finish();
    }

    private class SyncData extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection;

            String url = "http://"+strings[0]+"/api/InfinityRetail";

            String result = "false";


            try { // connection

                Cursor res = db.syncAllData();

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;

                int count = 0;
                while (res.moveToNext()) {

                    jsonObject = new JSONObject();
                    jsonObject.put("ID", res.getString(0));
                    jsonObject.put("Product_ID_PK", res.getString(1));
                    jsonObject.put("ProductName", res.getString(2));
                    jsonObject.put("UOMName", res.getString(3));
                    jsonObject.put("UOMID_PK", res.getString(4));
                    jsonObject.put("BaseUnitQ", res.getString(5));
                    jsonObject.put("Barcode", res.getString(6));
                    jsonObject.put("Quantity", res.getString(7));
                    jsonObject.put("ExDate", res.getString(8));
                    jsonObject.put("DateTime", res.getString(9));
                    jsonObject.put("DeviceID", res.getString(10));
                    jsonObject.put("userName", res.getString(11));
                    jsonArray.put(count, jsonObject);
                    count++;
                }


                urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();
                // write
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(jsonArray.toString());
                writer.close();
                outputStream.close();


                //// read

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String line = bufferedReader.readLine();
                return line;


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.compareTo("true") == 0){

                Toast.makeText(SyncDataActivity.this, "تمت عملية المزامنة", Toast.LENGTH_SHORT).show();

                InfinityDB db = new InfinityDB(SyncDataActivity.this);
                db.DropAllData();

                Intent intent = new Intent(SyncDataActivity.this, inventroyscan.class);
                startActivity(intent);
                SyncDataActivity.this.finish();


            }else{

                Toast.makeText(SyncDataActivity.this, "فشلت عملية المزامنة !", Toast.LENGTH_SHORT).show();

            }

            progressDialog.dismiss();
        }
    }

}
