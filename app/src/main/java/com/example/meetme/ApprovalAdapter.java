package com.example.meetme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ApprovalAdapter extends RecyclerView.Adapter<ApprovalAdapter.MyViewHolder> {

    Context context;
    ArrayList<Visitor> visitorarrayList;
    FirebaseFirestore db;
    String adminUserName;

    public ApprovalAdapter(Context context, ArrayList<Visitor> visitorarrayList) {
        this.context = context;
        this.visitorarrayList = visitorarrayList;
        this.db = FirebaseFirestore.getInstance();
        this.adminUserName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
    }

    @NonNull
    @Override
    public ApprovalAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.approval_list,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ApprovalAdapter.MyViewHolder holder, int position) {

        Visitor visitor = visitorarrayList.get(position);
        holder.name.setText(visitor.name);
        holder.phone.setText(visitor.phone);
        holder.date.setText(visitor.date);
        holder.time.setText(visitor.time);
        holder.vehicle.setText(visitor.vehicle);
        holder.category.setText(visitor.category);

        holder.approve.setOnClickListener(v -> updateVisitorStatus(visitor.getDocumentId(), "Approved"));
        holder.reject.setOnClickListener(v -> updateVisitorStatus(visitor.getDocumentId(), "Rejected"));

    }

    @Override
    public int getItemCount() {
        return visitorarrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, date, time, vehicle, approve, reject, category;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.visitorName);
            phone = itemView.findViewById(R.id.visitorPhone);
            date = itemView.findViewById(R.id.visitDate);
            time = itemView.findViewById(R.id.visitTime);
            vehicle = itemView.findViewById(R.id.visitorVehicle);
            approve = itemView.findViewById(R.id.approve);
            reject = itemView.findViewById(R.id.reject);
            category = itemView.findViewById(R.id.visitorCategory);
        }
    }

    private void updateVisitorStatus(String documentId, String status) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", status);
        updates.put("adminName", adminUserName);
        db.collection("visitors").document(documentId).update(updates)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
    }

}
