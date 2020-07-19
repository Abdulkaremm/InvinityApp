package com.example.invinityapp.goods;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.invinityapp.R;

import java.util.ArrayList;

public class GoodsListAdaptare extends ArrayAdapter<ProductsModel> {


    public GoodsListAdaptare(Context context, ArrayList<ProductsModel> data) {

        super(context, R.layout.goods_layout , data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ProductsModel product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.goods_layout, parent, false);
        }



        TextView T1 = convertView.findViewById(R.id.productName);
        TextView T2 = convertView.findViewById(R.id.ProductBar);
        TextView T3 = convertView.findViewById(R.id.ProductQun);
        TextView T4 = convertView.findViewById(R.id.productUN);
        TextView T5 = convertView.findViewById(R.id.ExpiredDate);
        LinearLayout linearLayout = convertView.findViewById(R.id.eDate);

        T1.setText(product.name);
        T2.setText(product.barcode);
        T3.setText(product.qun);
        T4.setText(product.unit);
        if(product.Edate.compareTo("") != 0 || product.Edate != null)
            T5.setText(product.Edate);
        else
            linearLayout.setVisibility(View.GONE);


        return convertView;
    }
}
