package com.example.invinityapp.goods;

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

public class GoodsListAdaptare extends ArrayAdapter<String> {

    Context context;
    private ArrayList<String>   barcode,
                        name,
                        qun,
                        unit;



    public GoodsListAdaptare(@NonNull Context context, ArrayList<String> bars,ArrayList<String> prname,ArrayList<String> qunn,ArrayList<String> UOM) {
        super(context, R.layout.goods_layout , R.id.productName,prname);
        this.context = context;
        this.barcode = bars;
        this.name = prname;
        this.qun = qunn;
        this.unit = UOM;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.goods_layout, parent, false);

        TextView T1 = row.findViewById(R.id.productName);
        TextView T2 = row.findViewById(R.id.ProductBar);
        TextView T3 = row.findViewById(R.id.ProductQun);
        TextView T4 = row.findViewById(R.id.productUN);

        T1.setText(name.get(position));
        T2.setText(barcode.get(position));
        T3.setText(qun.get(position));
        T4.setText(unit.get(position));

        return row;
    }
}
