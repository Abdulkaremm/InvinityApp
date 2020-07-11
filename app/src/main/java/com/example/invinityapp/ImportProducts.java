package com.example.invinityapp;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ImportProducts extends AsyncTask<Context,Void,Integer> {

    public Context context;
    public String bdate , edate;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        bdate = simpleDateFormat.format(new Date());




    }

    @Override
    protected Integer doInBackground(Context... con) {

        context = con[0];
        HttpURLConnection urlConnection;
        InputStream stream;
        BufferedReader reader;

        InfinityDB db = new InfinityDB(con[0]);
        Cursor result = db.ExbordSetting();
        result.moveToFirst();

        String url = "http://"+result.getString(7)+"/api/InfinityRetail/GetAllProducts";


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

            /// fetch json data and insert it into database

            JSONArray mainArray = new JSONArray(data);

            ContentValues tabel1 = new ContentValues();
            ContentValues tabel2 = new ContentValues();
            ContentValues tabel3 = new ContentValues();
            int i;
            for( i = 0; i < mainArray.length(); i++){
                JSONObject jsonObject =  mainArray.getJSONObject(i);

                tabel1.put("Product_ID_PK", Integer.parseInt(jsonObject.get("ProductID_PK").toString()) );
                tabel1.put("Product_Code", jsonObject.get("ProductCode").toString());
                tabel1.put("Product_Name", jsonObject.get("ProductName").toString());
                tabel1.put("invSub_Department", jsonObject.get("SubDepartmentName").toString());
                tabel1.put("Product_Group", jsonObject.get("GroupDescription").toString());
                tabel1.put("TrademarkDescrption", jsonObject.get("TrademarkDescrption").toString());
                tabel1.put("ProductTypeID_FK", jsonObject.get("ProductTypeID_FK").toString());
                tabel1.put("StockOnHand", jsonObject.get("StockOnHand").toString());
                tabel1.put("Modified_Date", jsonObject.get("ModifiedDate").toString());
                tabel1.put("Create_Date", jsonObject.get("CreatedDate").toString());
                tabel1.put("ProductType", jsonObject.get("ProductTypeID_FK").toString());

              db.InsertDATA_PRODUCTS(tabel1);


               JSONArray units = jsonObject.getJSONArray("ProductUOM"); // get units array

                for(int u = 0; u < units.length(); u++){

                    JSONObject uitejson = units.getJSONObject(u);
                    tabel2.put("ProductUOMID_PK", Integer.parseInt(uitejson.get("ProductUomID_PK").toString()));
                    tabel2.put("ProductID_FK", Integer.parseInt(jsonObject.get("ProductID_PK").toString()));
                    tabel2.put("UOMID_PK", Integer.parseInt(uitejson.get("UOMID_PK").toString()));
                    tabel2.put("UOM_NAME", uitejson.get("UOMName").toString());
                    tabel2.put("BaseUnit", uitejson.get("BaseUnitQYT").toString());
                    tabel2.put("UomCost", uitejson.get("UomCost").toString());
                    tabel2.put("UomLast", uitejson.get("UomLastPurchaseCost").toString());
                    tabel2.put("UomPrice1", uitejson.get("UomPrice1").toString());
                    tabel2.put("UomPrice2", uitejson.get("UomPrice2").toString());
                    tabel2.put("UomPrice3", uitejson.get("UomPrice3").toString());
                    tabel2.put("UomPrice4", uitejson.get("UomPrice4").toString());

                    /// insert to database below

                   db.InsertDATA_PRODUCTS_UOMS(tabel2);
                    Log.i("Count OF UOMS", Integer.toString(u));


                    JSONArray barcodes = uitejson.getJSONArray("Barcode"); // get barcodes array

                    for(int b = 0; b < barcodes.length(); b++){

                        JSONObject barcode = barcodes.getJSONObject(b);

                        tabel3.put("ProductBarcodeID_PK", Integer.parseInt(barcode.get("BarcodeID_PK").toString()));
                        tabel3.put("ProductUOMID_FK", Integer.parseInt(uitejson.get("ProductUomID_PK").toString()));
                        tabel3.put("ProductBarcode", barcode.get("Barcode").toString());

                        db.InsertDATA_PRODUCT_BARCODES(tabel3);

                        tabel3.clear();
                    }

                    tabel2.clear();

                }

                tabel1.clear();



                Log.i("Count OF PRODUCTS", Integer.toString(i));


            }


            return i; // if the response is ok

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Log.i("ERROR MSG", Objects.requireNonNull(e.getMessage()));
        }


        return -1; // if something wrong happened
    }


    @Override
    protected void onPostExecute(Integer response) {
        super.onPostExecute(response);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        edate = simpleDateFormat.format(new Date());

        Log.i("Begin Date", bdate);
        Log.i("End Date", edate);


        SyncDataActivity.progressDialog.dismiss();


        if(response == -1){

            Toast.makeText(context, "فشلت عملية الاستراد", Toast.LENGTH_SHORT).show();

        }else{

            String msg = "تم أستراد ( " + response + ") صنف";
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

        }


    }
}
