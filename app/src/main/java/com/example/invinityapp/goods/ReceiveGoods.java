package com.example.invinityapp.goods;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReceiveGoods extends AppCompatActivity {

    EditText quantity,
             packageing,
            Productname,
            Barcode;
    TextView date,mines;
    Spinner units;
    LinearLayout container;
    AlertDialog dialog;
    String UnitID, UnitName;
    private DatePickerDialog.OnDateSetListener mydate;
    String SuppllierID,SupplierName;
    String productID;
    InfinityDB db = new InfinityDB(this);
    boolean Isnew;
    SwipeMenuListView swiplist;
    LinearLayout linearLayout,ExDate;
    String baseunit;
    private Dialog datePiker;
    private ArrayList<ProductsModel> productsModels;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_goods);

        container = findViewById(R.id.container);
        swiplist = findViewById(R.id.GoodsList);
        Barcode = findViewById(R.id.barcode);
        Productname = findViewById(R.id.ProductName);
        quantity = findViewById(R.id.quantity);
        packageing = findViewById(R.id.packageing);
        date = findViewById(R.id.date);
        units = findViewById(R.id.UnitsSpinner);
        linearLayout = findViewById(R.id.list);
        ExDate = findViewById(R.id.ExDate);
        mines = findViewById(R.id.mines);

        Intent intent = getIntent();

            SuppllierID = intent.getStringExtra("ID");
            SupplierName = intent.getStringExtra("NAME");



        dialog = new AlertDialog.Builder(this)
                .setTitle("خطأ !")
                .setMessage("هذا الصنف غير موجد.")
