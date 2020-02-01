package com.example.invinityapp.viewcategory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.invinityapp.ExportAPurchaseBill.ClientAdapatare;
import com.example.invinityapp.ExportAPurchaseBill.ClientModale;
import com.example.invinityapp.ExportAPurchaseBill.ClientsList;
import com.example.invinityapp.ExportAPurchaseBill.CreateNewBillActivity;
import com.example.invinityapp.R;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class SearchByNameActivity extends AppCompatActivity {


    TextView title;
    LinearLayout container;
    ListView list;
    ArrayList<ClientModale> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name);

        title = findViewById(R.id.Title);
        container = findViewById(R.id.container);
        list = findViewById(R.id.List);


    }




     private void GetByName(String filter){

        InfinityDB db = new InfinityDB(this);
        arrayList = new ArrayList<>();
        Cursor res = db.FilterByName(filter);

         ClientAdapatare adapatare = new ClientAdapatare(this,arrayList);

        if(res.getCount() > 0){

            while (res.moveToNext()){

                adapatare.add(new ClientModale(res.getString(0),res.getString(1)));

            }

            list.setAdapter(adapatare);

        }


         list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

             @Override
             public void onItemClick(AdapterView<?> parent, View view,
                                     int position, long id) {
                 String barcode   = ((TextView) view.findViewById(R.id.clientID)).getText().toString();
                 Intent intent = new Intent(SearchByNameActivity.this, DataOrder.class);
                 intent.putExtra("barcode",barcode);
                 startActivity(intent);
                 finish();
             }

         });


    }








    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.inventorymenu, menu);

        MenuItem search = menu.findItem(R.id.search);

        SearchView searchView = (SearchView) search.getActionView();

        searchView.setQueryHint("بحث");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                GetByName(newText);
                return false;
            }
        });

        search.setVisible(true);

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

        Intent intent = new Intent(this, DataOrder.class);
        startActivity(intent);
        finish();
    }

}
