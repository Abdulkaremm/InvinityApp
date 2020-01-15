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
import java.net.URLConnection;
import java.nio.charset.Charset;

public class BarcodeChecker extends AsyncTask<String,Void,Boolean> {

    private String Code;

    @Override
    protected Boolean doInBackground(String... barcode) {

        Code = barcode[0];
        boolean isconnect = false;
        String path = "http://"+barcode[1]+"/api/InfinityRetail?Barcode=" + barcode[0];

        try {
            URL url = new URL(path);
            URLConnection connection = url.openConnection();
            ((URLConnection) connection).setConnectTimeout(3000);
            connection.connect();
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream , Charset.forName("UTF-8")));

            StringBuilder data = new StringBuilder(bufferedReader.readLine());
            while(bufferedReader.readLine() != null){

                data.append(bufferedReader.readLine());
            }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                JSONObject jsonObject = new JSONObject(data.toString());

                if (jsonObject.get("ProductCode").toString().compareTo("null") == 0){

                    return false;
                }else
                    return true;




        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        return false;
    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        DataOrder.isthere = aBoolean;
    }

}
