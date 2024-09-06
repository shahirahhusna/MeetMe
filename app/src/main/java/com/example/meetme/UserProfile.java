package com.example.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserProfile extends AppCompatActivity {

    TextView fullNameTextView, houseUnitTextView, contactNumberTextView;
    ImageView back;
    Button updateProfileButton;
    FirebaseFirestore firestore;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        fullNameTextView = findViewById(R.id.name);
        houseUnitTextView = findViewById(R.id.unit);
        contactNumberTextView = findViewById(R.id.contact);
        updateProfileButton = findViewById(R.id.updateButton);

        firestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadUserProfile();

        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateProfilePage();
            }
        });
    }

    private void loadUserProfile() {
        firestore.collection("UserProfile").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String fullName = document.getString("fullName");
                                String houseUnit = document.getString("houseUnit");
                                String contactNumber = document.getString("contactNumber");

                                fullNameTextView.setText(fullName);
                                houseUnitTextView.setText(houseUnit);
                                contactNumberTextView.setText(contactNumber);
                            } else {
                                showUpdateProfileDialog();
                            }
                        } else {
                            Toast.makeText(UserProfile.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showUpdateProfileDialog() {
        new AlertDialog.Builder(this)
                .setMessage("Please update your profile")
                .setPositiveButton("Update", (dialog, which) -> openUpdateProfilePage())
                .setCancelable(false)
                .show();
    }

    private void openUpdateProfilePage() {
        Intent intent = new Intent(UserProfile.this, UpdateProfile.class);
        startActivity(intent);
    }
}