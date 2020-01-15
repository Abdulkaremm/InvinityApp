package com.example.invinityapp.inventoryitems;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.invinityapp.R;
import java.util.ArrayList;


public class DateAdabter extends RecyclerView.Adapter<DateAdabter.ViewHolder> {

    private ArrayList<DataDates> dataDates;
    private OnItemClickListener click;
    public interface OnItemClickListener {

        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        click = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView date;
        public TextView qun;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            date = itemView.findViewById(R.id.date);
            qun = itemView.findViewById(R.id.quntity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(listener != null){

                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

    }

    public DateAdabter (ArrayList<DataDates> dates){

        dataDates = dates;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dates, parent, false);
        ViewHolder viewHolder = new ViewHolder(v, click);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DataDates date = dataDates.get(position);
        holder.date.setText(date.date);
        holder.qun.setText(date.q);

    }

    @Override
    public int getItemCount() {
        return dataDates.size();
    }

}
