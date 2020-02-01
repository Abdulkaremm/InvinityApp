package com.example.invinityapp.viewcategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ViewDataOfStock extends AppCompatActivity {


    private ProgressBar progressBar;
    private TextView ProductName;
    private ListView StockList;
    private ArrayList<StockModel> models;
    private InfinityDB db;
    private TextView errmsg;
    private StockAdapter stockAdapter;
    private String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data_of_stock);

        db = new InfinityDB(this);

        errmsg = findViewById(R.id.ErrorMsg);
        progressBar = findViewById(R.id.ProgressBar);
        StockList = findViewById(R.id.stockList);
        ProductName = findViewById(R.id.name);

        models = new ArrayList<>();

        stockAdapter = new StockAdapter(this, models);

        Intent intent = getIntent();
        barcode = intent.getStringExtra("productID");
        Cursor re = db.isExistBar(barcode);

        re.moveToFirst();

        ProductName.setText(re.getString(1));



        new fetchDate().execute();

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

                Intent intent = new Intent(ViewDataOfStock.this, DataOrder.class);

                intent.putExtra("barcode", barcode);
                startActivity(intent);
                ViewDataOfStock.this.finish();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class fetchDate extends AsyncTask<Void, String, String>{


        @Override
        protected String doInBackground(Void... voids) {

            HttpURLConnection urlConnection;
            InputStream stream;
            BufferedReader reader;

            Cursor result = db.ExbordSetting();
            result.moveToFirst();

            String Response = "";

            //String url = "http://"+result.getString(7)+"/api/InfinityRetail/GetAllProducts";
            String url = "https://api.myjson.com/bins/aipaq";


            try {  // set connection to url
                urlConnection = (HttpURLConnection) (new URL(url)).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();

                /// reading the response from the ulr
                stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream,"utf-8"));

                String data = reader.readLine();
                while (reader.readLine() != null){

                    data += reader.readLine();
                }

                JSONObject mainObj = new JSONObject(data);

                Response = mainObj.getString("ResponseCode");

                if(mainObj.getString("ResponseCode").compareTo("500") == 0){


                    JSONArray array = mainObj.getJSONArray("Products");


                    for (int i = 0; i < array.length() ;i++){

                        Log.i("hhh", "data = " + i);


                        JSONObject sucndObj = array.getJSONObject(i);

                        stockAdapter.add(new StockModel(sucndObj.getString("BranchName"), sucndObj.getString("LocationName"), sucndObj.getString("UOMName"), sucndObj.getString("StockOnHand"), sucndObj.getString("ExpiryDate"), sucndObj.getString("SyncExpiryDate")));

                    }

                }



            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();

            }

            return Response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.compareTo("500") == 0){

                progressBar.setVisibility(View.GONE);

                StockList.setAdapter(stockAdapter);
                StockList.setVisibility(View.VISIBLE);
            }
        }
    }

}
