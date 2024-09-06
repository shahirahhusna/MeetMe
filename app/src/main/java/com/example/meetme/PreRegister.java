package com.example.meetme;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PreRegister extends AppCompatActivity {

    CardView visitor, overnight, contractor, worker;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pre_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PreRegister.this, VisitorView.class);
                startActivity(intent);
                finish();
            }
        });

        visitor = findViewById(R.id.visitorReg);
        visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PreRegister.this, VisitorReg.class);
                startActivity(intent);
                finish();

            }
        });
        overnight = findViewById(R.id.overnightReg);
        overnight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PreRegister.this, OvernightReg.class);
                startActivity(intent);
                finish();


            }
        });
        contractor = findViewById(R.id.contractorReg);
        contractor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PreRegister.this, ContractorReg.class);
                startActivity(intent);
                finish();

            }
        });
        worker = findViewById(R.id.workerReg);
        worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PreRegister.this, WorkerReg.class);
                startActivity(intent);
                finish();

            }
        });

    }
}