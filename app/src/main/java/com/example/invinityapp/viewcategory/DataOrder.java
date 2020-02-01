package com.example.invinityapp.viewcategory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.invinityapp.MainActivity;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


public class DataOrder extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<String>> {

    public static TextView storage;
    boolean  isconnect;
    public static boolean isthere;
    int unitName = 1, pages = 0;
    static  int unites = 0;
    static String Code;
    TextView
    name,
    IfStorage,
    dep,
    unit,
    price1,
    price2,
    price3,
    price4,
    dataprice1,
    dataprice2,
    dataprice3,
    dataprice4,
    Payment;

    ProgressBar bar;
    Button next, prev;
    InfinityDB setting;
    ArrayList<String> list =new ArrayList<>();
    ArrayList<String> info = new ArrayList<>();
    AlertDialog dialog;
    ScrollView scrollView;
    EditText Barcode;
    LinearLayout title;

    public static String api_ip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_order);


        dialog = new AlertDialog.Builder(this)
                .setTitle("هذا الصنف غير موجود!")
                .setMessage("هل تود البحث عن الصنف عبر الشبكة ؟")
                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        UseLoader();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Barcode.setEnabled(true);
                        Barcode.setText("");
                    }
                })
                .create();

        list.clear();
        bar =  findViewById(R.id.progressBar);

        Payment = findViewById(R.id.Payment);
        title = findViewById(R.id.title);
        scrollView = findViewById(R.id.scrollData);
        Barcode = findViewById(R.id.Barcode);

        next      =  findViewById(R.id.next);
        prev      =  findViewById(R.id.prev);
        unit      =  findViewById(R.id.dataUnit);
        name      =  findViewById(R.id.dataName);
        IfStorage = findViewById(R.id.storage);
        storage   =  findViewById(R.id.dataStorage);
        dep       =  findViewById(R.id.dataDep);




        price1  =  findViewById(R.id.price1);
        price2  =  findViewById(R.id.price2);
        price3  =  findViewById(R.id.price3);
        price4  =  findViewById(R.id.price4);

        setting = new InfinityDB(this);

        Cursor res = setting.ExbordSetting();
        res.moveToFirst();

        price1.setText(res.getString(2));
        price2.setText(res.getString(3));
        price3.setText(res.getString(4));
        price4.setText(res.getString(5));


        dataprice1  =  findViewById(R.id.dataPrice1);
        dataprice2  =  findViewById(R.id.dataPrice2);
        dataprice3  =  findViewById(R.id.dataPrice3);
        dataprice4  =  findViewById(R.id.dataPrice4);




        Barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == 66 || keyCode == 301 || keyCode ==302 || keyCode ==303) && !Barcode.getText().toString().isEmpty()){
                    Code = Barcode.getText().toString();
                    CheckBarcode(Code);
                }



                return false;
            }
        });


    } // onCreate End


         /// using loader to get data
    private void UseLoader(){


        if(isconnect){


            if(isthere){

                Toast.makeText(this,"الرجاء الانتظار",Toast.LENGTH_SHORT).show();
                InfinityDB db = new InfinityDB(this);

                Cursor ip = db.ExbordSetting();
                ip.moveToFirst();
                api_ip = ip.getString(7);
                LoaderManager.getInstance(this).initLoader(0,null,this).forceLoad();
                bar.setVisibility(View.VISIBLE);


            }else
                Toast.makeText(this,"هذا الصنف غير موجود الرجاء التأكد من الصنف",Toast.LENGTH_LONG).show();


        }else{
            Intent intent = new Intent(this, NetworkState.class);
            startActivity(intent);
            finish();
        }


    }


    public void paymentDet(View v){

        Intent intent = new Intent(this,ProductPaymentDetails.class);
        intent.putExtra("ProductName",name.getText().toString());
        intent.putExtra("ProductID",info.get(2));
        startActivity(intent);
    }


               /////// option menu start here

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);
        MenuItem camera = menu.findItem(R.id.openCam);
        MenuItem bReader = menu.findItem(R.id.QR);

        camera.setVisible(true);
        bReader.setVisible(true);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                bacToMain(null);

                return true;

            case R.id.QR:

                title.setVisibility(View.INVISIBLE);
                scrollView.setVisibility(View.GONE);
                Barcode.setText("");
                Barcode.setEnabled(true);

                return true;

            case R.id.openCam:

                OPenCAM();

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



   public void nextUnit(View view){
        pages += 5;
        unitName += 1;
        unit.setText(list.get(pages));
        prev.setVisibility(View.VISIBLE);

        dataprice1.setText(list.get(pages + 1));
        dataprice2.setText(list.get(pages + 2));
        dataprice3.setText(list.get(pages + 3));
        dataprice4.setText(list.get(pages + 4));


        if (unitName == unites){

            next.setVisibility(View.INVISIBLE);
        }
    }

    public void prevUnit(View view){

        pages -= 5;
        unitName -=1;
        unit.setText(list.get(pages));
        next.setVisibility(View.VISIBLE);

        dataprice1.setText(list.get(pages + 1));
        dataprice2.setText(list.get(pages + 2));
        dataprice3.setText(list.get(pages + 3));
        dataprice4.setText(list.get(pages + 4));

        if (unitName == 1){

            prev.setVisibility(View.INVISIBLE);
        }

    }






    ///// check if barcode exist or not | if exist will get the data

    private void CheckBarcode(String barcode){

        InfinityDB db = new InfinityDB(this);

        Barcode.setEnabled(false);
        info.clear();
        list.clear();
        unitName = 1;
        pages = 0;
        prev.setVisibility(View.INVISIBLE);

        int UOMID = 0;

        Cursor res =  db.isExistBar(barcode);

        if(res.getCount() > 0){ // if-1
            while(res.moveToNext()){
                UOMID = Integer.parseInt(res.getString(0));
                info.add(res.getString(1));
                info.add(res.getString(2));
                info.add(res.getString(0));
            }

            res.close();
            res = db.GetProdectBybar(UOMID);

            if(res.getCount() > 0){ // if-1-1

                while(res.moveToNext()){
                    list.add(res.getString(0));
                    list.add(res.getString(1));
                    list.add(res.getString(2));
                    list.add(res.getString(3));
                    list.add(res.getString(4));
                }
            } // end if if-1-1

            res.close();

            isconnect = checkConnect();
            if(isconnect) {
                storage.setText("يتم جلب المخزون...");
                Cursor ip = db.ExbordSetting();
                ip.moveToFirst();
                new GetStorage().execute(Code, ip.getString(7));
            }else
                storage.setText("غير متوفر");


            name.setText(info.get(0));
            dep.setText(info.get(1));


            unites = list.size() / 5;
            unit.setText(list.get(0));
            dataprice1.setText(list.get(1));
            dataprice2.setText(list.get(2));
            dataprice3.setText(list.get(3));
            dataprice4.setText(list.get(4));

            if(unites  != 1){

                next.setVisibility(View.VISIBLE);
            }

            title.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);

        }else {
            title.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.GONE);
            isconnect = checkConnect();
            Cursor ip = db.ExbordSetting();
            ip.moveToFirst();
            if (isconnect)
             new BarcodeChecker().execute(Code, ip.getString(7));
            dialog.show();

        }  // end of if-1

    } /// end Of CheckBarcode method



            /// loader start here
    @NonNull
    @Override
    public Loader<List<String>> onCreateLoader(int id, @Nullable Bundle args) {
        return new GetDataLoader(DataOrder.this);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {

        bar.setVisibility(View.GONE);
        list.clear();

        list.addAll(data);

        name.setText(GetDataLoader.name);
        storage.setText(GetDataLoader.storage);
        dep.setText(GetDataLoader.dep);




        DataOrder.unites = list.size() / 5;

        unit.setText(list.get(0));
        dataprice1.setText(list.get(1));
        dataprice2.setText(list.get(2));
        dataprice3.setText(list.get(3));
        dataprice4.setText(list.get(4));

        if(DataOrder.unites  != 1){

            next.setVisibility(View.VISIBLE);
        }
        Payment.setVisibility(View.INVISIBLE);
        title.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

       list.clear();
    }

    public void bacToMain(View view){

        MainActivity.This.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }




    //// use camera for scan barcodes



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult Result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);

        if(Result != null){

            if(Result.getContents() == null){

                Toast.makeText(this,"لم يتم إلتقاط باركود",Toast.LENGTH_SHORT).show();

            }else {

                Code = Result.getContents();
                Barcode.setText(Result.getContents());
                CheckBarcode(Code);

            }

        }else {
            super.onActivityResult(requestCode , resultCode , data);
        }



    }

    public void OPenCAM(){

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setPrompt("ألتقط الباركود");
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();
    }




}
