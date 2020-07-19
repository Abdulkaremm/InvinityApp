package com.example.invinityapp.goods;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class ViewGoodsActivity extends AppCompatActivity {


    InfinityDB db = new InfinityDB(this);
    SwipeMenuListView swiplist;


    String SupID;
    TextView supplier;
    private ArrayList<ProductsModel> productsModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_goods);
        supplier = findViewById(R.id.supplier);
        swiplist = findViewById(R.id.ViewGoodsList);
        Intent intent = getIntent();

        SupID = intent.getStringExtra("ID");
        supplier.setText(intent.getStringExtra("NAME"));


        GoodsList();


        /// create swiplist buttons
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                // create "open" item

                SwipeMenuItem delete = new SwipeMenuItem(getApplicationContext());

                // set item background

                delete.setBackground(R.color.colorDanger);
                // set item width
                delete.setWidth(100);
                // set item title
                delete.setIcon(R.drawable.ic_delete);

                // add to menu
                menu.addMenuItem(delete);

                // create "open" item

                SwipeMenuItem Edit = new SwipeMenuItem(getApplicationContext());

                // set item background

                Edit.setBackground(R.color.colorSuccess);
                // set item width
                Edit.setWidth(100);
                // set item title
                Edit.setIcon(R.drawable.ic_update);

                // add to menu
                menu.addMenuItem(Edit);




            }
        };
        swiplist.setMenuCreator(creator);


    }


    private void GoodsList(){


        Cursor res;
        res = db.LastAddedGoods(SupID);

        productsModels = new ArrayList<>();

        GoodsListAdaptare productAdabter =new GoodsListAdaptare(this, productsModels);

        if(res.getCount() > 0){
            while (res.moveToNext()){

                Cursor res1 = db.LastGoodUnit(res.getString(4),res.getString(5));
                if(res1.getCount() > 0){
                    res1.moveToFirst();

                }
                productAdabter.add(new ProductsModel(res.getString(0), res.getString(3),res.getString(1), res.getString(2), res1.getString(0), res.getString(0)));
            }


        }

        swiplist.setAdapter(productAdabter);


        swiplist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 1:
                        Intent intent = new Intent(ViewGoodsActivity.this,UpdateProductActivity.class);
                        intent.putExtra("SupplierID",SupID);
                        intent.putExtra("ProductID",productsModels.get(position).id);
                        intent.putExtra("NAME",supplier.getText().toString());
                        intent.putExtra("ACTIVITY","View");
                        startActivity(intent);
                        finish();
                        break;

                    case 0:

                        AlertDialog dialog = new AlertDialog.Builder(ViewGoodsActivity.this)
                                .setTitle("مسح صنف !")
                                .setMessage("هل انت متأكد من عملية الحدف ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(db.DeleteAddedProduct(productsModels.get(position).id)) {
                                            Toast.makeText(ViewGoodsActivity.this, "تم الحدف", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewGoodsActivity.this, ViewGoodsActivity.class);
                                            intent.putExtra("ID",SupID);
                                            intent.putExtra("NAME",supplier.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }else
                                            Toast.makeText(ViewGoodsActivity.this,"حدث خطأ الرجاء اعادة المحاولة",Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("إلغاء", null)
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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                bacToMain(null);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bacToMain(View view){

        Intent intent = new Intent(this, receiving_goods_main.class);
        startActivity(intent);
        finish();
    }
}
