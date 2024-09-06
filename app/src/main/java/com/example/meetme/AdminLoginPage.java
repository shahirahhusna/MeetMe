package com.example.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLoginPage extends AppCompatActivity {

    Button login, back;
    TextView create;
    EditText useremail, userpassword;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.admin_login_page);

        back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLoginPage.this, WelcomePage.class);
                startActivity(intent);
                finish();
            }
        });
        useremail = findViewById(R.id.adminEmail);
        userpassword = findViewById(R.id.adminPassword);
        create = findViewById(R.id.createAccount);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminLoginPage.this, AdminSignUp.class);
                startActivity(intent);
                finish();
            }
        });

        login = findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = String.valueOf(useremail.getText());
                password = String.valueOf(userpassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(AdminLoginPage.this, "Fill Email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(AdminLoginPage.this, "Fill Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AdminLoginPage.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminLoginPage.this, AdminMainActivity.class);
                            startActivity(intent);
                            finish();
                        } else  {
                            Toast.makeText(AdminLoginPage.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}