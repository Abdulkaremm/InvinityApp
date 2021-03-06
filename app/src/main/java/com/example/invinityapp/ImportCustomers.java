package com.example.invinityapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImportCustomers extends AsyncTask<Context,Void,Boolean> {

    private String LastSync;
    @SuppressLint("StaticFieldLeak")
    Context context;

    @Override
    protected Boolean doInBackground(Context... contexts) {


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        LastSync = simpleDateFormat.format(new Date());

        context = contexts[0];
        InfinityDB db = new InfinityDB(contexts[0]);
        Cursor result = db.ExbordSetting();
        result.moveToFirst();

        HttpURLConnection urlConnection;
        InputStream stream;
        BufferedReader reader;
        String url ="http://"+result.getString(7)+"/api/InfinityRetail/GetAllCustomers";

        try {
            urlConnection = (HttpURLConnection) (new URL(url)).openConnection();
            stream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder data = new StringBuilder(reader.readLine());
            while (reader.readLine() != null){

                data.append(reader.readLine());
            }

            JSONArray array = new JSONArray(data.toString());
            ContentValues values = new ContentValues();
            for(int loop = 0; loop < array.length(); loop++){

                JSONObject jsonObject = array.getJSONObject(loop);

                values.put("ClientID_PK",jsonObject.getInt("CustomerID_PK"));
                values.put("Client_Name",jsonObject.getString("CustomerName"));
                values.put("Last_Sync_Date",LastSync);
                if(!db.AddAllCustomers(values)) {
                    return false;
                }
            }


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
