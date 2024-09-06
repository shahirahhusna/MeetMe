package com.example.meetme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ManageRequest extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView back;
    ArrayList<Visitor> visitorArrayList;
    ApprovalAdapter approvalAdapter;
    FirebaseFirestore db;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.manage_request);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageRequest.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db = FirebaseFirestore.getInstance();
        visitorArrayList = new ArrayList<>();
        approvalAdapter = new ApprovalAdapter(ManageRequest.this, visitorArrayList);

        recyclerView.setAdapter(approvalAdapter);

        manageRequest();


    }

    private void manageRequest() {
        db.collection("visitors").whereNotEqualTo("category","Family/Friend").whereEqualTo("status","Pending").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {

                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    Log.e("Firestore Error", Objects.requireNonNull(error.getMessage()));
                    return;
                }

                if (value != null && !value.isEmpty()) {

                    visitorArrayList.clear();
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            Visitor visitor = dc.getDocument().toObject(Visitor.class);
                            visitor.setDocumentId(dc.getDocument().getId());  // Store the document ID
                            visitorArrayList.add(visitor);
                        }
                    }
                    approvalAdapter.notifyDataSetChanged();

                }

                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        });
    }
}