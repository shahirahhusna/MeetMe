package com.example.meetme;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdminMainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SCAN = 1;
    private CardView scan, approval, renovation, adminHistory, announcement;
    TextView name, date, logout;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    ImageView alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_main);

        alert = findViewById(R.id.notification);
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AdminAlerts.class);
                startActivity(intent);
                finish();
            }
        });

        name = findViewById(R.id.adminName);
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    firebaseAuth.signOut();
                    // After signing out, redirect the user to the login or splash screen
                    Intent intent = new Intent(AdminMainActivity.this, AdminLoginPage.class);
                    startActivity(intent);
                    finish(); // Close the current activity to prevent user from going back
            }
        });

        currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null){
            // Retrieve and set the display name
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                name.setText(displayName);
            } else {
                name.setText("No display name set");
            }
        } else {
            // If no user is logged in, redirect to login screen or show a message
            Toast.makeText(AdminMainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login activity if necessary
        }

        // Get the TextView reference
        TextView dateView = findViewById(R.id.dateView);

        // Get the current date
        Date currentDate = new Date();

        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Set the formatted date to the TextView
        dateView.setText(formattedDate);

        approval = findViewById(R.id.approval);
        approval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, ManageRequest.class);
                startActivity(intent);
                finish();
            }
        });

        adminHistory = findViewById(R.id.adminHistory);
        adminHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AdminHistory.class);
                startActivity(intent);
                finish();
            }
        });
        scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        });

        announcement = findViewById(R.id.adminAnnouncement);
        announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, Announcement.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            String scannedUID = data.getStringExtra("SCAN_RESULT");
            if (scannedUID != null) {
                retrieveAndCheckInVisitor(scannedUID);
            } else {
                Toast.makeText(this, "No QR code scanned", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void retrieveAndCheckInVisitor(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("visitors").whereEqualTo("id", id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming only one document per UID
                            Visitor visitor = document.toObject(Visitor.class);

                                Dialog dialog = new Dialog(this);
                                dialog.setContentView(R.layout.popup);

                                TextView visitorName = dialog.findViewById(R.id.visitor_name);
                                TextView visitorContact = dialog.findViewById(R.id.visitor_contact);
                                TextView visitorDate = dialog.findViewById(R.id.visitor_date);
                                TextView visitorTime = dialog.findViewById(R.id.visitor_time);
                                TextView visitorVehicle = dialog.findViewById(R.id.visitor_vehicle);
                                Button checkInButton = dialog.findViewById(R.id.checkIn);
                                Button checkOutButton = dialog.findViewById(R.id.checkOut);

                                visitorName.setText(visitor.getName());
                                visitorContact.setText(visitor.getPhone());
                                visitorDate.setText(visitor.getDate());
                                visitorTime.setText(visitor.getTime());
                                visitorVehicle.setText(visitor.getVehicle());

                                checkInButton.setOnClickListener(v -> {
                                    updateVisitorStatus(document.getId(), "Checked-In");
                                    dialog.dismiss();
                                });

                                checkOutButton.setOnClickListener(v -> {
                                    updateVisitorStatus(document.getId(), "Checked-out");
                                    dialog.dismiss();
                                });

                                dialog.show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to retrieve visitor data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateVisitorStatus(String documentId, String status) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("visitors").document(documentId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Visitor successfully updated", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update visitor", Toast.LENGTH_SHORT).show());
    }
}
