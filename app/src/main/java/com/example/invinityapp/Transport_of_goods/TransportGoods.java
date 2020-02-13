package com.example.invinityapp.Transport_of_goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.ExportDocumntsAndProducts;
import com.example.invinityapp.MainActivity;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;
import java.util.ArrayList;
public class TransportGoods extends AppCompatActivity {



    private SwipeMenuListView ListsOfDocument;
    private TextView DNF;
    private InfinityDB db;
    private ArrayList<DocumentData> DataDocument;
    private DocumenAdabter dataAdabter;
    public static ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport_goods);

        db = new InfinityDB(this);

        progressDialog = new ProgressDialog(TransportGoods.this);
        progressDialog.setTitle("مزامنة البيانات");
        progressDialog.setMessage("يتم العمل على مزامنة البيانات...");
        progressDialog.setCanceledOnTouchOutside(false);

        ListsOfDocument = findViewById(R.id.listData);
        DNF =  findViewById(R.id.dataNotFound);

        Cursor res = db.SelectAllDocument();

        if(res.getCount() == 0){

            DNF.setText("لا توجد مستندات لعرضها");

        }else{

            DataDocument = new ArrayList<>();

           dataAdabter = new DocumenAdabter(this, DataDocument);

            while (res.moveToNext()){

                dataAdabter.add(new DocumentData(res.getString(0), res.getString(4), res.getString(6), res.getString(8), res.getString(10), res.getString(3)));
            }

            ListsOfDocument.setAdapter(dataAdabter);

        }


        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

               SwipeMenuItem view = new SwipeMenuItem(getApplicationContext());

                // set item background

                view.setBackground(R.color.colorMain);
                // set item width
                view.setWidth(170);
                // set item title
                view.setIcon(R.drawable.ic_open_document);

                // add to menu
                menu.addMenuItem(view);

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

        ListsOfDocument.setMenuCreator(creator);

        ListsOfDocument.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:

                        ScanProduct.DocumentID_PK = Integer.parseInt(DataDocument.get(position).DocumentID_PK);
                        Intent intent = new Intent(TransportGoods.this, ScanProduct.class);
                        startActivity(intent);
                        TransportGoods.this.finish();

                        break;

                    case 1:

                        Intent intent1 = new Intent(TransportGoods.this, CreateDocument.class);

                        intent1.putExtra("id", DataDocument.get(position).DocumentID_PK);
                        startActivity(intent1);
                        TransportGoods.this.finish();

                       break;

                    case 2:

                        AlertDialog dialog = new AlertDialog.Builder(TransportGoods.this)
                                .setTitle("مسح مستند !")
                                .setMessage("هل انت متأكد من المستند مع كل اصنافه ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        db.DeleteDocument(DataDocument.get(position).DocumentID_PK);
                                        db.DeleteDocumentProduct(DataDocument.get(position).DocumentID_PK);
                                       Intent intent = new Intent(TransportGoods.this, TransportGoods.class);
                                       startActivity(intent);
                                       TransportGoods.this.finish();
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

                Intent intent = new Intent(TransportGoods.this, MainActivity.class);
                startActivity(intent);
                TransportGoods.this.finish();

                return true;

            case R.id.addnew:

                Cursor resData = db.SelectAllBranchs();

                if(resData.getCount() == 0){

                    Toast.makeText(TransportGoods.this, "الرجاء مزامنة بيانات المنضومة", Toast.LENGTH_SHORT).show();

                }else {

                    Intent intent2 = new Intent(TransportGoods.this, CreateDocument.class);
                    startActivity(intent2);
                    TransportGoods.this.finish();

                }


                return true;

            case R.id.sync:

                if(checkConnect()){


                    if(true){//ifHasData){

                        AlertDialog dialog = new AlertDialog.Builder(TransportGoods.this)
                                .setTitle("مزامنة الاصناف !")
                                .setMessage("هل انت متأكد من عملية المزامنة ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.show();
                                       new ExportDocumntsAndProducts().execute(TransportGoods.this);

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



}
