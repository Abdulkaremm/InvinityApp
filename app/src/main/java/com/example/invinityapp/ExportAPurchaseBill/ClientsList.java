package com.example.invinityapp.ExportAPurchaseBill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.invinityapp.R;
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



        clientsList = findViewById(R.id.SupList);
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

            //title.setText("قائمة الموردينلا يوجد زبائن لعرضهم ");
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

}
