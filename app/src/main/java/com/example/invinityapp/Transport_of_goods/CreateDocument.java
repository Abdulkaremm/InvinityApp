package com.example.invinityapp.Transport_of_goods;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.invinityapp.MainActivity;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;
import com.example.invinityapp.inventoryitems.ProductHaveDate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.invinityapp.MainActivity.progressDialog;

public class CreateDocument extends AppCompatActivity {

    private Spinner
            fromBranch,
            fromLocation,
            toBranch,
            toLocation;

    private EditText discription;

    private CheckBox isExpridDate;
    private InfinityDB db;

    private ArrayList<Contact> contactsFromBranch = new ArrayList<>();
    private ArrayList<Contact> contactsFromLocation = new ArrayList<>();
    private ArrayList<Contact> contactstoBranch = new ArrayList<>();
    private ArrayList<Contact> contactstoLocation = new ArrayList<>();

    private boolean needUpdate = false;

    private String idDocument;

    private LinearLayout expiredDAte;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_document);

        db = new InfinityDB(this);

        fromBranch =  findViewById(R.id.branchName);
        fromLocation = findViewById(R.id.fromLocation);
        toBranch = findViewById(R.id.toBranch);
        toLocation = findViewById(R.id.toLocation);

        isExpridDate = findViewById(R.id.checkBox);

        discription = findViewById(R.id.discription);
        expiredDAte = findViewById(R.id.viewOrno);

        Cursor ex = db.ExbordSetting();

        ex.moveToFirst();
        if(ex.getString(1).compareTo("\"TRUE\"") != 0){

            expiredDAte.setVisibility(View.GONE);
        }
        ex.close();




        Cursor branchs = db.SelectAllBranchs();

        while (branchs.moveToNext()){

            contactsFromBranch.add(new Contact(branchs.getString(1), branchs.getString(0)));
            contactstoBranch.add(new Contact(branchs.getString(1), branchs.getString(0)));
        }


        ArrayAdapter<Contact> adapter = new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contactsFromBranch);
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        fromBranch.setAdapter(adapter);

        ArrayAdapter<Contact> adapter2 = new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contactstoBranch);
        adapter2.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        toBranch.setAdapter(adapter2);



        branchs.close();


        Intent intent =getIntent();

        if(intent.hasExtra("id")){

            needUpdate = true;
            idDocument = intent.getStringExtra("id");

            Cursor res = db.SelectDocumentByID(idDocument);
            res.moveToFirst();

            if(res.getString(2).compareTo("1") == 0){

                isExpridDate.setChecked(true);
            }

            discription.setText(res.getString(3));

            for (int  i = 0; i < adapter.getCount() ; i++){

                if(adapter.getItem(i).contact_id.compareTo(res.getString(5)) == 0){

                    fromBranch.setSelection(i);

                    break;
                }
            }

            for (int  i = 0; i < adapter2.getCount() ; i++){

                if(adapter2.getItem(i).contact_id.compareTo(res.getString(9)) == 0){

                    toBranch.setSelection(i);

                    break;
                }
            }
        }



       fromBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) fromBranch.getSelectedItem();

                Cursor location = db.SelectLocationByBranch(contact.contact_id);
                contactsFromLocation.clear();
                while (location.moveToNext()){

                    contactsFromLocation.add(new Contact(location.getString(1), location.getString(0)));
                }

                ArrayAdapter<Contact> adapter1 = new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contactsFromLocation);
                adapter1.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                fromLocation.setAdapter(adapter1);

                Intent intent =getIntent();

                if(intent.hasExtra("id")){

                    Cursor res = db.SelectDocumentByID(idDocument);
                    res.moveToFirst();


                    for (int  i = 0; i < adapter1.getCount() ; i++){

                        if(adapter1.getItem(i).contact_id.compareTo(res.getString(7)) == 0){

                            fromLocation.setSelection(i);

                            break;
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



       toBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Contact contact = (Contact) toBranch.getSelectedItem();
                Cursor location = db.SelectLocationByBranch(contact.contact_id);
                contactstoLocation.clear();

                while (location.moveToNext()) {

                    contactstoLocation.add(new Contact(location.getString(1), location.getString(0)));
                }

                ArrayAdapter<Contact> adapter3 = new ArrayAdapter<Contact>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, contactstoLocation);
                adapter3.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                toLocation.setAdapter(adapter3);

                Intent intent =getIntent();

                if(intent.hasExtra("id")){

                    Cursor res = db.SelectDocumentByID(idDocument);
                    res.moveToFirst();


                    for (int  i = 0; i < adapter3.getCount() ; i++){

                        if(adapter3.getItem(i).contact_id.compareTo(res.getString(11)) == 0){

                            toLocation.setSelection(i);

                            break;
                        }
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
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

                Intent intent = new Intent(CreateDocument.this, TransportGoods.class);
                startActivity(intent);
                CreateDocument.this.finish();

                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addDocument(View v){


        if(needUpdate){

            update();
        }else{

            add();
        }

    }

    private void update(){

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        String thisTime = df.format(c.getTime());
        int isExpiredDataValue;

        if (isExpridDate.isChecked()){

            isExpiredDataValue = 1;

        }else {

            isExpiredDataValue = 0;
        }

        Contact branch = (Contact) fromBranch.getSelectedItem();
        Contact location = (Contact) fromLocation.getSelectedItem();
        Contact toBranches = (Contact) toBranch.getSelectedItem();
        Contact toLocations = (Contact) toLocation.getSelectedItem();




        ContentValues data = new ContentValues();

        data.put("CreatDate", thisTime);
        data.put("IsExpiredDate", isExpiredDataValue);
        data.put("Discription", discription.getText().toString());
        data.put("FromBranchName", branch.contact_name);
        data.put("FromBranchID", branch.contact_id);
        data.put("FromLocationName", location.contact_name);
        data.put("FromLocationID", location.contact_id);
        data.put("ToBranchName", toBranches.contact_name);
        data.put("ToBranchID", toBranches.contact_id);
        data.put("ToLocationName", toLocations.contact_name);
        data.put("ToLocationID", toLocations.contact_id);

        int res = db.updateDocument(data, idDocument);

        if(res > 0){

            Toast.makeText(this, "تمت عملية الحفظ", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(CreateDocument.this, TransportGoods.class);
            startActivity(intent);
            CreateDocument.this.finish();

        }else {

            Toast.makeText(this, "فشلت عملية الحفظ", Toast.LENGTH_SHORT).show();

        }

    }


    private void add(){

        Contact branch = (Contact) fromBranch.getSelectedItem();
        Contact location = (Contact) fromLocation.getSelectedItem();
        Contact toBranches = (Contact) toBranch.getSelectedItem();
        Contact toLocations = (Contact) toLocation.getSelectedItem();

        if(location.contact_id.compareTo(toLocations.contact_id) == 0){

            Toast.makeText(this, "لا يمكن النقل الي نفس المخزن", Toast.LENGTH_SHORT).show();

        }else{

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            String thisTime = df.format(c.getTime());
            int isExpiredDataValue;

            if (isExpridDate.isChecked()){

                isExpiredDataValue = 1;

            }else {

                isExpiredDataValue = 0;
            }






            ContentValues data = new ContentValues();

            data.put("CreatDate", thisTime);
            data.put("IsExpiredDate", isExpiredDataValue);
            data.put("Discription", discription.getText().toString());
            data.put("FromBranchName", branch.contact_name);
            data.put("FromBranchID", branch.contact_id);
            data.put("FromLocationName", location.contact_name);
            data.put("FromLocationID", location.contact_id);
            data.put("ToBranchName", toBranches.contact_name);
            data.put("ToBranchID", toBranches.contact_id);
            data.put("ToLocationName", toLocations.contact_name);
            data.put("ToLocationID", toLocations.contact_id);

            boolean res = db.insertDocument(data);

            if(res){

                Toast.makeText(this, "تمت عملية الحفظ", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CreateDocument.this, TransportGoods.class);
                startActivity(intent);
                CreateDocument.this.finish();

            }else {

                Toast.makeText(this, "فشلت عملية الحفظ", Toast.LENGTH_SHORT).show();

            }

        }

    }


    public static class Contact {
        private String contact_name;
        private String contact_id;

        public Contact() {
        }

        public Contact(String contact_name, String contact_id) {
            this.contact_name = contact_name;
            this.contact_id = contact_id;
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
