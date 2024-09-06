package com.example.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Announcement extends AppCompatActivity {

    private EditText announcementText;
    private CheckBox checkboxAllResidents;
    private Button submitButton;
    ImageView back;

    // Arrays to store buttons and layouts for each level
    private Button[] levelButtons = new Button[9];
    private LinearLayout[] dropdowns = new LinearLayout[9];
    private CheckBox[] selectAllCheckboxes = new CheckBox[9];
    private List<CheckBox>[] unitCheckboxes = new ArrayList[9];

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.announcement);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Announcement.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        announcementText = findViewById(R.id.announcement_text);
        checkboxAllResidents = findViewById(R.id.checkbox_all_residents);
        submitButton = findViewById(R.id.submit_button);

        db = FirebaseFirestore.getInstance();

        // Initialize level buttons, dropdowns, and checkboxes
        for (int i = 0; i < 9; i++) {
            int level = i + 1;
            int buttonId = getResources().getIdentifier("button_level_" + level, "id", getPackageName());
            int dropdownId = getResources().getIdentifier("dropdown_level_" + level, "id", getPackageName());
            int selectAllId = getResources().getIdentifier("checkbox_level_" + level + "_all", "id", getPackageName());

            levelButtons[i] = findViewById(buttonId);
            dropdowns[i] = findViewById(dropdownId);
            selectAllCheckboxes[i] = findViewById(selectAllId);

            unitCheckboxes[i] = new ArrayList<>();
            for (int j = 1; j <= 9; j++) {
                int checkboxId = getResources().getIdentifier("checkbox_" + level + "_" + j, "id", getPackageName());
                unitCheckboxes[i].add(findViewById(checkboxId));
            }

            // Set up click listeners for level buttons
            final int index = i;
            levelButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleDropdown(index);
                }
            });

            // Set up select all checkbox listener
            selectAllCheckboxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (CheckBox checkBox : unitCheckboxes[index]) {
                        checkBox.setChecked(isChecked);
                    }
                }
            });

        }

        // Set up submit button listener
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnnouncement();
            }
        });
    }

    private void toggleDropdown(int index) {
        if (dropdowns[index].getVisibility() == View.GONE) {
            dropdowns[index].setVisibility(View.VISIBLE);
        } else {
            dropdowns[index].setVisibility(View.GONE);
        }
    }

    private void submitAnnouncement() {
        String announcement = announcementText.getText().toString().trim();
        if (announcement.isEmpty()) {
            Toast.makeText(this, "Please enter an announcement", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> announcementData = new HashMap<>();
        announcementData.put("announcement", announcement);
        announcementData.put("timestamp", FieldValue.serverTimestamp());

        List<String> targetUnits = new ArrayList<>();
        if (checkboxAllResidents.isChecked()) {
            targetUnits.add("all");
        }

        for (int i = 0; i < 9; i++) {
            for (CheckBox checkBox : unitCheckboxes[i]) {
                if (checkBox.isChecked()) {
                    targetUnits.add(checkBox.getText().toString());
                }
            }
        }

        announcementData.put("targetUnits", targetUnits);

        db.collection("announcements")
                .add(announcementData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(Announcement.this, "Announcement submitted", Toast.LENGTH_SHORT).show();
                        announcementText.setText("");
                        checkboxAllResidents.setChecked(false);
                        for (int i = 0; i < 9; i++) {
                            selectAllCheckboxes[i].setChecked(false);
                            for (CheckBox checkBox : unitCheckboxes[i]) {
                                checkBox.setChecked(false);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Announcement.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}