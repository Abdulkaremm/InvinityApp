package com.example.invinityapp.ExportAPurchaseBill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.goods.GoodsListAdaptare;
import com.example.invinityapp.goods.ProductsModel;
import com.example.invinityapp.goods.UpdateProductActivity;
import com.example.invinityapp.goods.ViewGoodsActivity;
import com.example.invinityapp.goods.receiving_goods_main;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class ViewBillProducts extends AppCompatActivity {

    InfinityDB db = new InfinityDB(this);
    SwipeMenuListView ProductsLis;


    String PurchaseID;
    TextView client,title;
    private ArrayList<ProductsModel> productsModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill_products);

        title = findViewById(R.id.title);
        client = findViewById(R.id.client);
        ProductsLis = findViewById(R.id.ViewProductsList);
        Intent intent = getIntent();

        PurchaseID = intent.getStringExtra("ID");
        client.setText(intent.getStringExtra("NAME"));






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
        ProductsLis.setMenuCreator(creator);

        ProductsList();

    }


    private void ProductsList(){

        Cursor result;

        result = db.SelectBillProducts(PurchaseID);

        productsModels = new ArrayList<>();

        GoodsListAdaptare productAdabter =new GoodsListAdaptare(this, productsModels);

        if(result.getCount() > 0){
            title.setText("قائمة الاصناف المفاضة للفاتورة");
            while (result.moveToNext()){

                Cursor res1 = db.SelectProductUOM(result.getString(4));
                res1.moveToFirst();
                productAdabter.add(new ProductsModel(result.getString(0), result.getString(3),result.getString(1), result.getString(2), res1.getString(0), ""));
            }


        }else
            title.setText("لم يتم اضافة اصناف للفاتورة !");

        ProductsLis.setAdapter(productAdabter);

        ProductsLis.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 1:
                        Intent intent = new Intent(ViewBillProducts.this, EditProduct.class);
                        intent.putExtra("ID",PurchaseID);
                        intent.putExtra("ProductID",productsModels.get(position).id);
                        intent.putExtra("ACTIVITY","View");
                        intent.putExtra("NAME",client.getText().toString());
                        startActivity(intent);
                        finish();
                        break;

                    case 0:

                        AlertDialog dialog = new AlertDialog.Builder(ViewBillProducts.this)
                                .setTitle("مسح صنف !")
                                .setMessage("هل انت متأكد من عملية الحدف ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(db.DeleteBillProduct(productsModels.get(position).id)) {
                                            Toast.makeText(ViewBillProducts.this, "تم الحدف", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ViewBillProducts.this, ViewBillProducts.class);
                                            intent.putExtra("ID",PurchaseID);
                                            intent.putExtra("NAME",client.getText().toString());
                                            startActivity(intent);
                                            finish();
                                        }else
                                            Toast.makeText(ViewBillProducts.this,"حدث خطأ الرجاء اعادة المحاولة",Toast.LENGTH_SHORT).show();
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

        Intent intent = new Intent(this, PurchaseBillActivity.class);
        startActivity(intent);
        finish();
    }
}
