package com.example.invinityapp.viewcategory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class GetDataLoader extends AsyncTaskLoader<List<String>> {

    public static String name = "";
    static String dep = "";
    static String storage = "";
    private String code = DataOrder.Code;
    private String ip = DataOrder.api_ip;




    GetDataLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public List<String> loadInBackground() {

        ArrayList<String> result = new ArrayList<>();


        try {
            URL url = new URL("http://"+ip+"/api/InfinityRetail?Barcode=" + code);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream , Charset.forName("UTF-8")));


            StringBuilder data = new StringBuilder(bufferedReader.readLine());
            while(bufferedReader.readLine() != null){

                data.append(bufferedReader.readLine());

            }

            JSONObject jsonObject = new JSONObject(data.toString());

            name = String.valueOf(jsonObject.get("ProductName"));
            dep = String.valueOf(jsonObject.get("InvSubDepartmentName"));
            storage = String.valueOf(jsonObject.get("StockOnHand"));

            JSONArray subArray = jsonObject.getJSONArray("units");


            for (int i = 0; i < subArray.length(); i++){

                JSONObject  unitData =  subArray.getJSONObject(i);

                result.add(unitData.get("UOMName").toString());
                result.add(unitData.get("UomPrice1").toString());
                result.add(unitData.get("UomPrice2").toString());
                result.add(unitData.get("UomPrice3").toString());
                result.add(unitData.get("UomPrice4").toString());

            }


            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

}
