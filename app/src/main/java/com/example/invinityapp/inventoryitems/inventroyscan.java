package com.example.invinityapp.inventoryitems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.MainActivity;
import com.example.invinityapp.R;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class inventroyscan extends AppCompatActivity {

    public static InfinityDB mydata;
    public static TextView DNF;
    public SwipeMenuListView dataLists;
    private ArrayList<ProductData> productData;
    public Activity activity = this;
    public boolean ifHasData = false;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventroyscan);


        mydata = new InfinityDB(this);


        DNF =  findViewById(R.id.dataNotFound);
        dataLists = findViewById(R.id.listData);

        progressDialog = new ProgressDialog(inventroyscan.this);
        progressDialog.setTitle("مزامنة البيانات");
        progressDialog.setMessage("يتم العمل على مزامنة البيانات...");
        progressDialog.setCanceledOnTouchOutside(false);


        Cursor res = inventroyscan.mydata.selectData();


        if (res.getCount() == 0) {

            DNF.setText("لا توجد اصناف لعرضها");
            ifHasData = false;

        } else {

            ifHasData = true;

            DNF.setText("قائمة بأخر 30 صنف");

            productData = new ArrayList<>();
            IventoryAdabter productAdabter =new IventoryAdabter(this, productData);

            int i = 1;

            while (res.moveToNext()) {

                productAdabter.add(new ProductData(i, Integer.parseInt(res.getString(0)),res.getString(2), res.getString(3), res.getString(6),res.getString(7),res.getString(8) ));
                i++;
            }


            dataLists.setAdapter(productAdabter);

        }


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

               /* SwipeMenuItem view = new SwipeMenuItem(getApplicationContext());

                // set item background

                view.setBackground(R.color.colorMain);
                // set item width
                view.setWidth(170);
                // set item title
                view.setIcon(R.drawable.ic_eye);

                // add to menu
                menu.addMenuItem(view);*/

                // create "open" item

                SwipeMenuItem Update = new SwipeMenuItem(getApplicationContext());

                // set item background

                Update.setBackground(R.color.colorSuccess);
                // set item width
                Update.setWidth(170);
                // set item title
                Update.setIcon(R.drawable.ic_update);

                // add to menu
                menu.addMenuItem(Update);

                // create "delete" item
                SwipeMenuItem delete = new SwipeMenuItem(getApplicationContext());
                // set item background
                delete.setBackground(R.color.colorDanger);
                // set item width
                delete.setWidth(170);
                // set a icon
                delete.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(delete);
            }
        };

        dataLists.setMenuCreator(creator);

        dataLists.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                  /*  case 0:

                        //ViewData(productData.get(position).Product_id);

                        break;*/

                    case 0:

                        update(productData.get(position).Product_id);

                        break;
                    case 1:

                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle("مسح صنف !")
                                .setMessage("هل انت متأكد من عملية الحدف ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteFromDB(productData.get(position).Product_id);
                                    }
                                })
                                .setNegativeButton("لا", null)
                                .show();


                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });


    }

    private void ViewData(int id){

        Cursor res = mydata.getById(Integer.toString(id));

        Intent intent = new Intent(inventroyscan.this, ViewInfoProducts.class);
        intent.putExtra("productID", res.getString(1));
        intent.putExtra("plase", "1");
        startActivity(intent);
        inventroyscan.this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

       MenuItem addNew = menu.findItem(R.id.addnew);
       MenuItem sync = menu.findItem(R.id.sync);
       MenuItem viewData = menu.findItem(R.id.viewAllData);

       MenuItem type = menu.findItem(R.id.inventoryType);
       TextView txt = (TextView) type.getActionView();
       txt.setTextColor(Color.WHITE);


        //#######################################جلب نوع الجرد##################
        InfinityDB db =new InfinityDB(this);
        Cursor ifQuick = db.ExbordSetting();
        ifQuick.moveToFirst();
        if(Integer.parseInt(ifQuick.getString(6)) == 0 ){

            txt.setText("جرد عادي");

        }else{

            txt.setText("جرد سريع");

        }

        ifQuick.close();

        //#######################################


       addNew.setVisible(true);
       sync.setVisible(true);
       viewData.setVisible(true);
       type.setVisible(true);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                backToMainList(null);

                return true;

            case R.id.addnew:

                searchBarCodeInventoryDep(null);

                return true;

            case R.id.sync:

                if(checkConnect()){


                    if(ifHasData){

                        AlertDialog dialog = new AlertDialog.Builder(activity)
                                .setTitle("مزامنة الاصناف !")
                                .setMessage("هل انت متأكد من عملية المزامنة ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.show();
                                        Cursor result = mydata.ExbordSetting();
                                        result.moveToFirst();
                                        new SyncData().execute(result.getString(7));
                                    }
                                })
                                .setNegativeButton("لا", null)
                                .show();

                    }else{

                        Toast.makeText(this, "لاتوجد بيانات لمزامنتها", Toast.LENGTH_LONG).show();

                    }
                }else{

                    Toast.makeText(this, "لايوجد اتصال بالشبكة الرجاء التحقق من الشبكة و اعادة المحاولة", Toast.LENGTH_LONG).show();
                }



                return true;

            case R.id.viewAllData:

                Intent intent = new Intent(inventroyscan.this, ViewAllDataInventory.class);
                startActivity(intent);
                inventroyscan.this.finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private boolean checkConnect(){

        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }



    public void searchBarCodeInventoryDep(View view) {

        Intent intent = new Intent(this, QuickAdd.class);
        startActivity(intent);
        finish();

    }

    public void backToMainList(View view) {

        MainActivity.This.finish();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void update(int id) {

        InfinityDB db = new InfinityDB(this);

        Cursor res = db.getById(Integer.toString(id));

        Intent intent;
        res.moveToFirst();

        if(!res.getString(5).isEmpty()){

            intent =  new Intent(this, UpdateProductHaveDate.class);
            intent.putExtra("activity", "1");
            intent.putExtra("Barcode", res.getString(3));
            startActivity(intent);
            this.finish();

        }else{

            intent =  new Intent(this, UpdateDate.class);

            intent.putExtra("activity", "1");
            intent.putExtra("id", res.getString(0));
            intent.putExtra("Product_ID_PK", res.getString(1));
            intent.putExtra("ProductName", res.getString(2));
            intent.putExtra("barcode", res.getString(3));
            intent.putExtra("qun", res.getString(4));

            startActivity(intent);
            finish();
        }

    }

    public void deleteFromDB(int id) {


        int res = mydata.DeleteInventory(Integer.toString(id));

        if (res > 0) {

            Toast.makeText(this, "تم مسح الصنف", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, inventroyscan.class);
            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "حدث خطأ ما الرجاء إعادة المحاولة", Toast.LENGTH_LONG).show();

        }
    }




    private class SyncData extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... strings) {

            HttpURLConnection urlConnection;

            String url = "http://"+strings[0]+"/api/InfinityRetail";

            String result = "false";


            try { // connection

                Cursor res = mydata.syncAllData();

                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject;

                int count = 0;
                while (res.moveToNext()) {

                    jsonObject = new JSONObject();
                    jsonObject.put("ID", res.getString(0));
                    jsonObject.put("Product_ID_PK", res.getString(1));
                    jsonObject.put("ProductName", res.getString(2));
                    jsonObject.put("UOMName", res.getString(3));
                    jsonObject.put("UOMID_PK", res.getString(4));
                    jsonObject.put("BaseUnitQ", res.getString(5));
                    jsonObject.put("Barcode", res.getString(6));
                    jsonObject.put("Quantity", res.getString(7));
                    jsonObject.put("ExDate", res.getString(8));
                    jsonObject.put("DateTime", res.getString(9));
                    jsonObject.put("DeviceID", res.getString(10));
                    jsonObject.put("userName", res.getString(11));
                    jsonArray.put(count, jsonObject);
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
                writer.write(jsonArray.toString());
                writer.close();
                outputStream.close();


                //// read

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                String line = bufferedReader.readLine();
                return line;


            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.compareTo("true") == 0){

                Toast.makeText(inventroyscan.this, "تمت عملية المزامنة", Toast.LENGTH_SHORT).show();

                InfinityDB db = new InfinityDB(inventroyscan.this);
                db.DropAllData();

                Intent intent = new Intent(inventroyscan.this, inventroyscan.class);
                startActivity(intent);
                inventroyscan.this.finish();


            }else{

                Toast.makeText(inventroyscan.this, "فشلت عملية المزامنة !", Toast.LENGTH_SHORT).show();

            }

            progressDialog.dismiss();
        }
    }

}
