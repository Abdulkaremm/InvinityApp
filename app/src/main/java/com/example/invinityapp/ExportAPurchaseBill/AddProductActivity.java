package com.example.invinityapp.ExportAPurchaseBill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.goods.GoodsListAdaptare;
import com.example.invinityapp.goods.ProductsModel;
import com.example.invinityapp.goods.ReceiveGoods;
import com.example.invinityapp.goods.UpdateProductActivity;
import com.example.invinityapp.goods.receiving_goods_main;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AddProductActivity extends AppCompatActivity {

    EditText quantity,
            packageing,
            Barcode;
    TextView mines,Productname;
    Spinner units;
    LinearLayout container;
    AlertDialog dialog;
    String UnitID, UnitName;
    String PurchaseID,ClientName,productID;
    InfinityDB db = new InfinityDB(this);
    SwipeMenuListView swiplist;
    LinearLayout linearLayout;
    String baseunit;
    private ArrayList<ProductsModel> productsModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);


        container = findViewById(R.id.container);
        swiplist = findViewById(R.id.ProductsList);
        Barcode = findViewById(R.id.barcode);
        Productname = findViewById(R.id.ProductName);
        quantity = findViewById(R.id.quantity);
        packageing = findViewById(R.id.packageing);
        units = findViewById(R.id.UnitsSpinner);
        linearLayout = findViewById(R.id.list);
        mines = findViewById(R.id.mines);

        Intent intent = getIntent();

        PurchaseID = intent.getStringExtra("ID");
        ClientName = intent.getStringExtra("NAME");



        Barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == 66 || keyCode == 301 || keyCode == 302 || keyCode == 303) && !Barcode.getText().toString().isEmpty()){


                    final Cursor result = db.CheckIfProductExist(Barcode.getText().toString(),Integer.parseInt(PurchaseID));
                    if(result.getCount() > 0) {

                        AlertDialog dialog = new AlertDialog.Builder(AddProductActivity.this)
                                .setTitle("تمت اضافة هذا الصنف !")
                                .setMessage("تمت اضافة الصنف سابقأ هل ترغب في.. ")
                                .setPositiveButton("إضافة", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        GetByBarcode(Barcode.getText().toString());
                                    }
                                })
                                .setNegativeButton("تعديل", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (result.getCount() == 1) {

                                            result.moveToFirst();
                                            Intent intent = new Intent(AddProductActivity.this, EditProduct.class);
                                            intent.putExtra("ID", PurchaseID);
                                            intent.putExtra("ProductID", result.getString(0));
                                            intent.putExtra("ACTIVITY", "ADD");
                                            intent.putExtra("NAME",ClientName);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                })
                                .setNeutralButton("إلغاء", null)
                                .show();

                    }else
                        GetByBarcode(Barcode.getText().toString());


                }
                return false;
            }
        });

        ProductsList();


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





    private void ProductsList(){

        Cursor result;

        result = db.LastBillProducts(PurchaseID);

        productsModels = new ArrayList<>();

        GoodsListAdaptare productAdabter =new GoodsListAdaptare(this, productsModels);

        if(result.getCount() > 0){
            while (result.moveToNext()){

                Cursor res1 = db.SelectProductUOM(result.getString(4));
                res1.moveToFirst();
                productAdabter.add(new ProductsModel(result.getString(0), result.getString(3),result.getString(1), result.getString(2), res1.getString(0), ""));
            }


        }

        swiplist.setAdapter(productAdabter);

        swiplist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 1:
                        Intent intent = new Intent(AddProductActivity.this, EditProduct.class);
                        intent.putExtra("ID",PurchaseID);
                        intent.putExtra("ProductID",productsModels.get(position).id);
                        intent.putExtra("ACTIVITY","ADD");
                        startActivity(intent);
                        finish();
                        break;

                    case 0:

                        AlertDialog dialog = new AlertDialog.Builder(AddProductActivity.this)
                                .setTitle("مسح صنف !")
                                .setMessage("هل انت متأكد من عملية الحدف ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(db.DeleteBillProduct(productsModels.get(position).id)) {
                                            Toast.makeText(AddProductActivity.this, "تم الحدف", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(AddProductActivity.this, AddProductActivity.class);
                                            intent.putExtra("ID",PurchaseID);
                                            intent.putExtra("NAME",ClientName);
                                            startActivity(intent);
                                            finish();
                                        }else
                                            Toast.makeText(AddProductActivity.this,"حدث خطأ الرجاء اعادة المحاولة",Toast.LENGTH_SHORT).show();
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





    private void GetByBarcode(String barcode){

        linearLayout.setVisibility(View.GONE);
        Barcode.setEnabled(false);
        Cursor res;
        res = db.isbarcodeExist(barcode);
        ArrayList<Contact> contacts = new ArrayList<>();

        if(res.getCount() > 0){

            res.moveToFirst();
            productID = res.getString(0);
            Productname.setText(res.getString(1));


            res.close();
            res = db.GetBarUnits(Integer.parseInt(productID));
            if(res.getCount() > 0)
                while (res.moveToNext())
                    contacts.add(new Contact(res.getString(0), res.getString(1),res.getString(2)));


            ArrayAdapter<Contact> adapter =
                    new ArrayAdapter<>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
            adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

            units.setAdapter(adapter);


            units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Contact contact = (Contact) parent.getSelectedItem();
                    UnitName = contact.contact_name;
                    UnitID = contact.contact_id;
                    baseunit =contact.BaseUnit;
                    packageing.setText(baseunit);
                    packageing.setEnabled(false);


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    Contact contact = (Contact) parent.getSelectedItem();
                    UnitName = contact.contact_name;
                    UnitID = contact.contact_id;
                    baseunit =contact.BaseUnit;
                    packageing.setText(baseunit);
                    packageing.setEnabled(false);

                }
            });



            container.setVisibility(View.VISIBLE);
        }else{

            Toast.makeText(this,"هذا الصنف غير موجود !",Toast.LENGTH_SHORT).show();
        }



    }



    public void AddIntoDataBase(View v){

        if(quantity.getText().toString().isEmpty())
            Toast.makeText(this,"الرجاء تعبة الكمية",Toast.LENGTH_SHORT).show();
        else{


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String datetime = simpleDateFormat.format(new Date());

            ContentValues values = new ContentValues();

            values.put("ProductID",Integer.parseInt(productID));
            values.put("Purchase_FK",Integer.parseInt(PurchaseID));
            values.put("ProductUOMID_FK",Integer.parseInt(UnitID));
            values.put("Product_Name",Productname.getText().toString());
            values.put("Quantity",quantity.getText().toString());
            values.put("BaseUnitQ",packageing.getText().toString());
            values.put("ProductBarcode",Barcode.getText().toString());
            values.put("CountingDate",datetime);


            if(db.AddNewProduct(values)) {
                Toast.makeText(this, "تمت عملية الاضافة", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddProductActivity.this, AddProductActivity.class);
                intent.putExtra("ID",PurchaseID);
                intent.putExtra("NAME",ClientName);
                startActivity(intent);
                finish();
            }else
                Toast.makeText(this,"حدث خطأ الرجاء إعادة المحاولة",Toast.LENGTH_SHORT).show();
        }




    }






    ////// spinner structure


    public class Contact {
        private String contact_name;
        private String contact_id;
        private String BaseUnit;

        public Contact() {
        }

        public Contact(String contact_id,String contact_name,String BaseUnit) {
            this.contact_id = contact_id;
            this.contact_name = contact_name;
            this.BaseUnit = BaseUnit;
        }

        public String getContact_name() {
            return contact_name;
        }

        public void setContact_name(String contact_name) {
            this.contact_name = contact_name;
        }

        public String getContact_id() {
            return contact_id;
        }

        public void setContact_id(String contact_id) {
            this.contact_id = contact_id;
        }

        /**
         * Pay attention here, you have to override the toString method as the
         * ArrayAdapter will reads the toString of the given object for the name
         *
         * @return contact_name
         */
        @Override
        public String toString() {
            return contact_name;
        }
    }


    ///// for quantity on click

    public void plus(View view){

        if(quantity.getText().toString().isEmpty()){
            quantity.setText("1");

        }else{
            mines.setVisibility(View.VISIBLE);
            int x = Integer.parseInt(quantity.getText().toString());
            x = x+1;
            quantity.setText(Integer.toString(x));

        }
    }

    @SuppressLint("SetTextI18n")
    public void mines(View view){
        if(quantity.getText().toString().isEmpty()){
            mines.setVisibility(View.INVISIBLE);
            quantity.setText("");

        }if(Integer.parseInt(quantity.getText().toString()) == 1){
            mines.setVisibility(View.INVISIBLE);
            quantity.setText("");

        }else{

            int x = Integer.parseInt(quantity.getText().toString());
            x = x -1;
            quantity.setText(Integer.toString(x));

        }

        if(Integer.parseInt(quantity.getText().toString()) == 1)
            mines.setVisibility(View.INVISIBLE);


    }
    ///////////////



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem cam = menu.findItem(R.id.openCam);
        MenuItem qr = menu.findItem(R.id.QR);
        cam.setVisible(true);
        qr.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:

                bacToMain(null);

                return true;

            case R.id.openCam:
                Barcode.setText("");
                Barcode.setEnabled(true);
                quantity.setText("");
                packageing.setText("");
                Productname.setText("");
                container.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
                OPenCAM();
                return true;

            case R.id.QR:

                Barcode.setText("");
                Barcode.setEnabled(true);
                Productname.setText("");
                quantity.setText("");
                packageing.setText("");
                container.setVisibility(View.GONE);
                linearLayout.setVisibility(View.VISIBLE);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (Result != null) {

            if (Result.getContents() == null) {

                Toast.makeText(this, "لم يتم إلتقاط باركود", Toast.LENGTH_SHORT).show();

            } else {


                Barcode.setText(Result.getContents());
               // GetByBarcode(Result.getContents());


            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
