package com.example.meetme;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    CardView visitor, history, favorite, reno;
    ImageView profile, announcement;
    TextView name, logout;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        announcement = findViewById(R.id.announcement);
        announcement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserAnnouncement.class);
                startActivity(intent);
                finish();
            }
        });

        profile = findViewById(R.id.profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent);
                finish();
            }
        });
        name = findViewById(R.id.userName);
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    firebaseAuth.signOut();
                    // After signing out, redirect the user to the login or splash screen
                    Intent intent = new Intent(MainActivity.this, LoginPage.class);
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
            Toast.makeText(MainActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
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

        visitor = findViewById(R.id.inviteVisitorCard);
        visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VisitorView.class);
                startActivity(intent);
                finish();
            }
        });

        history = findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CheckHistory.class);
                startActivity(intent);
                finish();
            }
        });

        favorite = findViewById(R.id.favorite);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Favorite.class);
                startActivity(intent);
                finish();
            }
        });

        reno = findViewById(R.id.reno);
        reno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RenoLetter.class);
                startActivity(intent);
                finish();
            }
        });
    }
}