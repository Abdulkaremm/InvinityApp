package com.example.invinityapp.goods;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.invinityapp.R;

import java.util.ArrayList;

public class SuppliersAdapater extends ArrayAdapter<String> implements Filterable {





    private Context context;
    // Original Values
    private ArrayList<String>    names,
                                 IDs;


    SuppliersAdapater(@NonNull Context context, ArrayList<String> Name, ArrayList<String> ID) {
        super(context, R.layout.suppliers_layout , R.id.Supname,Name);

        this.context = context;
        this.names = Name;
        this.IDs = ID;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("ViewHolder") View row = layoutInflater.inflate(R.layout.suppliers_layout, parent, false);


        TextView T1 = row.findViewById(R.id.Supname);
        TextView T2 = row.findViewById(R.id.supID);
        T1.setText(names.get(position));
        T2.setText(IDs.get(position));

        return row;
    }


}
