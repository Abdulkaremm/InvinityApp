package com.example.invinityapp.Transport_of_goods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.invinityapp.R;

import java.util.ArrayList;

public class DocumenAdabter extends ArrayAdapter<DocumentData> {

    public DocumenAdabter(Context context, ArrayList<DocumentData> data) {
        super(context, R.layout.list_documents, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        // Get the data item for this position
        DocumentData product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_documents, parent, false);
        }
        // Lookup view for data population
        TextView t1 = convertView.findViewById(R.id.fromBranch);
        TextView t2 = convertView.findViewById(R.id.toBranch);
        TextView t3 = convertView.findViewById(R.id.fromLocation);
        TextView t4 = convertView.findViewById(R.id.tolocation);
        TextView t5 = convertView.findViewById(R.id.discription);
        // Populate the data into the template view using the data object
        t1.setText(product.FromBranchName);
        t2.setText(product.ToBranchName);
        t3.setText(product.FromLocationName);
        t4.setText(product.ToLocationName);
        t5.setText(product.Discription);
        // Return the completed view to render on screen
        return convertView;
    }

}
