package com.example.meetme;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RenoLetter extends AppCompatActivity {

    private EditText editTextRenovation, editTextWeeks;
    private TextView dateTextView;
    ImageView back;
    private Button buttonSubmit;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reno_letter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RenoLetter.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        editTextRenovation = findViewById(R.id.editTextRenovation);
        dateTextView = findViewById(R.id.dateTextView);
        editTextWeeks = findViewById(R.id.editTextWeeks);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dateTextView.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void submitForm() {
        String renovationType = editTextRenovation.getText().toString();
        String dateString = dateTextView.getText().toString();
        String weeksTaken = editTextWeeks.getText().toString();

        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dateBegin = sdf.parse(dateString);
            calendar.setTime(dateBegin);

            String userId = auth.getCurrentUser().getUid();
            db.collection("UserProfile").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                String name = documentSnapshot.getString("fullName");
                String unitNumber = documentSnapshot.getString("houseUnit");
                String contactNumber = documentSnapshot.getString("contactNumber");

                Intent intent = new Intent(RenoLetter.this, RenoView.class);
                intent.putExtra("renovationType", renovationType);
                intent.putExtra("dateBegin", dateBegin.getTime());
                intent.putExtra("weeksTaken", weeksTaken);
                intent.putExtra("name", name);
                intent.putExtra("unitNumber", unitNumber);
                intent.putExtra("contactNumber", contactNumber);
                startActivity(intent);

                // Save data to Firestore
                Map<String, Object> visitorData = new HashMap<>();
                visitorData.put("residentName", name);
                visitorData.put("dateBegin", dateBegin);
                visitorData.put("weekTaken", weeksTaken);
                visitorData.put("unitNumber", unitNumber);
                visitorData.put("category", "Contractor");
                visitorData.put("status", "Pending Approval");

                db.collection("visitors").add(visitorData).addOnSuccessListener(aVoid -> {
                    // Data added successfully
                }).addOnFailureListener(e -> {
                    // Handle failure
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
