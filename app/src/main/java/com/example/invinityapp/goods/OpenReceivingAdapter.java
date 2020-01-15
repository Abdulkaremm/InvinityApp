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

public class OpenReceivingAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String>   name,
                        date;

    public OpenReceivingAdapter(Context context,ArrayList<String> Name,ArrayList<String> Date) {
        super(context, R.layout.openreceiving , R.id.SupName,Name);

        this.context = context;
        this.name = Name;
        this.date = Date;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.openreceiving, parent, false);

        TextView T1 = row.findViewById(R.id.SupName);
        TextView T2 = row.findViewById(R.id.date);

        T1.setText(name.get(position));
        T2.setText(date.get(position));

        return row;
    }
}
