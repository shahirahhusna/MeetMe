package com.example.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminSignUp extends AppCompatActivity {

    EditText regemail, regpass;
    Button register, back;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_sign_up);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSignUp.this, WelcomePage.class);
                startActivity(intent);
                finish();
            }
        });
        regemail = findViewById(R.id.adminEmail);
        regpass = findViewById(R.id.adminPassword);
        register = findViewById(R.id.regButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String registeremail, registerpassword;
                registeremail = String.valueOf(regemail.getText());
                registerpassword = String.valueOf(regpass.getText());

                if (TextUtils.isEmpty(registeremail)) {
                    Toast.makeText(AdminSignUp.this, "Fill Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(registerpassword)) {
                    Toast.makeText(AdminSignUp.this, "Fill Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(registeremail, registerpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminSignUp.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminSignUp.this, AdminRegName.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Sign up failed
                            Log.w("SignupActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AdminSignUp.this, "Signup failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}