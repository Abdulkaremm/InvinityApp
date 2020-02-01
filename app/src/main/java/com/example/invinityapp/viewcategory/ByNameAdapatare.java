package com.example.invinityapp.viewcategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.invinityapp.ExportAPurchaseBill.ClientModale;
import com.example.invinityapp.R;

import java.util.ArrayList;

public class ByNameAdapatare extends ArrayAdapter<ClientModale> {

    public ByNameAdapatare(Context context, ArrayList<ClientModale> clientModales) {
        super(context, R.layout.client_layout ,clientModales);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ClientModale pro = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.client_layout, parent, false);
        }


        TextView T1 = convertView.findViewById(R.id.clientName);
        TextView T2 = convertView.findViewById(R.id.clientID);

        T1.setText(pro.name);
        T2.setText(pro.id);


        return convertView;
    }
}
