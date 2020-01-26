package com.example.invinityapp.viewcategory;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetStorage extends AsyncTask<String,Void,String> {





    @Override
    protected String doInBackground(String... barcode) {

        HttpURLConnection urlConnection;
        InputStream stream;
        BufferedReader reader;
        String  url = "http://"+barcode[1]+"/api/InfinityRetail?Barcode="+ barcode[0],
                storage = "";

        try {
            urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();



            stream  = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder data = new StringBuilder(reader.readLine());
            while(reader.readLine() != null){
                data.append(reader.readLine());
            }


            JSONObject jsonObject = new JSONObject(data.toString());

            storage = String.valueOf(jsonObject.get("StockOnHand"));


        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return storage;
    }

    @Override
    protected void onPostExecute(String storage) {
        super.onPostExecute(storage);

        if (storage.compareTo("null") == 0){

            DataOrder.storage.setText("غير متوفر");
        }else
            DataOrder.storage.setText(storage);

    }
}
