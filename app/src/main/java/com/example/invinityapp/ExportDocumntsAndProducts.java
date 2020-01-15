package com.example.invinityapp;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.invinityapp.Transport_of_goods.TransportGoods;
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
import java.net.ProtocolException;
import java.net.URL;

public class ExportDocumntsAndProducts extends AsyncTask<Context, Void, Boolean> {

    private Context context;

    @Override
    protected Boolean doInBackground(Context... contexts) {



        context = contexts[0];

        HttpURLConnection urlConnection;

        InfinityDB db = new InfinityDB(contexts[0]);
        Cursor result = db.ExbordSetting();
        result.moveToFirst();

        String url = "http://"+result.getString(7)+"/api/InfinityRetail/StockTransfer";

        Cursor dv = db.ExbordSetting();
        dv.moveToFirst();
        String ip = dv.getString(8);

        try { // connection

            JSONArray MainArray = new JSONArray();

            Cursor res = db.SelectAllDocument();
            int count = 0;

            while (res.moveToNext()) {
                JSONObject MainObj;

                MainObj = new JSONObject();
                MainObj.put("FromBranch", Integer.parseInt(res.getString(5)));
                MainObj.put("FromLocation", Integer.parseInt(res.getString(7)));
                MainObj.put("ToBarnch", Integer.parseInt(res.getString(9)));
                MainObj.put("ToLocation", Integer.parseInt(res.getString(11)));
                MainObj.put("CreateDate", res.getString(1));
                MainObj.put("DeviceID", ip);
                MainObj.put("Note", res.getString(3));

                JSONArray ScundArray = new JSONArray();

                Cursor products = db.SelectAllDocumentProduct(res.getString(0));

                int count2 = 0;

                while (products.moveToNext()){

                    JSONObject ScundObj = new JSONObject();

                    ScundObj.put("ProductID", Integer.parseInt(products.getString(1)));
                    ScundObj.put("ProductName", products.getString(2));
                    ScundObj.put("UOM_FK", Integer.parseInt(products.getString(4)));
                    ScundObj.put("BaseUOM", products.getString(5));
                    ScundObj.put("Barcode", products.getString(6));
                    ScundObj.put("Quantity", Integer.parseInt(products.getString(7)));
                    ScundObj.put("ExpiryDate", products.getString(8));
                    ScundObj.put("CountingDate", products.getString(9));
                    ScundObj.put("DeviceID", products.getString(10));

                    ScundArray.put(ScundObj);

                    ScundArray.put(count2, ScundObj);
                    count2++;

                }

                MainObj.put("Products", ScundArray);
                MainArray.put(count, MainObj);
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
            writer.write(MainArray.toString());
            writer.close();
            outputStream.close();


            //// read

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line = bufferedReader.readLine();

            Log.i("DATA", MainArray.toString());

            if(line.compareTo("true") == 0){

                return true;

            }else{

                return false;
            }


        }catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);


        if(aBoolean){

            Toast.makeText(context, "تمت عملية المزامنة بنجاح", Toast.LENGTH_SHORT).show();
        }else{

            Toast.makeText(context, "فشلت عملية المزامنة", Toast.LENGTH_SHORT).show();

        }

        TransportGoods.progressDialog.dismiss();
    }
}