//                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AddNewProduct();
//
//                        dialog.dismiss();
//                    }
//                })
                .setNegativeButton("موافق", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        linearLayout.setVisibility(View.VISIBLE);
                        Barcode.setEnabled(true);
                        Barcode.setText("");
                    }
                })
                .create();



        Barcode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((keyCode == 66 || keyCode == 301 || keyCode == 302 || keyCode == 303) && !Barcode.getText().toString().isEmpty()){
                    GetByBarcode(Barcode.getText().toString());


                }
                return false;
            }
        });





        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createDilog();
                datePiker.show();

            }
        });

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
                delete.setWidth(170);
                // set item title
                delete.setIcon(R.drawable.ic_delete);

                // add to menu
                menu.addMenuItem(delete);

                // create "open" item

                SwipeMenuItem Edit = new SwipeMenuItem(getApplicationContext());

                // set item background

                Edit.setBackground(R.color.colorSuccess);
                // set item width
                Edit.setWidth(170);
                // set item title
                Edit.setIcon(R.drawable.ic_update);

                // add to menu
                menu.addMenuItem(Edit);




            }
        };
        swiplist.setMenuCreator(creator);
    }



    public void createDilog(){

        datePiker =  new Dialog(this);
        datePiker.setContentView(R.layout.date_piker);

        final NumberPicker year = datePiker.findViewById(R.id.year);
        final NumberPicker mont = datePiker.findViewById(R.id.mont);
        final NumberPicker day  = datePiker.findViewById(R.id.day);
        TextView pikdate  = datePiker.findViewById(R.id.pikDate);
        TextView close  = datePiker.findViewById(R.id.close);
        TextView qun = datePiker.findViewById(R.id.title_q);
        LinearLayout qunLayout = datePiker.findViewById(R.id.qunLayout);


        qunLayout.setVisibility(View.GONE);
        qun.setVisibility(View.GONE);



        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("YYYY");
        String AdapterDate = df.format(c.getTime());


        year.setMinValue(Integer.parseInt(AdapterDate));
        year.setMaxValue(Integer.parseInt(AdapterDate) + 30);

        mont.setMinValue(1);
        mont.setMaxValue(12);

        day.setMinValue(1);
        day.setMaxValue(31);


        pikdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View h) {

                String dateFormat = year.getValue() + "-" + mont.getValue() + "-" + day.getValue();
                date.setText(dateFormat);
                datePiker.dismiss();

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePiker.dismiss();
            }
        });
    }


    private void GoodsList(){

        Cursor result;

        result = db.LastAddedGoods(SuppllierID);

        productsModels = new ArrayList<>();

        GoodsListAdaptare productAdabter =new GoodsListAdaptare(this, productsModels);

        if(result.getCount() > 0){
            while (result.moveToNext()){

                Cursor res1 = db.LastGoodUnit(result.getString(4),result.getString(5));
                if(res1.getCount() > 0){
                    res1.moveToFirst();
                }
                productAdabter.add(new ProductsModel(result.getString(0), result.getString(3),result.getString(1), result.getString(2), res1.getString(0)));
            }


        }

        swiplist.setAdapter(productAdabter);

        swiplist.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {

                    case 1:
                        Intent intent = new Intent(ReceiveGoods.this, UpdateProductActivity.class);
                        intent.putExtra("SupplierID",SuppllierID);
                        intent.putExtra("ProductID",productsModels.get(position).id);
                        intent.putExtra("ACTIVITY","Receive");
                        startActivity(intent);
                        finish();
                        break;

                    case 0:

                        AlertDialog dialog = new AlertDialog.Builder(ReceiveGoods.this)
                                .setTitle("مسح صنف !")
                                .setMessage("هل انت متأكد من عملية الحدف ؟")
                                .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(db.DeleteAddedProduct(productsModels.get(position).id)) {
                                            Toast.makeText(ReceiveGoods.this, "تم الحدف", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ReceiveGoods.this, ReceiveGoods.class);
                                            intent.putExtra("ID",SuppllierID);
                                            intent.putExtra("NAME",SupplierName);
                                            startActivity(intent);
                                            finish();
                                        }else
                                            Toast.makeText(ReceiveGoods.this,"حدث خطأ الرجاء اعادة المحاولة",Toast.LENGTH_SHORT).show();
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
          Productname.setEnabled(false);
          if(res.getString(2).compareTo("6") != 0)
              ExDate.setVisibility(View.GONE);

          Isnew = false;
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
                  if(baseunit.compareTo("new") != 0){
                      packageing.setText(baseunit);
                      packageing.setEnabled(false);
                  }else
                      packageing.setEnabled(true);

              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

                  Contact contact = (Contact) parent.getSelectedItem();
                  UnitName = contact.contact_name;
                  UnitID = contact.contact_id;
                  baseunit =contact.BaseUnit;
                  if(baseunit.compareTo("new") != 0){
                      packageing.setText(baseunit);
                      packageing.setEnabled(false);
                  }else
                      packageing.setEnabled(true);
              }
          });



          container.setVisibility(View.VISIBLE);
      }else{


          final Cursor result = db.GetProductIfExist(Barcode.getText().toString(),Integer.parseInt(SuppllierID));
          if(result.getCount() > 0){

              AlertDialog dialog = new AlertDialog.Builder(ReceiveGoods.this)
                      .setTitle("تم أستلام هذا الصنف !")
                      .setMessage("تم أستلام الصنف سابقأ هل ترغب في.. ")
                      .setPositiveButton("إضافة", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              AddNewProduct();
                          }
                      })
                      .setNegativeButton("تعديل", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {

                              if(result.getCount() == 1){

                                  result.moveToFirst();
                                  Intent intent = new Intent(ReceiveGoods.this, UpdateProductActivity.class);
                                  intent.putExtra("SupplierID",SuppllierID);
                                  intent.putExtra("ProductID",result.getString(0));
                                  intent.putExtra("ACTIVITY","Receive");
                                  startActivity(intent);
                                  finish();
                              }
                          }
                      })
                      .setNeutralButton("إلغاء", null)
                      .show();

          }else
            dialog.show();

      }



 }




 public  void  AddNewProduct(){



        Cursor res;
        res =db.GetAllUnits();
     ArrayList<Contact> contacts = new ArrayList<>();
     Isnew = true;
     productID = "-1";
     if(res.getCount() > 0)
         while (res.moveToNext())
             contacts.add(new Contact(res.getString(0), res.getString(1),"new"));

     ArrayAdapter<Contact> adapter =
             new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, contacts);
     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

     units.setAdapter(adapter);


     units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

             Contact contact = (Contact) parent.getSelectedItem();
             UnitName = contact.contact_name;
             UnitID = contact.contact_id;

         }

         @Override
         public void onNothingSelected(AdapterView<?> parent) {

             Contact contact = (Contact) parent.getSelectedItem();
             UnitName = contact.contact_name;
             UnitID = contact.contact_id;
         }
     });

     container.setVisibility(View.VISIBLE);
}


   public void AddIntoDataBase(View view){

        if(Productname.getText().toString().isEmpty())
            Toast.makeText(this,"الرجاء تعبة اسم الصنف",Toast.LENGTH_SHORT).show();
        else if(quantity.getText().toString().isEmpty())
                Toast.makeText(this,"الرجاء تعبة الكمية",Toast.LENGTH_SHORT).show();
             else if(packageing.getText().toString().isEmpty())
                   Toast.makeText(this,"الرجاء تعبة العبوة",Toast.LENGTH_SHORT).show();

                   else{

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String datetime = simpleDateFormat.format(new Date());

                       ContentValues values = new ContentValues();

                        values.put("ProductID_PK",Integer.parseInt(productID));
                        values.put("Supplier_FK",Integer.parseInt(SuppllierID));
                        values.put("ProductUOMID_FK",Integer.parseInt(UnitID));
                        values.put("Product_Name",Productname.getText().toString());
                        values.put("Quantity",quantity.getText().toString());
                        values.put("BaseUnitQ",packageing.getText().toString());
                        values.put("ProductBarcode",Barcode.getText().toString());
                        values.put("IsNew",Isnew);
                        values.put("CountingDate",datetime);
                        if(!date.getText().toString().isEmpty())
                            values.put("EndDate",date.getText().toString());


                        if(db.AddGoods(values)) {
                            Toast.makeText(this, "تمت عملية الاضافة", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ReceiveGoods.this, ReceiveGoods.class);
                            intent.putExtra("ID",SuppllierID);
                            intent.putExtra("NAME",SupplierName);
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

        Intent intent = new Intent(this, receiving_goods_main.class);
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
                GetByBarcode(Result.getContents());


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
