package com.example.invinityapp.goods;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class SuppliersList extends AppCompatActivity {

    ListView supList;
    TextView title;
    LinearLayout container;
    EditText search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppliers_list);

        supList = findViewById(R.id.SupList);
        title = findViewById(R.id.Title);
        container = findViewById(R.id.container);
        search = findViewById(R.id.SearchSup);

        InfinityDB db = new InfinityDB(this);
        Cursor res;


        res = db.GetSuppliers();
        if(res.getCount() > 0) {

            title.setText("قائمة الموردين");
            GetSupplires("null");
        }else
            title.setText("لا يوجد موردين لعرضهم ");



        supList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name = ((TextView) view.findViewById(R.id.Supname)).getText().toString();
                String supid   = ((TextView) view.findViewById(R.id.supID)).getText().toString();
                Intent intent = new Intent(SuppliersList.this,AddNewSup.class);
                intent.putExtra("Supplier",name);
                intent.putExtra("ID",supid);
                startActivity(intent);
                finish();
            }

        });


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                GetSupplires(s.toString());
            }
        });


    }




    private void GetSupplires(String filter){

        InfinityDB db = new InfinityDB(this);
        Cursor res;
        ArrayList<String>   names = new ArrayList<>(),
                            IDs = new ArrayList<>();

        SuppliersAdapater adapter;

            if(filter.compareTo("null") == 0){

                res = db.GetSuppliers();

                if(res.getCount() > 0){

                    while (res.moveToNext()){

                        IDs.add(res.getString(0));
                        names.add(res.getString(1));
                    }



                    adapter = new SuppliersAdapater(this,names,IDs);
                    supList.setAdapter(adapter);
                    supList.setTextFilterEnabled(true);
                    container.setVisibility(View.VISIBLE);
                }


            }else{

                res = db.FilterSuppliers(filter);

                if(res.getCount() > 0){


                    while (res.moveToNext()){

                        IDs.add(res.getString(0));
                        names.add(res.getString(1));
                    }



                    adapter = new SuppliersAdapater(this,names,IDs);
                    supList.setAdapter(adapter);
                    supList.setTextFilterEnabled(true);
                    container.setVisibility(View.VISIBLE);

                }else
                    Toast.makeText(SuppliersList.this,"هذا المورد غير موجود !",Toast.LENGTH_SHORT).show();




            }

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

        Intent intent = new Intent(this,AddNewSup.class);
        startActivity(intent);
        finish();
    }
}
