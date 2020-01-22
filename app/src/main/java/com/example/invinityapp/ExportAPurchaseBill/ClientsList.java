package com.example.invinityapp.ExportAPurchaseBill;

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

import com.example.invinityapp.R;
import com.example.invinityapp.goods.AddNewSup;
import com.example.invinityapp.inventoryitems.InfinityDB;

import java.util.ArrayList;

public class ClientsList extends AppCompatActivity {

    ListView clientsList;
    TextView title;
    LinearLayout container;
    ArrayList<ClientModale> clientModales;
    InfinityDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clients_list);



        clientsList = findViewById(R.id.clientsList);
        title = findViewById(R.id.Title);
        container = findViewById(R.id.container);

        GetAllClients();
    }

    private void GetAllClients(){

        db = new InfinityDB(this);
        clientModales = new ArrayList<>();

        ClientAdapatare clientAdapatare = new ClientAdapatare(this,clientModales);

        Cursor res =  db.GetAllClients();

        if(res.getCount() > 0){

            while (res.moveToNext()){

                clientAdapatare.add(new ClientModale(res.getString(0),res.getString(1)));

            }

            clientsList.setAdapter(clientAdapatare);

            title.setText("قائمة الموردين");

            container.setVisibility(View.VISIBLE);

        }else{

            title.setText("لا يوجد زبائن لعرضهم ");
        }


        clientsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String name = ((TextView) view.findViewById(R.id.clientName)).getText().toString();
                String ClientID   = ((TextView) view.findViewById(R.id.clientID)).getText().toString();
                Intent intent = new Intent(ClientsList.this, CreateNewBillActivity.class);
                intent.putExtra("client",name);
                intent.putExtra("ID",ClientID);
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
              //  GetSupplires(newText);
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

        Intent intent = new Intent(this, CreateNewBillActivity.class);
        startActivity(intent);
        finish();
    }

}
