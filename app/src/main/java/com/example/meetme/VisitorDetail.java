package com.example.meetme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class VisitorDetail extends AppCompatActivity {

    TextView name, phone, date, time, vehicle;
    ImageView visitorQr;
    Button share, home;
    String visitorId, visitorName, visitorPhone, visitDate, visitTime, visitorVehicle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitor_detail);

        visitorQr = findViewById(R.id.visitorQr);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.contact);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        vehicle = findViewById(R.id.vehicle);
        home = findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VisitorDetail.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        share = findViewById(R.id.shareQrBtn);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
            }
        });

        // Retrieve data from intent
        Intent intent = getIntent();
        visitorId = intent.getStringExtra("visitorId");
        visitorName = intent.getStringExtra("name");
        visitorPhone = intent.getStringExtra("phone");
        visitDate = intent.getStringExtra("date");
        visitTime = intent.getStringExtra("time");
        visitorVehicle = intent.getStringExtra("vehicle");

        // Display data
        name.setText(visitorName);
        phone.setText(visitorPhone);
        date.setText(visitDate);
        time.setText(visitTime);
        vehicle.setText(visitorVehicle);

        // Generate QR
        new GenerateQRCodeTask().execute();
    }

    private Bitmap generateQRCode(String data) {
        // QR code generation
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white));
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class GenerateQRCodeTask extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... voids) {
            String data = visitorId;
            return generateQRCode(data);
        }

        @Override
        protected void onPostExecute(Bitmap qrCodeBitmap) {
            if (qrCodeBitmap != null) {
                visitorQr.setImageBitmap(qrCodeBitmap);
            } else {
                // Handle error generating QR code
                qrCodeBitmap.recycle();
            }
        }
    }

    private void shareQRCode() {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) visitorQr.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs(); // Create the cache directory if it doesn't exist
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // Overwrite existing file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File imagePath = new File(getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, "com.example.meetme.fileprovider", newFile);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));

    }

}
