package com.example.meetme;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdminHistoryAdapter extends RecyclerView.Adapter<AdminHistoryAdapter.MyViewHolder> {

    Context context;
    ArrayList<Visitor> visitorarrayList;

    public AdminHistoryAdapter(Context context, ArrayList<Visitor> visitorarrayList) {
        this.context = context;
        this.visitorarrayList = visitorarrayList;
    }

    @NonNull
    @Override
    public AdminHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.adminhistory_list,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull AdminHistoryAdapter.MyViewHolder holder, int position) {

        Visitor visitor = visitorarrayList.get(position);
        holder.name.setText(visitor.name);
        holder.phone.setText(visitor.phone);
        holder.date.setText(visitor.date);
        holder.time.setText(visitor.time);
        holder.vehicle.setText(visitor.vehicle);
        holder.status.setText(visitor.status);
        holder.category.setText(visitor.category);
        holder.adminName.setText(visitor.adminName);

    }

    @Override
    public int getItemCount() {
        return visitorarrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, date, time, vehicle, status, category, adminName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.visitorName);
            phone = itemView.findViewById(R.id.visitorPhone);
            date = itemView.findViewById(R.id.visitDate);
            time = itemView.findViewById(R.id.visitTime);
            vehicle = itemView.findViewById(R.id.visitorVehicle);
            status = itemView.findViewById(R.id.visitorStatus);
            category = itemView.findViewById(R.id.visitorCategory);
            adminName = itemView.findViewById(R.id.adminName);
        }
    }

}
