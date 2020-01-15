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
import com.example.invinityapp.MainActivity;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class receiving_goods_main extends AppCompatActivity {

    SwipeMenuListView swiplist;
    TextView title;
    InfinityDB db = new InfinityDB(this);
    ArrayList<String>   names = new ArrayList<>(),
            dates = new ArrayList<>(),
            IDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_goods_main);
        title = findViewById(R.id.Title);
        swiplist = findViewById(R.id.listData);

        IfOpenReceiving();



        /// create swiplist buttons
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item

                SwipeMenuItem Add = new SwipeMenuItem(getApplicationContext());

                // set item background

                Add.setBackground(R.color.colorMain);
                // set item width
                Add.setWidth(100);
                // set item title
                Add.setIcon(R.drawable.ic_add_new);

                // add to menu
                menu.addMenuItem(Add);



                // create "open" item

                SwipeMenuItem view = new SwipeMenuItem(getApplicationContext());

                // set item background

                view.setBackground(R.color.colorSuccess);
                // set item width
                view.setWidth(100);
                // set item title
                view.setIcon(R.drawable.ic_view);

                // add to menu
                menu.addMenuItem(view);

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



            }
        };
        swiplist.setMenuCreator(creator);


    }



    private void IfOpenReceiving(){


        Cursor res = db.IfOpenReceiving();



        OpenReceivingAdapter adapter;


        if(res.getCount() > 0){

            while (res.moveToNext()){
                IDs.add(res.getString(0));
                names.add(res.getString(1));
                dates.add(res.getString(2));
            }

            adapter = new OpenReceivingAdapter(this,names,dates);

            title.setText("جلسات الاستلام المفتوحة");
            swiplist.setAdapter(adapter);


            swiplist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            Intent intent = new Intent(receiving_goods_main.this,ReceiveGoods.class);
                            intent.putExtra("ID",IDs.get(position));
                            intent.putExtra("NAME",names.get(position));
                            startActivity(intent);
                            finish();
                            break;
                        case 1:
                            Intent intent1 = new Intent(receiving_goods_main.this,ViewGoodsActivity.class);
                            intent1.putExtra("ID",IDs.get(position));
                            intent1.putExtra("NAME",names.get(position));
                            startActivity(intent1);
                            finish();
                            break;

                        case 2:

                            AlertDialog dialog = new AlertDialog.Builder(receiving_goods_main.this)
                                    .setTitle("مسح صنف !")
                                    .setMessage("هل انت متأكد من عملية الحدف ؟")
                                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(db.DeleteOpenReceivie(IDs.get(position))) {
                                                Toast.makeText(receiving_goods_main.this, "تم حدف الجلسة", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(receiving_goods_main.this,receiving_goods_main.class);
                                                startActivity(intent);
                                                finish();
                                            }else
                                                Toast.makeText(receiving_goods_main.this,"حدث خطأ الرجاء اعادة المحاولة",Toast.LENGTH_SHORT).show();
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



        }else{

            title.setText("لم يتم استلام بضائع بعد!");
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem addNew = menu.findItem(R.id.addnew);
        addNew.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                bacToMain(null);

                return true;

            case R.id.addnew:

                Intent intent = new Intent(this,AddNewSup.class);
                startActivity(intent);
                finish();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bacToMain(View view){

        MainActivity.This.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}
