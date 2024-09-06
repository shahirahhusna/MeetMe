package com.example.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminAlerts extends AppCompatActivity {

    ListView alertListView;
    List<String> alerts;
    ArrayAdapter<String> adapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_alerts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        alertListView = findViewById(R.id.alertListView);
        alerts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alerts);
        alertListView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        db.collection("admin_alerts").whereEqualTo("status", "Pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        alerts.clear();
                        for (QueryDocumentSnapshot doc : value) {
                            String message = doc.getString("message");
                            alerts.add(message);
                            Map<String, Object> contractorDetails = (Map<String, Object>) doc.get("contractorDetails");
                            showAlertDialog(message, contractorDetails);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    private void showAlertDialog(String message, Map<String, Object> contractorDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Contractor Alert");

        // Inflate custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_contractor_alert, null);
        builder.setView(customLayout);

        TextView alertMessage = customLayout.findViewById(R.id.alertMessage);
        TextView contractorName = customLayout.findViewById(R.id.contractorName);
        TextView contractorContact = customLayout.findViewById(R.id.contractorContact);

        alertMessage.setText(message);
        contractorName.setText("Name: " + contractorDetails.get("name"));
        contractorContact.setText("Contact: " + contractorDetails.get("phone"));

        builder.setPositiveButton("Approve", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AdminAlerts.this, ManageRequest.class);
                startActivity(intent);
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}