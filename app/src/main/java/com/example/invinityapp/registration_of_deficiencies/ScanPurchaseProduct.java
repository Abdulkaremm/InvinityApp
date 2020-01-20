package com.example.invinityapp.registration_of_deficiencies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.example.invinityapp.inventoryitems.IventoryAdabter;
import com.example.invinityapp.inventoryitems.ProductData;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class ScanPurchaseProduct extends AppCompatActivity {

    InfinityDB db;
    private SwipeMenuListView dataLists;
    private ArrayList<ProductData> productData;
    private EditText barcode;
    private TextView MSG;
    MediaPlayer error;
    private Dialog dialog;
    private String SearchUsePRODUCT_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);


        dialog = new Dialog(this);

        db = new InfinityDB(this);


        dataLists = findViewById(R.id.listData);

        MSG = findViewById(R.id.msg);

        barcode  =  findViewById(R.id.barcode);
        barcode.requestFocus();

        barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {



                if((keyCode == 66 || keyCode ==301 || keyCode ==302 || keyCode ==303) && barcode.getText().toString().compareTo("") != 0){

                    addProduct();

                }

                return false;
            }
        });

        createMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem openCam = menu.findItem(R.id.openCam);
        openCam.setVisible(true);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                Intent intent = new Intent(this, RegistrationOfDeficiencies.class);
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
                addProduct();
            }

        } else {
            super.onActivityResult(requestCode , resultCode , data);
        }

    }





    private void addProduct(){

        Cursor result = db.getDataProduct(barcode.getText().toString());


        if (result.getCount() == 0) {

            MSG.setText("الصنف غير موجود الرجاء التأكد من الصنف");
            barcode.setText("");
            play();

        }else{

            result.moveToFirst();
            int x = db.checkPurchaseProductByID(result.getString(0));

            if (x > 0) {

                SearchUsePRODUCT_ID = result.getString(0);

                CreateDilog();
                dialog.show();

            } else {

                Intent goTOAdd = new Intent(ScanPurchaseProduct.this, AddProductToPurchase.class);;

                goTOAdd.putExtra("Barcode", barcode.getText().toString());

                barcode.setText("");
                startActivity(goTOAdd);
                finish();
            }
        }
    }


    public void CreateDilog(){

        dialog.setContentView(R.layout.inventory_popup);

        TextView add =  dialog.findViewById(R.id.add);
        TextView equnt = dialog.findViewById(R.id.edit_qun);
        TextView edit = dialog.findViewById(R.id.edit);
        TextView no = dialog.findViewById(R.id.no);

        add.setVisibility(View.GONE);

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

        Cursor bar = db.GetBarcodeByPurchaseProductID(SearchUsePRODUCT_ID);
        bar.moveToFirst();

        Cursor res = db.SelectPurchasePriductByBarcode(bar.getString(0));
        res.moveToFirst();

        bar.close();

        Intent intent = new Intent(this, UpdatePurchaseProduct.class);

        if(editType == 1){

            intent.putExtra("updateQuntity", 0);
        }


        intent.putExtra("activity", "2");
        intent.putExtra("id", res.getString(0));
        intent.putExtra("Product_ID_PK", res.getString(1));
        intent.putExtra("ProductName", res.getString(2));
        intent.putExtra("barcode", res.getString(3));
        intent.putExtra("qun", res.getString(4));
        intent.putExtra("discription", res.getString(5));

        startActivity(intent);
        finish();

    }


    public void createMenu(){

        Cursor res = db.SelectPurchaseProducts();
        productData = new ArrayList<>();
        IventoryAdabter productAdabter = new IventoryAdabter(this, productData);

        if (res.getCount() != 0) {
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

                        AlertDialog dialog = new AlertDialog.Builder(ScanPurchaseProduct.this)
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


    public void update(int id) {


        Cursor res = db.getPurchaseProductById(Integer.toString(id));

        Intent intent;
        res.moveToFirst();

        intent =  new Intent(this, UpdatePurchaseProduct.class);
        intent.putExtra("activity", "2");
        intent.putExtra("id", res.getString(0));
        intent.putExtra("Product_ID_PK", res.getString(1));
        intent.putExtra("ProductName", res.getString(2));
        intent.putExtra("barcode", res.getString(3));
        intent.putExtra("qun", res.getString(4));
        intent.putExtra("discription", res.getString(5));

        startActivity(intent);
        ScanPurchaseProduct.this.finish();
    }

    public void deleteFromDB(int id) {


        int res = db.DeletePurchaseProductByID(Integer.toString(id));

        if (res > 0) {

            Toast.makeText(this, "تم مسح الصنف", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, ScanPurchaseProduct.class);
            startActivity(intent);
            ScanPurchaseProduct.this.finish();

        } else {

            Toast.makeText(this, "حدث خطأ ما الرجاء إعادة المحاولة", Toast.LENGTH_LONG).show();

        }
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
