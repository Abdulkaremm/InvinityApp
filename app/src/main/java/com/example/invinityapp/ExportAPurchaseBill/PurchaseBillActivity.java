package com.example.invinityapp.ExportAPurchaseBill;


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
        import com.example.invinityapp.MainActivity;
        import com.example.invinityapp.R;
        import com.example.invinityapp.inventoryitems.InfinityDB;

        import java.util.ArrayList;


public class PurchaseBillActivity extends AppCompatActivity {

    SwipeMenuListView BillsList;
    TextView title;
    ArrayList<BillsModel> BillsData;
    InfinityDB db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_bill);


        title = findViewById(R.id.Title);
        BillsList = findViewById(R.id.listData);


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
        BillsList.setMenuCreator(creator);

        GetAllBilss();
    }


    public void GetAllBilss(){

         db = new InfinityDB(this);

        BillsData = new ArrayList<>();

        BillsAdapatare billsAdapatare = new BillsAdapatare(this,BillsData);

        Cursor res = db.GetAllBills();

        if(res.getCount() > 0){

            while (res.moveToNext()){

              billsAdapatare.add(new BillsModel(res.getString(0),res.getString(2),res.getString(3)));

            }
            title.setText("قائمة بالفواتير");

            BillsList.setAdapter(billsAdapatare);


            BillsList.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                    switch (index) {
                        case 0:
                            Intent intent = new Intent(PurchaseBillActivity.this, CreateNewBillActivity.class);
                            intent.putExtra("ID",BillsData.get(position).id);
                            intent.putExtra("NAME",BillsData.get(position).name);
                            startActivity(intent);
                            finish();
                            break;
                        case 1:
                            Intent intent1 = new Intent(PurchaseBillActivity.this, ViewBillProducts.class);
                            intent1.putExtra("ID",BillsData.get(position).id);
                            intent1.putExtra("NAME",BillsData.get(position).name);
                            startActivity(intent1);
                            finish();
                            break;

                        case 2:

                            AlertDialog dialog = new AlertDialog.Builder(PurchaseBillActivity.this)
                                    .setTitle("حذف الفاتورة !")
                                    .setMessage("هل انت متأكد من عملية الحدف ؟")
                                    .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if(db.DeletePurchaseBill(BillsData.get(position).id)) {
                                                Toast.makeText(PurchaseBillActivity.this, "تم حدف الفاتورة", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(PurchaseBillActivity.this, PurchaseBillActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else
                                                Toast.makeText(PurchaseBillActivity.this,"حدث خطأ الرجاء اعادة المحاولة",Toast.LENGTH_SHORT).show();
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



        }else{title.setText("لا توجد فواتير لعرضها");}
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
    public boolean onOptionsItemSelected( MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                bacToMain(null);

                return true;

            case R.id.addnew:

                Intent intent = new Intent(this,CreateNewBillActivity.class);
                startActivity(intent);
                finish();

                return true;

            case R.id.sync:

                //Cursor res = db.IfOpenReceiving();
                //if(res.getCount() > 0){

                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("مزامنة  الفواتير !")
                            .setMessage("هل انت متأكد من عملية المزامنة ؟")
                            .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(PurchaseBillActivity.this,"لم يتم إنشاء فواتير لتصديرها",Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("إلغاء", null)
                            .show();

                //}else



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
