package com.example.invinityapp.viewcategory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.invinityapp.R;

import java.util.ArrayList;

public class StockAdapter extends ArrayAdapter<StockModel> {

    public StockAdapter(Context context, ArrayList<StockModel> data) {
        super(context, R.layout.stock_layout, data);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        // Get the data item for this position
        StockModel product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.stock_layout, parent, false);
        }
        // Lookup view for data population
        TextView t1 = convertView.findViewById(R.id.branchName);
        TextView t2 = convertView.findViewById(R.id.locationName);
        TextView t3 = convertView.findViewById(R.id.uomsname);
        TextView t4 = convertView.findViewById(R.id.stock);
        TextView t5 = convertView.findViewById(R.id.expiredDate);
        TextView t6 = convertView.findViewById(R.id.lastSync);
        // Populate the data into the template view using the data object
        t1.setText(product.branch);
        t2.setText(product.location);
        t3.setText(product.uom);
        t4.setText(product.stock);
        if(product.exDate.compareTo("") == 0){

            t5.setVisibility(View.GONE);
        }else{

            String str = t5.getText().toString() + product.exDate;
            t5.setText(str);
        }

        String str2 = t6.getText().toString() + product.lastSync;

        t6.setText(str2);
        // Return the completed view to render on screen
        return convertView;
    }
}
