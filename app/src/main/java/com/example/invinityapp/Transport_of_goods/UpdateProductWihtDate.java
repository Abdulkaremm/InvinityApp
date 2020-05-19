package com.example.invinityapp.Transport_of_goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.DataDates;
import com.example.invinityapp.inventoryitems.DateAdabter;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.example.invinityapp.inventoryitems.UpdateProductHaveDate;
import com.example.invinityapp.inventoryitems.inventroyscan;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class UpdateProductWihtDate extends AppCompatActivity {

    private RecyclerView ExDates;
    private DateAdabter RecyclerViewAdapter;

    private RecyclerView.LayoutManager layoutManager;

    private TextView name, barcode;
    private ArrayList<DataDates> data;
    private String AdapterDate;
    private Dialog datePiker;
    private TextView SumQun;
    private Spinner spinner;
    private LinearLayout layout;
    InfinityDB db;

    private String idUOMS, Product_ID, BaseUnit, UOMNAME;

    private ArrayList<Contact> contacts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product_wiht_date);

        db = new InfinityDB(this);
        ExDates = findViewById(R.id.listDates);
        ExDates.setHasFixedSize(false);

        TextView AddNewDate = findViewById(R.id.AddNewDate);

        data = new ArrayList<>();


        barcode  =  findViewById(R.id.barcode);
        name     =  findViewById(R.id.name);
        spinner = findViewById(R.id.spinner);
        SumQun = findViewById(R.id.sumQunt);
        layout = findViewById(R.id.lsumQ);

        Intent GetData = getIntent();

        Cursor res = db.getDataProduct(GetData.getStringExtra("Barcode"));

        while (res.moveToNext()) {


            barcode.setText(GetData.getStringExtra("Barcode"));
            name.setText(res.getString(1));
            Product_ID = res.getString(0);
            contacts.add(new Contact(res.getString(2), res.getString(3), res.getString(4)));
        }


        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contacts);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        Cursor uomResult = db.getDocumentProductByProductID(Product_ID, Integer.toString(ScanProduct.DocumentID_PK));

        if(uomResult.getCount() != 0){

            uomResult.moveToFirst();

            for(int i = 0; i < adapter.getCount() ;i++){

                if(uomResult.getString(6).compareTo(adapter.getItem(i).contact_id) == 0){

                    spinner.setSelection(i);
                    break;
                }
            }
        }

        uomResult.close();


        Cursor res2 = db.getDocumentProductByProductID(Product_ID, Integer.toString(ScanProduct.DocumentID_PK));
        int sum = 0;
        while (res2.moveToNext()) {

            data.add(new DataDates(res2.getString(5), res2.getString(4)));

            sum += Integer.parseInt(res2.getString(4));
        }

        SumQun.setText(Integer.toString(sum));
        layout.setVisibility(View.VISIBLE);

        layoutManager = new LinearLayoutManager(this);
        RecyclerViewAdapter = new DateAdabter(data);

        ExDates.setLayoutManager(layoutManager);
        ExDates.setAdapter(RecyclerViewAdapter);


        AddNewDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDilog(-1);
                datePiker.show();
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog dialog = new AlertDialog.Builder(UpdateProductWihtDate.this)
                        .setTitle("مسح !")
                        .setMessage("هل انت متأكد من عملية الحدف ؟")
                        .setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                data.remove(viewHolder.getAdapterPosition());
                                RecyclerViewAdapter.notifyDataSetChanged();

                                if(data.size() == 0){

                                    layout.setVisibility(View.INVISIBLE);
                                }else{

                                    int sum = 0;
                                    for (int i = 0; i < data.size() ;i++){

                                        sum += Integer.parseInt(data.get(i).q);
                                    }

                                    SumQun.setText(Integer.toString(sum));


                                }


                            }
                        })
                        .setNegativeButton("لا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                RecyclerViewAdapter.notifyDataSetChanged();
                            }
                        }).show();

                dialog.setCanceledOnTouchOutside(false);
            }
        });

        itemTouchHelper.attachToRecyclerView(ExDates);

        RecyclerViewAdapter.setOnItemClickListener(new DateAdabter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                createDilog(position);
                datePiker.show();
            }
        });

        if(GetData.hasExtra("addQuntAndDate")){

            createDilog(-1);
            datePiker.show();
        }




    }


    public void createDilog(final int type){

        datePiker =  new Dialog(this);
        datePiker.setContentView(R.layout.date_piker);

        final NumberPicker year = datePiker.findViewById(R.id.year);
        final NumberPicker mont = datePiker.findViewById(R.id.mont);
        final NumberPicker day  = datePiker.findViewById(R.id.day);
        TextView pikdate  = datePiker.findViewById(R.id.pikDate);
        TextView close  = datePiker.findViewById(R.id.close);
        final TextView  mines = datePiker.findViewById(R.id.mines);
        TextView add = datePiker.findViewById(R.id.plus);
        final TextView msg = datePiker.findViewById(R.id.msg);
        final EditText quntity = datePiker.findViewById(R.id.quntity);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(quntity.getText().toString().isEmpty()){

                    quntity.setText("1");

                }else{

                    mines.setVisibility(View.VISIBLE);
                    int x = Integer.parseInt(quntity.getText().toString());
                    x = x+1;
                    quntity.setText(Integer.toString(x));
                }

            }
        });

        mines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(quntity.getText().toString().isEmpty()){

                    quntity.setText("1");

                }if(Integer.parseInt(quntity.getText().toString()) == 1){

                    quntity.setText("1");
                    v.setVisibility(View.INVISIBLE);

                }else{

                    int x = Integer.parseInt(quntity.getText().toString());
                    x = x -1;
                    quntity.setText(Integer.toString(x));

                    if(Integer.parseInt(quntity.getText().toString()) == 1){

                        quntity.setText("1");
                        v.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });


        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("YYYY");
        AdapterDate = df.format(c.getTime());


        year.setMinValue(Integer.parseInt(AdapterDate));
        year.setMaxValue(Integer.parseInt(AdapterDate) + 30);
        year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, mont.getValue()-1);
                c.set(Calendar.YEAR, newVal);

                int res = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                day.setValue(res);

            }
        });


        mont.setMinValue(1);
        mont.setMaxValue(12);




        mont.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                Calendar c = Calendar.getInstance();
                c.set(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.MONTH, newVal-1);
                c.set(Calendar.YEAR, year.getValue());

                int res = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                day.setValue(res);

            }
        });

        day.setMinValue(1);
        day.setMaxValue(31);

        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, mont.getValue());
        cal.set(Calendar.DAY_OF_MONTH, year.getValue());

        int res = cal.getActualMaximum(Calendar.DATE);
        day.setValue(res);
        day.setEnabled(false);

        if(type != -1){

            quntity.setText(data.get(type).q);

            String currentString = data.get(type).date;
            String[] separated = currentString.split("-");
            year.setValue(Integer.parseInt(separated[0]));
            mont.setValue(Integer.parseInt(separated[1]));
            day.setValue(Integer.parseInt(separated[2]));
        }


        pikdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View h) {

                if(quntity.getText().toString().isEmpty() || quntity.getText().toString().compareTo("0") == 0){

                    msg.setVisibility(View.VISIBLE);

                }else{

                    String d =  year.getValue() + "-" + mont.getValue() + "-" + day.getValue();

                    if(type == -1){

                        data.add(new DataDates(d, quntity.getText().toString()));

                    }else{

                        data.get(type).q = quntity.getText().toString();
                        data.get(type).date = d;
                    }

                    RecyclerViewAdapter.notifyDataSetChanged();

                    int sum = 0;
                    for (int i = 0; i < data.size() ;i++){

                        sum += Integer.parseInt(data.get(i).q);
                    }
                    SumQun.setText(Integer.toString(sum));
                    layout.setVisibility(View.VISIBLE);

                    datePiker.dismiss();

                }

            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePiker.dismiss();
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

                Intent intent = new Intent(UpdateProductWihtDate.this, ScanProduct.class);
                startActivity(intent);
                UpdateProductWihtDate.this.finish();

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public void update(View view){


        String bar = barcode.getText().toString(),
                thisTime,
                ip,
                username = "";

        Cursor dv = db.ExbordSetting();
        dv.moveToFirst();
        ip = dv.getString(8);



        if (data.size() == 0) {


            Toast.makeText(getApplicationContext(), "الرجاء ادخال تاريخ الصلاحية", Toast.LENGTH_SHORT).show();


        } else {

            db.DeleteDocumentProductByProductID(Product_ID, Integer.toString(ScanProduct.DocumentID_PK));

            boolean ifInsert = false;

            Contact contact = (Contact) spinner.getSelectedItem();

            UOMNAME = contact.contact_name;
            idUOMS = contact.contact_id;
            BaseUnit = contact.contact_id;

            for (int i = 0; i < data.size() ;i++) {

                Calendar c = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
                thisTime = df.format(c.getTime());

                ContentValues values = new ContentValues();
                values.put("DocumentID_FK", ScanProduct.DocumentID_PK);
                values.put("Product_ID_PK", Product_ID);
                values.put("ProductName", name.getText().toString());
                values.put("UOMName", UOMNAME);
                values.put("UOMID_PK", idUOMS);
                values.put("BaseUnitQ", BaseUnit);
                values.put("barcode", bar);
                values.put("quantity", data.get(i).q);
                values.put("endDate", data.get(i).date);
                values.put("dateTime", thisTime);
                values.put("deviceIP", ip);
                values.put("userName", username);
                ifInsert = db.inserProduToDocument(values);
            }





            if (ifInsert) {

                Toast.makeText(getApplicationContext(), "تمت عملية الحفظ", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateProductWihtDate.this, ScanProduct.class);
                startActivity(intent);
                UpdateProductWihtDate.this.finish();
            } else {

                Toast.makeText(getApplicationContext(), "فشلت عملية الحفظ", Toast.LENGTH_SHORT).show();

            }
        }


    }






    public static class Contact {
        private String contact_name;
        private String contact_id;
        private String BaseUnit;

        public Contact() {
        }

        public Contact(String contact_name, String contact_id, String BaseUnit) {
            this.contact_name = contact_name;
            this.contact_id = contact_id;
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

}
