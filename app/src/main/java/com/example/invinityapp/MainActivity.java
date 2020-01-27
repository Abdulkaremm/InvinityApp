package com.example.invinityapp;
import com.example.invinityapp.ExportAPurchaseBill.PurchaseBillActivity;
import com.example.invinityapp.Transport_of_goods.TransportGoods;
import com.example.invinityapp.goods.receiving_goods_main;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.example.invinityapp.inventoryitems.inventroyscan;
import com.example.invinityapp.registration_of_deficiencies.RegistrationOfDeficiencies;
import com.example.invinityapp.viewcategory.DataOrder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static Context activity;
    public static Activity This;
    public static ProgressDialog progressDialog;
    Dialog TypeInventroryScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* Intent intent =new Intent(this, ProductHaveDate.class);
        startActivity(intent);*/

        This = this;
        TypeInventroryScan = new Dialog(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("أستراد الاصناف");
        progressDialog.setMessage("يتم العمل على أستراد بيانات الاصناف...");
        progressDialog.setCanceledOnTouchOutside(false);

        activity = this;

    }

    public void invetoryScan(View view){

        Intent intent = new Intent(this, DataOrder.class);
        startActivity(intent);
    }

    public void TypeInventory(View view){

        TypeInventroryScan.setContentView(R.layout.popup_menu);
        TextView close = TypeInventroryScan.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TypeInventroryScan.dismiss();
            }
        });

        TypeInventroryScan.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater =  getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.Setting:

                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);

                return true;

            case R.id.SyncData:

               Intent intent1 = new Intent(this,SyncDataActivity.class);
                intent1.putExtra("activity","main");
                startActivity(intent1);

                return true;

            case R.id.ImportProducts:

                progressDialog.show();

                new ImportProducts().execute(this);

                return true;

           /* case R.id.ImportAllUnits:

                progressDialog.show();
                new ImportAllUnits().execute(this);

                return true;


            case R.id.ImportAllSuppliers:

                progressDialog.show();
                new ImportAllSuppliers().execute(this);

                return true;*/


            default: return super.onOptionsItemSelected(item);

        }
    }




    public  void  QuickInventory(View view){

        TypeInventroryScan.dismiss();
        InfinityDB db = new InfinityDB(this);
        ContentValues data = new ContentValues();

        data.put("QuickInvetory", 1);
        db.updateSetting(data);
        Intent intent = new Intent(this, inventroyscan.class);
        startActivity(intent);

    }

    public  void  transformGods(View view){

        Intent intent = new Intent(this, TransportGoods.class);
        startActivity(intent);
    }

    public  void  NormalInventory(View view){

        TypeInventroryScan.dismiss();
        InfinityDB db = new InfinityDB(this);
        ContentValues data = new ContentValues();

        data.put("QuickInvetory", 0);
        db.updateSetting(data);

        Intent intent = new Intent(this, inventroyscan.class);
        startActivity(intent);

    }

    //// method for recievingGoods package

    public void recievingGoodsDep(View view){

        InfinityDB db = new InfinityDB(this);
        if(db.IfUintsSynced()){

            Intent intent = new Intent(this, receiving_goods_main.class);
            startActivity(intent);

        }else{

            Toast.makeText(activity, "الرجاء استراد وحدات الاصناف من المنظومة الرئيسية", Toast.LENGTH_SHORT).show();
        }


    }


    public void PurchaseBillsDep(View v){

        Intent intent = new Intent(this, PurchaseBillActivity.class);
        startActivity(intent);


    }



    public void PURCHASE_ORDERS(View view){


        Intent intent = new Intent(this, RegistrationOfDeficiencies.class);
        startActivity(intent);


    }



}
