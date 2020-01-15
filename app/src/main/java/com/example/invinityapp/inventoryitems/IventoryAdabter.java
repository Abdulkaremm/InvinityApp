package com.example.invinityapp.inventoryitems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.invinityapp.R;

import java.util.ArrayList;


public class IventoryAdabter extends ArrayAdapter<ProductData> {


    public IventoryAdabter(Context context, ArrayList<ProductData> data) {
        super(context, R.layout.data_inventory, data);
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent) {


        // Get the data item for this position
        ProductData product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.data_inventory, parent, false);
        }
        // Lookup view for data population
        TextView t1 = convertView.findViewById(R.id.barcodes);
        TextView t2 = convertView.findViewById(R.id.date);
        TextView t3 = convertView.findViewById(R.id.qun);
        TextView t4 = convertView.findViewById(R.id.uomsname);
        TextView t5 = convertView.findViewById(R.id.productname);
        TextView t6 = convertView.findViewById(R.id.counter);
        // Populate the data into the template view using the data object
        t1.setText(product.barcode);
        t2.setText(product.date);
        t3.setText(product.qun);
        t4.setText(product.UOM);
        t5.setText(product.name);
        t6.setText(Integer.toString(product.number));
        // Return the completed view to render on screen
        return convertView;
    }

}
