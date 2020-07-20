package com.example.invinityapp;

import android.annotation.SuppressLint;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImportProductPurchaseHistory extends AsyncTask<Context,Void,Boolean> {

    @SuppressLint("StaticFieldLeak")
    Context context;

    @Override
    protected Boolean doInBackground(Context... contexts) {

        context = contexts[0];
        InfinityDB db = new InfinityDB(contexts[0]);
        Cursor result = db.ExbordSetting();
        result.moveToFirst();

        HttpURLConnection urlConnection;
        InputStream stream;
        BufferedReader reader;
        String url = "http://" + result.getString(7) + "/api/InfinityRetail/ProductPurchaseHistory";
        String url2 = "http://" + result.getString(7) + "/api/InfinityRetail/ProductPurchaseHistoryConfirmation";

        new ImportProducts(2).execute(context);

       // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
       // String LastSync = simpleDateFormat.format(new Date());

        try {
            urlConnection = (HttpURLConnection) (new URL(url)).openConnection();
            stream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder data = new StringBuilder(reader.readLine());
            while (reader.readLine() != null){

                data.append(reader.readLine());
            }

            JSONArray array = new JSONArray(data.toString());
            for(int loop = 0; loop < array.length(); loop++){

                JSONObject jsonObject = array.getJSONObject(loop);
                ContentValues values = new ContentValues();

                values.put("ProductID_PK",jsonObject.getInt("ProductID_PK"));
                values.put("Supplier",jsonObject.getString("SupplierName"));
                values.put("Unit",jsonObject.getString("UOMName"));
                values.put("QYN",jsonObject.getString("QYT"));
                values.put("Price",jsonObject.getString("UniteCoast"));
                values.put("Date",jsonObject.getString("PurchaseDate"));
                values.put("PurchaseInvoiceItemID_PK",jsonObject.getString("PurchaseInvoiceItemID_PK"));


                if(!db.AddPurchaseHistory(values)) {
                    return false;
                }
            }

            urlConnection.disconnect();
            stream.close();
            reader.close();

            urlConnection = (HttpURLConnection) (new URL(url2)).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.connect();

            stream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream,"utf-8"));

            reader.readLine();


        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        SyncDataActivity.progressDialog.dismiss();

        if(state)
            Toast.makeText(context,"تمت عملية الاستراد بنجاح",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context,"فشلت عملية الاستراد",Toast.LENGTH_SHORT).show();

    }
}


// if(!db.CheckIfProductExist( jsonObject.getString("ProductID_PK") )) {
//
//
//         if(ProID.length() == 0){
//
//         JSONObject id = new JSONObject();
//         id.put("ProductID_PK", jsonObject.getString("ProductID_PK"));
//         ProID.put(id);
//
//         }else
//         for(int j = 0; j < ProID.length(); j++){
//
//        JSONObject proIDJSONObject =  ProID.getJSONObject(j);
//        if(proIDJSONObject.getString("ProductID_PK").compareTo(jsonObject.getString("ProductID_PK")) == 0){
//        addIn = false;
//        }else{
//        addIn = true;
//        }
//        }
//
//        if(addIn) {
//        JSONObject id = new JSONObject();
//        id.put("ProductID_PK", jsonObject.getString("ProductID_PK"));
//        ProID.put(id);
//
//        }
//        }
