package com.example.invinityapp.inventoryitems;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;

import java.util.ArrayList;

public class ViewAllDataInventory extends AppCompatActivity {


    private InfinityDB db;
    private SwipeMenuListView dataList;
    private ArrayList<ProductData> productData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_data_inventory);

        db = new InfinityDB(this);

        dataList = findViewById(R.id.dataList);
        createMenu("");
    }



    public void createMenu(String input){

        Cursor res;

        if(input.isEmpty()){

         res = db.selectData();

        }else{

            res = db.SearchProduct(input);
        }

        productData = new ArrayList<>();
        IventoryAdabter productAdabter =new IventoryAdabter(this, productData);

        if (res.getCount() != 0) {
            int i = 1;
            while (res.moveToNext()) {

                productAdabter.add(new ProductData(i, Integer.parseInt(res.getString(0)),res.getString(2), res.getString(3), res.getString(6),res.getString(7),res.getString(8) ));
                i++;
            }


            dataList.setAdapter(productAdabter);

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

        dataList.setMenuCreator(creator);

        dataList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        update(productData.get(position).Product_id);
                        break;
                    case 1:

                        AlertDialog dialog = new AlertDialog.Builder(ViewAllDataInventory.this)
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

        }else {


            intent = new Intent(this, UpdateDate.class);

            intent.putExtra("activity", "1");
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






    @SuppressLint("WrongConstant")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem search = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) search.getActionView();

        searchView.setQueryHint("بحث");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                createMenu(newText);
                return false;
            }
        });

        search.setVisible(true);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                Intent intent = new Intent(ViewAllDataInventory.this, inventroyscan.class);
                startActivity(intent);
                ViewAllDataInventory.this.finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
