package com.example.invinityapp.registration_of_deficiencies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.MainActivity;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.example.invinityapp.inventoryitems.IventoryAdabter;
import com.example.invinityapp.inventoryitems.ProductData;

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
import java.util.ArrayList;

public class RegistrationOfDeficiencies extends AppCompatActivity {


    private  InfinityDB db;
    private  TextView DNF;
    private SwipeMenuListView dataLists;
    private ArrayList<ProductData> productData;
    public boolean ifHasData = false;

    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_of_deficiencies);


        db = new InfinityDB(this);

        DNF =  findViewById(R.id.dataNotFound);
        dataLists = findViewById(R.id.listData);
        progressDialog = new ProgressDialog(RegistrationOfDeficiencies.this);
        progressDialog.setTitle("مزامنة البيانات");
        progressDialog.setMessage("يتم العمل على مزامنة البيانات...");
        progressDialog.setCanceledOnTouchOutside(false);



        Cursor res = db.SelectPurchaseProducts();

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

                productAdabter.add(new ProductData(i, Integer.parseInt(res.getString(0)),res.getString(2), res.getString(3), res.getString(6),res.getString(7),""));
                i++;
            }


            dataLists.setAdapter(productAdabter);
        }

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

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
                    case 0:

                        update(productData.get(position).Product_id);

                        break;
                    case 1:

                        AlertDialog dialog = new AlertDialog.Builder(RegistrationOfDeficiencies.this)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem addNew = menu.findItem(R.id.addnew);
        MenuItem sync = menu.findItem(R.id.sync);

        MenuItem type = menu.findItem(R.id.inventoryType);
        TextView txt = (TextView) type.getActionView();
        txt.setTextColor(Color.WHITE);

        txt.setText("تسجيل النواقص");



        //#######################################

        type.setVisible(true);
        addNew.setVisible(true);
        sync.setVisible(true);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                MainActivity.This.finish();
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                RegistrationOfDeficiencies.this.finish();

                return true;

            case R.id.addnew:

                Intent intent = new Intent(this, ScanPurchaseProduct.class);
                startActivity(intent);
                finish();

                return true;

            case R.id.sync:

                if(checkConnect()){


                    if(ifHasData){

                        AlertDialog dialog = new AlertDialog.Builder(RegistrationOfDeficiencies.this)
                                .setTitle("مزامنة الاصناف !")
                                .setMessage("هل انت متأكد من عملية المزامنة ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        progressDialog.show();
                                        Cursor result = db.ExbordSetting();
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



    public void update(int id) {

        Cursor res = db.getPurchaseProductById(Integer.toString(id));

        Intent intent;
        res.moveToFirst();

        intent =  new Intent(this, UpdatePurchaseProduct.class);
        intent.putExtra("activity", "1");
        intent.putExtra("id", res.getString(0));
        intent.putExtra("Product_ID_PK", res.getString(1));
        intent.putExtra("ProductName", res.getString(2));
        intent.putExtra("barcode", res.getString(3));
        intent.putExtra("qun", res.getString(4));
        intent.putExtra("discription", res.getString(5));
        startActivity(intent);

        finish();

    }

    public void deleteFromDB(int id) {


        int res = db.DeletePurchaseProductByID(Integer.toString(id));

        if (res > 0) {

            Toast.makeText(this, "تم مسح الصنف", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, RegistrationOfDeficiencies.class);
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

            String url = "http://"+strings[0]+"/api/InfinityRetail/StockOrders";

            String result = "false";


            try { // connection

                Cursor res = db.syncAllPurchaseData();

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
                    jsonObject.put("Description", res.getString(8));
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

                Toast.makeText(RegistrationOfDeficiencies.this, "تمت عملية المزامنة", Toast.LENGTH_SHORT).show();

                InfinityDB db = new InfinityDB(RegistrationOfDeficiencies.this);
                db.DropAllPurchaseData();

                Intent intent = new Intent(RegistrationOfDeficiencies.this, RegistrationOfDeficiencies.class);
                startActivity(intent);
                RegistrationOfDeficiencies.this.finish();


            }else{

                Toast.makeText(RegistrationOfDeficiencies.this, "فشلت عملية المزامنة !", Toast.LENGTH_SHORT).show();

            }

            progressDialog.dismiss();
        }
    }

}
