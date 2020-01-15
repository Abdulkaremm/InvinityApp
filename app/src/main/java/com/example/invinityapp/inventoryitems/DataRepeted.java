package com.example.invinityapp.inventoryitems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;

import java.util.ArrayList;

public class DataRepeted extends AppCompatActivity {

    public static ArrayList<String> dataID = new ArrayList<>();

    public SwipeMenuListView dataLists;
    private ArrayList<ProductData> productData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_repeted);

        dataLists = (SwipeMenuListView) findViewById(R.id.listData);

        if(dataID.size() == 0){

            Intent intent = new Intent(DataRepeted.this, QuickAdd.class);
            startActivity(intent);
            DataRepeted.this.finish();
        }

        productData = new ArrayList<>();
        IventoryAdabter productAdabter =new IventoryAdabter(this, productData);

        for(int i = 0; i < dataID.size(); i++){

            Cursor res = inventroyscan.mydata.selectDataByID(dataID.get(i));
            res.moveToFirst();

            productAdabter.add(new ProductData(i+1, Integer.parseInt(res.getString(0)),res.getString(2), res.getString(3), res.getString(6),res.getString(7),res.getString(8) ));

        }

        dataLists.setAdapter(productAdabter);


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

        //final Activity activity = this;

        dataLists.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final  int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        update(dataID.get(position));
                        break;
                    case 1:

                        AlertDialog dialog = new AlertDialog.Builder(DataRepeted.this)
                                .setTitle("مسح صنف !")
                                .setMessage("هل انت متأكد من عملية الحدف ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                       deleteFromDB(productData.get(position).Product_id, position);

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

        MenuItem item1 = menu.findItem(R.id.sync);

        item1.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                backToScan(null);

                return true;

            case R.id.openCam:

                openCam();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void update (String id){

        Intent intent = new Intent(this, UpdateDate.class);
        Intent getData = getIntent();

        InfinityDB db = new InfinityDB(this);

        Cursor res = db.getById(id);

        res.moveToFirst();

        if(getData.hasExtra("updateQuntity")){

            intent.putExtra("updateQuntity", 0);
        }

        intent.putExtra("activity", "3");
        intent.putExtra("id", res.getString(0));
        intent.putExtra("Product_ID_PK", res.getString(1));
        intent.putExtra("ProductName", res.getString(2));
        intent.putExtra("barcode", res.getString(3));
        intent.putExtra("qun", res.getString(4));
        intent.putExtra("date", res.getString(5));

        startActivity(intent);
        finish();
    }

    public void deleteFromDB(int id, int position) {

        InfinityDB db = new InfinityDB(this);
        int res = db.DeleteInventory(Integer.toString(id));
        dataID.remove(position);
        if (res > 0) {

            Toast.makeText(this, "تم مسح الصنف", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, DataRepeted.class);
            startActivity(intent);
            finish();

        } else {

            Toast.makeText(this, "حدث خطأ ما الرجاء إعادة المحاولة", Toast.LENGTH_LONG).show();

        }
    }


    public void backToScan(View view){

        Intent intent = new Intent(this, QuickAdd.class);
        startActivity(intent);
        finish();
    }

    public void openCam(){

        Intent intent = new Intent(this, InventoryAddData.class);
        startActivity(intent);
        finish();
    }
}
