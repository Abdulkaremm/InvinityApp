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
    private String ProductID_PK;

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

        if(re.getCount() != 0){

            ProductName.setText(re.getString(1));
            ProductID_PK = re.getString(0);

        }else{

            Intent intent2 = new Intent(ViewDataOfStock.this, DataOrder.class);

            intent.putExtra("barcode", barcode);
            startActivity(intent2);
            ViewDataOfStock.this.finish();
        }



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
            String url = "http://"+result.getString(11)+"//InfinityRetailAPI/v1.0/InfinityRetail/GetProductInventory?CustomerCode="+result.getString(10)+"&ProductID="+ProductID_PK+"&CurrentBranchID=0";

           //String url = "http://41.208.71.100:5252//InfinityRetailAPI/v1.0/InfinityRetail/GetProductInventory?CustomerCode=926405120&ProductID=177793&CurrentBranchID=0";



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


                if(mainObj.getString("ResponseCode").compareTo("500") == 0){

                    Response = mainObj.getString("ResponseCode");


                    JSONArray array = mainObj.getJSONArray("Products");



                    for (int i = 0; i < array.length() ;i++){

                        JSONObject sucndObj = array.getJSONObject(i);

                        String ex = "لايدعم تاريخ الصلاحية";

                        if(sucndObj.getString("ExpiryDate").compareTo("null") != 0){

                            ex = sucndObj.getString("ExpiryDate");

                        }
                        stockAdapter.add(new StockModel(sucndObj.getString("BranchName"), sucndObj.getString("LocationName"), sucndObj.getString("UOMName"), sucndObj.getString("StockOnHand"), ex, sucndObj.getString("LastSync")));

                    }







                }else{

                    Response = mainObj.getString("ResponseMsg");
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

            progressBar.setVisibility(View.GONE);

            if(s.compareTo("500") == 0){

                StockList.setAdapter(stockAdapter);
                StockList.setVisibility(View.VISIBLE);

            }else if(s.compareTo("") == 0){

                errmsg.setText("عذراً، لقد فشلت عملية الاتصال بالخادم");
                errmsg.setVisibility(View.VISIBLE);

            }else {

                errmsg.setText(s);
                errmsg.setVisibility(View.VISIBLE);
            }



        }
    }

}
