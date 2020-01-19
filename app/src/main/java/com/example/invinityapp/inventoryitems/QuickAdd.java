package com.example.invinityapp.inventoryitems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



public class QuickAdd extends AppCompatActivity {


    InfinityDB db;
    private SwipeMenuListView dataLists;
    private ArrayList<ProductData> productData;
    private EditText  barcode;
    private TextView MSG;
    private String name, BaseUnit, UOMNAME, Product_ID, idUOMS;
    MediaPlayer error;
    private int quickFlag;
    private Dialog dialog;
    private String SearchUsePRODUCT_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_add);

        dialog = new Dialog(this);

        db = new InfinityDB(this);

        //#######################################جلب نوع الجرد##################

            Cursor ifQuick = db.ExbordSetting();
            ifQuick.moveToFirst();
            quickFlag = Integer.parseInt(ifQuick.getString(6));
            ifQuick.close();

        //#######################################



        dataLists = findViewById(R.id.listData);

        MSG = findViewById(R.id.msg);

        barcode  =  findViewById(R.id.barcode);
        barcode.requestFocus();

        barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {



                if((keyCode == 66 || keyCode ==301 || keyCode ==302 || keyCode ==303) && barcode.getText().toString().compareTo("") != 0){

                    CheckType();

                }

                return false;
            }
        });

       // CreateDilog();//########## To create Alarm Dilog
        createMenu();

    }


    public void createMenu(){

        Cursor res = db.selectData();
        productData = new ArrayList<>();
        IventoryAdabter productAdabter =new IventoryAdabter(this, productData);

        if (res.getCount() != 0) {
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

                        AlertDialog dialog = new AlertDialog.Builder(QuickAdd.this)
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

//########لفحص نوع الجرد
    public void CheckType(){

        Cursor result = db.getDataProduct(barcode.getText().toString());


       if (result.getCount() == 0) {

            MSG.setText("الصنف غير موجود الرجاء التأكد من الصنف");
            barcode.setText("");
            play();

        }else{


            if(quickFlag == 0){

                SendDataToAdd(result);

            }else{

                OnGetBarcode(result);

            }


        }

    }


    public void SendDataToAdd(Cursor result){

        result.moveToFirst();
        int x = db.checkInventoryByID(result.getString(0));

        if (x > 0) {

            SearchUsePRODUCT_ID = result.getString(0);

            CreateDilog();
            dialog.show();

        } else {

            Intent goTOAdd;


            if(Integer.parseInt(result.getString(5)) == 6) {

                goTOAdd = new Intent(QuickAdd.this, ProductHaveDate.class);
            }else{

                goTOAdd = new Intent(QuickAdd.this, InventoryAddData.class);
            }


            goTOAdd.putExtra("Barcode", barcode.getText().toString());

            barcode.setText("");
            startActivity(goTOAdd);
            finish();
        }

    }


    public void OnGetBarcode(Cursor result){

        result.moveToFirst();
        int x = db.checkInventoryByID(result.getString(0));

        if (x > 0) {

            Cursor res = db.checkBarcode(barcode.getText().toString());
            res.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put("cat_id", res.getString(0));
            contentValues.put("quantity", ( Integer.parseInt(res.getString(1) )+ 1 ));

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            contentValues.put("dateTime", df.format(c.getTime()));

            db.updateData(contentValues);

            MSG.setText("تم زيادة الكمية للصنف");
            barcode.setText("");
            createMenu();

        } else {

            //Log.i("BEFORE SEND", result.getString(5));

            if(Integer.parseInt(result.getString(5)) == 6 ){

                play();
                Intent goTOAdd = new Intent(QuickAdd.this, ProductHaveDate.class);

                goTOAdd.putExtra("Barcode", barcode.getText().toString());
                barcode.setText("");
                startActivity(goTOAdd);
                this.finish();

            }else if(result.getCount() > 1) {

                play();

                Intent goTOAdd = new Intent(QuickAdd.this, InventoryAddData.class);

                goTOAdd.putExtra("Barcode", barcode.getText().toString());
                barcode.setText("");
                startActivity(goTOAdd);
                finish();

            }else{

                result.moveToFirst();
                name       = result.getString(1);
                UOMNAME    = result.getString(2);
                idUOMS     =  result.getString(3);
                Product_ID = result.getString(0);
                BaseUnit   = result.getString(4);
                addItem();
            }

        }

    }

    public void addItem(){

            String bar = barcode.getText().toString(),
                    thisTime,
                    ip,
                    username = "", qunt = "1";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        thisTime = df.format(c.getTime());

            Cursor dv = inventroyscan.mydata.ExbordSetting();
            dv.moveToFirst();
            ip = dv.getString(8);

            ContentValues values = new ContentValues();
            values.put("Product_ID_PK", Product_ID);
            values.put("ProductName", name);
            values.put("UOMName", UOMNAME);
            values.put("UOMID_PK", idUOMS);
            values.put("BaseUnitQ", BaseUnit);
            values.put("barcode", bar);
            values.put("endDate", "");
            values.put("quantity", qunt);
            values.put("dateTime", thisTime);
            values.put("deviceIP", ip);
            values.put("userName", username);

            boolean ifInsert = inventroyscan.mydata.insertData(values);

            if (ifInsert) {

                MSG.setText("تمت عملية الاضافة");
                barcode.setText("");
                createMenu();


            } else {

                MSG.setText("فشلت عملية الاضافة");
                barcode.setText("");
            }


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem openCam = menu.findItem(R.id.openCam);

        MenuItem type = menu.findItem(R.id.inventoryType);
        TextView txt = (TextView) type.getActionView();
        txt.setTextColor(Color.WHITE);


        //####################################### جلب نوع الجرد##################

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

        openCam.setVisible(true);
        type.setVisible(true);


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                Intent intent = new Intent(this, inventroyscan.class);
                startActivity(intent);
                finish();

                return true;

            case R.id.openCam:

                openCam(null);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void update(int id) {

        InfinityDB db = new InfinityDB(this);

        Cursor res = db.getById(Integer.toString(id));

        Intent intent;
        res.moveToFirst();

        if(!res.getString(5).isEmpty()){

            intent =  new Intent(this, UpdateProductHaveDate.class);
            intent.putExtra("activity", "2");
            intent.putExtra("Barcode", res.getString(3));
            startActivity(intent);
            this.finish();

        }else {

            intent =  new Intent(this, UpdateDate.class);
            intent.putExtra("activity", "2");
            intent.putExtra("id", res.getString(0));
            intent.putExtra("Product_ID_PK", res.getString(1));
            intent.putExtra("ProductName", res.getString(2));
            intent.putExtra("barcode", res.getString(3));
            intent.putExtra("qun", res.getString(4));
            intent.putExtra("date", res.getString(5));

            startActivity(intent);
            finish();
        }
    }

    public void deleteFromDB(int id) {


        int res = db.DeleteInventory(Integer.toString(id));

        if (res > 0) {

            Toast.makeText(this, "تم مسح الصنف", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, QuickAdd.class);
            startActivity(intent);
            finish();

        } else {

            Toast.makeText(this, "حدث خطأ ما الرجاء إعادة المحاولة", Toast.LENGTH_LONG).show();

        }
    }

    public void openCam(View view){

        Activity activity = this;

        IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("ألتقط الباركود");
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult Result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);



        if(Result != null){

            if(Result.getContents() == null){

                Toast.makeText(this, "تم التراجع", Toast.LENGTH_SHORT).show();

            } else {

                barcode.setText(Result.getContents());
                CheckType();
            }

        } else {
            super.onActivityResult(requestCode , resultCode , data);
        }

    }

    public void CreateDilog(){

        dialog.setContentView(R.layout.inventory_popup);

        TextView add =  dialog.findViewById(R.id.add);
        TextView equnt = dialog.findViewById(R.id.edit_qun);
        TextView edit = dialog.findViewById(R.id.edit);
        TextView no = dialog.findViewById(R.id.no);

        if(!barcode.getText().toString().isEmpty()){


            Cursor result = db.getDataProduct(barcode.getText().toString());
            result.moveToFirst();

            if(Integer.parseInt(result.getString(5)) == 6){

                equnt.setVisibility(View.GONE);

            }else{

                add.setVisibility(View.GONE);
            }
        }


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YES_Dilog();
                dialog.dismiss();

            }
        });

        equnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EDIT_QUNTITY_Dilog();
                dialog.dismiss();

            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EDIT_Dilog();
                dialog.dismiss();

            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NO_Dilog();
                dialog.dismiss();
            }
        });

    }

    private void YES_Dilog(){

        Cursor result = db.getDataProduct(barcode.getText().toString());

        result.moveToFirst();

        Intent goTOAdd;


        if(Integer.parseInt(result.getString(5)) == 6) {

            goTOAdd  = new Intent(this, UpdateProductHaveDate.class);
            goTOAdd.putExtra("activity", "2");
            goTOAdd.putExtra("addQuntAndDate", "yes");

        }else{

            goTOAdd = new Intent(QuickAdd.this, InventoryAddData.class);
        }


        goTOAdd.putExtra("Barcode", barcode.getText().toString());

        barcode.setText("");

        startActivity(goTOAdd);
        finish();

    }

    private void EDIT_QUNTITY_Dilog(){

        gotoUpdate(barcode.getText().toString(), 1);
    }

    private void EDIT_Dilog(){

        gotoUpdate(barcode.getText().toString(), 0);
    }

    private void NO_Dilog(){

        barcode.setText("");
    }


    public void gotoUpdate(String barcode, int editType){




        InfinityDB db = new InfinityDB(this);

        Cursor bar = db.GetBarcodeByProductID(SearchUsePRODUCT_ID);
        bar.moveToFirst();

        Cursor res = db.getBarcods(bar.getString(0));
        Intent intent;

        res.moveToFirst();

        if(res.getString(5).compareTo("") != 0){

            intent = new Intent(this, UpdateProductHaveDate.class);
            intent.putExtra("Barcode", barcode);
            intent.putExtra("activity", "2");
            startActivity(intent);
            this.finish();

        } else if(res.getCount() == 1){

            res.moveToFirst();
            intent = new Intent(this, UpdateDate.class);

            if(editType == 1){

                intent.putExtra("updateQuntity", 0);
            }


            intent.putExtra("activity", "2");
            intent.putExtra("id", res.getString(0));
            intent.putExtra("Product_ID_PK", res.getString(1));
            intent.putExtra("ProductName", res.getString(2));
            intent.putExtra("barcode", res.getString(3));
            intent.putExtra("qun", res.getString(4));
            intent.putExtra("date", res.getString(5));

        }else{

            DataRepeted.dataID.clear();

            Cursor res2 = db.getBarcods(barcode);

            while (res2.moveToNext()){

                DataRepeted.dataID.add(res2.getString(0));
            }

            intent = new Intent(this, DataRepeted.class);

            if(editType == 1){

                intent.putExtra("updateQuntity", 0);
            }

        }

        startActivity(intent);
        finish();

    }


    public void play(){

        if(error == null){

            error = MediaPlayer.create(this, R.raw.error);
            error.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    stop();
                }
            });
        }

        error.start();
    }


    public void stop(){

        if(error != null){
            error.release();
            error = null;

        }
    }



}
