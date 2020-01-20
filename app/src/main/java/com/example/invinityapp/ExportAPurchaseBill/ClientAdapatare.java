package com.example.invinityapp.ExportAPurchaseBill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.invinityapp.R;
import java.util.ArrayList;

public class ClientAdapatare extends ArrayAdapter<ClientModale> {



    public ClientAdapatare(Context context, ArrayList<ClientModale> clientModales) {
        super(context, R.layout.client_layout ,clientModales);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ClientModale client = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bills_adaptare, parent, false);
        }


        TextView T1 = convertView.findViewById(R.id.client);
        TextView T2 = convertView.findViewById(R.id.date);

        T1.setText(client.name);
        T2.setText(client.id);


        return convertView;
    }
}
