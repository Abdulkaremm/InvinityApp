package com.example.invinityapp.goods;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.Calendar;

public class AddNewSup extends AppCompatActivity {

    EditText    Supplier,
                BillNum;
    TextView date,
             addSup;
    String id;
    String Supname;

    private DatePickerDialog.OnDateSetListener mydate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_sup);

        Supplier = findViewById(R.id.supplier);
        BillNum = findViewById(R.id.billNumber);
        date = findViewById(R.id.date);
        addSup = findViewById(R.id.addSup);


        Intent intent = getIntent();
        intent.getExtras();
        if(intent.hasExtra("ID")){
            id = intent.getStringExtra("ID");
            Supplier.setText(intent.getStringExtra("Supplier"));
        }else{
            id = "-1";
        }


        listeners();


    }



       // a method for any listener event

    private void listeners(){

        addSup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Goadd = new Intent(AddNewSup.this,SuppliersList.class);
                startActivity(Goadd);
                finish();
            }
        });

        ///// to choose date
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                int yer = cal.get(Calendar.YEAR);
                int mon = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        AddNewSup.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mydate,
                        yer,mon,day);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();

            }
        });

        mydate = new DatePickerDialog.OnDateSetListener(){


            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String DataDate = dayOfMonth + "/" + month + "/" + year;
                date.setText(DataDate);



            }
        }; /// date end here

       Supplier.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {

           }

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void afterTextChanged(Editable s) {
               if(s.toString().isEmpty())
                   id = "-1";

           }
       });

    } //  listeners() method end here



     /// open new receiving set

    public void AddNew(View view){

        InfinityDB db = new InfinityDB(this);


        if(Supplier.getText().toString().isEmpty()){ // f-1
            Toast.makeText(this,"الرجاء ادخال إسم المورد",Toast.LENGTH_SHORT).show();
        }else
            if(BillNum.getText().toString().isEmpty()){

                Toast.makeText(this,"الرجاء ادخال رقم الفاتورة",Toast.LENGTH_SHORT).show();

        }else
            if(date.getText().toString().isEmpty()){

                Toast.makeText(this,"الرجاء ادخال تاريخ الإستلام ",Toast.LENGTH_SHORT).show();

            }else{

                ContentValues values = new ContentValues();
                Supname = Supplier.getText().toString();
                values.put("SupplierID_FK", Integer.parseInt(id));
                values.put("Supplier_Name", Supname);
                values.put("Bill_Number", BillNum.getText().toString());
                values.put("Receive_Date", date.getText().toString());

                if(!db.Openreceiving(values)){
                    Toast.makeText(this,"حدث خطأ! الرجاء اعادة المحاولة",Toast.LENGTH_SHORT).show();
                }else{

                    Cursor res = db.getLastSup();
                    if(res.getCount() > 0) {

                        res.moveToFirst();
                        Intent GoaddGoods = new Intent(this,ReceiveGoods.class);
                        GoaddGoods.putExtra("ID",res.getString(0));
                        GoaddGoods.putExtra("NAME",Supname);
                        startActivity(GoaddGoods);
                        finish();
                    }

                }


            }


    } // end of AddNew(View view) method








            //////////// option menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        /*MenuItem camera = menu.findItem(R.id.openCam);
        MenuItem QR = menu.findItem(R.id.QR);

        camera.setVisible(true);
        QR.setVisible(true);
        */
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
