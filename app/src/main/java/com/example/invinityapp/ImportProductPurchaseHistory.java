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
        String[] url = new String[]{"http://" + result.getString(7) + "/api/InfinityRetail/ProductPurchaseHistory",
                                    "http://" + result.getString(7) + "/api/InfinityRetail/GetMissingProducts"};

        OutputStream outputStream;
        BufferedWriter writer;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String LastSync = simpleDateFormat.format(new Date());

        try {
            urlConnection = (HttpURLConnection) (new URL(url[0])).openConnection();
            stream = urlConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            StringBuilder data = new StringBuilder(reader.readLine());
            while (reader.readLine() != null){

                data.append(reader.readLine());
            }

            db.DeleteAllHistory();
            boolean addIn = false;
            JSONArray ProID = new JSONArray();
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

                if(!db.CheckIfProductExist( jsonObject.getString("ProductID_PK") )) {


                    if(ProID.length() == 0){

                        JSONObject id = new JSONObject();
                        id.put("ProductID_PK", jsonObject.getString("ProductID_PK"));
                        ProID.put(id);
                    }else
                        for(int j = 0; j < ProID.length(); j++){

                            JSONObject proIDJSONObject =  ProID.getJSONObject(j);
                            if(proIDJSONObject.getString("ProductID_PK").compareTo(jsonObject.getString("ProductID_PK")) == 0){
                                addIn = false;
                            }else{
                                addIn = true;
                            }
                        }

                    if(addIn) {
                        JSONObject id = new JSONObject();
                        id.put("ProductID_PK", jsonObject.getString("ProductID_PK"));
                        ProID.put(id);

                    }
                }
                if(!db.AddPurchaseHistory(values)) {
                    return false;
                }
            }

            stream.close();
            reader.close();
            urlConnection.disconnect();

            if(ProID.length() > 0){ // if-1


                urlConnection = (HttpURLConnection) ((new URL(url[1]).openConnection()));
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.connect();


                // write

                outputStream = urlConnection.getOutputStream();
                writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                writer.write(ProID.toString());
                writer.close();
                outputStream.close();


                /// read

                /// reading the response from the ulr
                stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

                StringBuilder Products = new StringBuilder(reader.readLine());
                while (reader.readLine() != null){

                    Products.append(reader.readLine());
                }

                /// fetch json data and insert it into database

                JSONArray mainArray = new JSONArray(Products.toString());

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

                }
            } // end of if-1




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
