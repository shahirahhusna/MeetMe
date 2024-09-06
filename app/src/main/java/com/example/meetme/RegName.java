package com.example.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegName extends AppCompatActivity {

    EditText name;
    Button login;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reg_name);

        name = findViewById(R.id.name);
        login = findViewById(R.id.loginButton);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setName = name.getText().toString().trim();

                if(TextUtils.isEmpty(setName)){
                    Toast.makeText(RegName.this,"Enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(setName).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegName.this, "Name set successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegName.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegName.this, "Failed to set name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

    }
}