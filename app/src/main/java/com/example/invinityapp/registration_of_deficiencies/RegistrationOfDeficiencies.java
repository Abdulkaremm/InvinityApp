package com.example.invinityapp.registration_of_deficiencies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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

import java.util.ArrayList;

public class RegistrationOfDeficiencies extends AppCompatActivity {


    private  InfinityDB db;
    private  TextView DNF;
    private SwipeMenuListView dataLists;
    private ArrayList<ProductData> productData;
    public boolean ifHasData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_of_deficiencies);


        db = new InfinityDB(this);

        DNF =  findViewById(R.id.dataNotFound);
        dataLists = findViewById(R.id.listData);


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

}
