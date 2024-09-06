package com.example.meetme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    Context context;
    ArrayList<Visitor> visitorarrayList;

    public MyAdapter(Context context, ArrayList<Visitor> visitorarrayList) {
        this.context = context;
        this.visitorarrayList = visitorarrayList;
    }

    @NonNull
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.visitor_list,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {

        Visitor visitor = visitorarrayList.get(position);
        holder.name.setText(visitor.name);
        holder.phone.setText(visitor.phone);
        holder.date.setText(visitor.date);
        holder.time.setText(visitor.time);
        holder.vehicle.setText(visitor.vehicle);
        holder.status.setText(visitor.status);
        holder.category.setText(visitor.category);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VisitorDetail.class);
                intent.putExtra("visitorId", visitor.id);
                intent.putExtra("name", visitor.name);
                intent.putExtra("phone", visitor.phone);
                intent.putExtra("date", visitor.date);
                intent.putExtra("time", visitor.time);
                intent.putExtra("vehicle", visitor.vehicle);
                intent.putExtra("status", visitor.status);
                intent.putExtra("category", visitor.category);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return visitorarrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, date, time, vehicle, status, category;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.visitorName);
            phone = itemView.findViewById(R.id.visitorPhone);
            date = itemView.findViewById(R.id.visitDate);
            time = itemView.findViewById(R.id.visitTime);
            vehicle = itemView.findViewById(R.id.visitorVehicle);
            status = itemView.findViewById(R.id.visitorStatus);
            category = itemView.findViewById(R.id.visitorCategory);

        }
    }
}
