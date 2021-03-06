package com.example.invinityapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.invinityapp.goods.receiving_goods_main;
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
import java.util.Objects;

public class ExportReceivedGoods extends AsyncTask<Context,Void,Boolean> {

    @SuppressLint("StaticFieldLeak")
    Context context;
    String id;
    public ExportReceivedGoods(String ID){

        this.id = ID;
    }

    @Override
    protected Boolean doInBackground(Context... contexts) {

        context = contexts[0];
        InfinityDB db = new InfinityDB(contexts[0]);
        Cursor result = db.ExbordSetting();
        result.moveToFirst();

        String url = "http://"+result.getString(7)+"/api/InfinityRetail/ReceiveData";
        OutputStream outputStream;
        BufferedWriter writer;
        HttpURLConnection urlConnection;
        String line = "false";


        try {



            Cursor res =  db.IfOpenReceiving(id);


            if(res.getCount() > 0){



                int count = 0;
                JSONArray Receive = new JSONArray();
                while(res.moveToNext()){ // while-1


                    JSONObject object = new JSONObject();

                    object.put("SupplierID",Integer.parseInt(res.getString(4)));
                    object.put("SupplierName",res.getString(1));
                    object.put("BillNamber",Integer.parseInt(res.getString(3)));
                    object.put("ReceiveDate",res.getString(2));
                    object.put("DeviceID",result.getString(8));




                    Cursor pro = db.GetGoodsBySupllier(Integer.parseInt(res.getString(0)));
                    int Count1 = 0;

                    JSONArray products = new JSONArray();
                    while (pro.moveToNext()){

                        JSONObject prudect = new JSONObject();

                        prudect.put("ProductID",Integer.parseInt(pro.getString(1)));
                        prudect.put("ProductName",pro.getString(4));
                        prudect.put("UOM_FK",pro.getString(3));
                        prudect.put("BaseUOM",pro.getString(8));
                        prudect.put("Quantity",pro.getString(5));
                        prudect.put("Barcode",pro.getString(9));
                        if(pro.getString(6) != null)
                            prudect.put("ExpiryDate",pro.getString(6));
                        else
                            prudect.put("ExpiryDate","");
                        prudect.put("CountingDate",pro.getString(7));
                        prudect.put("DeviceID",result.getString(8));
                        prudect.put("IsNew",pro.getString(10));


                        products.put(Count1,prudect);
                        Count1++;
                    }

                    object.put("Products",products);
                    Receive.put(count,object);
                    count++;
                } // while-1 end here


                Log.i("OBJ",  Receive.toString());

                urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();
                // write

                outputStream = urlConnection.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                writer.write(Receive.toString());
                writer.close();
                outputStream.close();


                //// read

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                line = bufferedReader.readLine();



                if(line.compareTo("true") == 0){

                        db.DeleteGoodsAfterSync();

                    return true;
                }
                else
                    return false;

            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return false;
        }

        return Boolean.getBoolean(line);
    }

    @Override
    protected void onPostExecute(Boolean state) {
        super.onPostExecute(state);

        receiving_goods_main.progressDialog.dismiss();
        if(state)
            Toast.makeText(context,"تمت عملية التصدير بنجاح",Toast.LENGTH_SHORT).show();
        else
        Toast.makeText(context,"فشلت عملية التصدير",Toast.LENGTH_SHORT).show();

    }
}
