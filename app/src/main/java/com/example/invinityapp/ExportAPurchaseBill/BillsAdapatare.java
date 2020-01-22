package com.example.invinityapp.ExportAPurchaseBill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.invinityapp.R;


import java.util.ArrayList;


public class BillsAdapatare extends ArrayAdapter<BillsModel> {


    public BillsAdapatare(Context context, ArrayList<BillsModel> bills) {
        super(context, R.layout.bills_adaptare , bills);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        BillsModel bill = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bills_adaptare, parent, false);
        }


        TextView T1 = convertView.findViewById(R.id.clientName);
        TextView T2 = convertView.findViewById(R.id.date);

        T1.setText(bill.name);
        T2.setText(bill.date);


        return convertView;
    }
}
