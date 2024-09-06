package com.example.meetme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WorkerReg extends AppCompatActivity {

    EditText name, phone, date, time, vehicle;
    ImageView fav, back;
    Button submit;
    RadioGroup setRepeat;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.worker_reg);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerReg.this, PreRegister.class);
                startActivity(intent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        fav = findViewById(R.id.fav);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.contact);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        vehicle = findViewById(R.id.vehicle);
        submit = findViewById(R.id.submitBtn);
        setRepeat = findViewById(R.id.setRepeat);

        fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Toggle between heart.png and no_heart.png based on isFavorite flag
                if (isFavorite) {
                    fav.setImageResource(R.drawable.no_heart);
                } else {
                    fav.setImageResource(R.drawable.heart);
                }

                // Adjust size of the image
                fav.getLayoutParams().width = (int) getResources().getDimension(R.dimen.fav_image_size);
                fav.getLayoutParams().height = (int) getResources().getDimension(R.dimen.fav_image_size);

                // Toggle the isFavorite flag
                isFavorite = !isFavorite;

            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveVisitor();
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
                        date.setText(String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year));
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }
    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Convert hourOfDay and minute to a 12-hour format with AM/PM
                        boolean isPM = (hourOfDay >= 12);
                        time.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                    }
                },
                hour, minute, false); // Use false for 12-hour time format
        timePickerDialog.show();
    }

    private void saveVisitor() {
        String visitorName = name.getText().toString().trim();
        String visitorPhone = phone.getText().toString().trim();
        String visitDate = date.getText().toString().trim();
        String visitTime = time.getText().toString().trim();
        String visitorVehicle = vehicle.getText().toString().trim();
        int selectedRepeatId = setRepeat.getCheckedRadioButtonId();
        String repeat = "";

        if (selectedRepeatId == R.id.noRepeatBtn) {
            repeat = "No Repeat";
        } else if (selectedRepeatId == R.id.dailyBtn) {
            repeat = "Daily";
        } else if (selectedRepeatId == R.id.weeklyBtn) {
            repeat = "Weekly";
        }

        if (visitorName.isEmpty() || visitorPhone.isEmpty() || visitDate.isEmpty() || visitTime.isEmpty() || visitorVehicle.isEmpty()) {
            Toast.makeText(WorkerReg.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String visitorId = UUID.randomUUID().toString();

        Map<String, Object> visitor = new HashMap<>();
        visitor.put("id", visitorId);
        visitor.put("userId", userId);
        visitor.put("name", visitorName);
        visitor.put("phone", visitorPhone);
        visitor.put("date", visitDate);
        visitor.put("time", visitTime);
        visitor.put("vehicle", visitorVehicle);
        visitor.put("repeat", repeat);
        visitor.put("status", "Pending");
        visitor.put("category", "Worker");

        // Determine status based on isFavorite flag
        String isFav = isFavorite ? "favorite" : "none";
        visitor.put("isFavorite", isFav);

        String finalRepeat = repeat;
        db.collection("visitors").add(visitor).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(WorkerReg.this, "New visitor added successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WorkerReg.this, VisitorDetail.class);
                intent.putExtra("visitorId", visitorId);
                intent.putExtra("name", visitorName);
                intent.putExtra("phone", visitorPhone);
                intent.putExtra("date", visitDate);
                intent.putExtra("time", visitTime);
                intent.putExtra("vehicle", visitorVehicle);
                intent.putExtra("repeat", finalRepeat);
                intent.putExtra("status", "Pending");
                intent.putExtra("isFavorite", isFav);
                intent.putExtra("category", "Worker");
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WorkerReg.this, "Failed to add new visitor", Toast.LENGTH_SHORT).show();
            }
        });


    }

}