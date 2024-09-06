package com.example.meetme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RenoView extends AppCompatActivity {

    private TextView textViewDocumentContent;
    private Button buttonSharePdf, home;
    private ImageView imageViewCondo;
    private TextView textViewCondoName, textViewAddress, textViewContact;
    private String name, renovationType, weeksTaken, unitNumber, contactNumber;
    private long dateBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.reno_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        home = findViewById(R.id.buttonHome);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RenoView.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        textViewDocumentContent = findViewById(R.id.textViewDocumentContent);
        buttonSharePdf = findViewById(R.id.buttonSharePdf);
        imageViewCondo = findViewById(R.id.imageViewCondo);
        textViewCondoName = findViewById(R.id.textViewCondoName);
        textViewAddress = findViewById(R.id.textViewAddress);
        textViewContact = findViewById(R.id.textViewContact);

        // Load image into imageViewCondo
        Picasso.get().load(R.drawable.condo_image).into(imageViewCondo);

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            renovationType = extras.getString("renovationType");
            dateBegin = extras.getLong("dateBegin");
            weeksTaken = extras.getString("weeksTaken");
            name = extras.getString("name");
            unitNumber = extras.getString("unitNumber");
            contactNumber = extras.getString("contactNumber");
        }

        generateDocumentContent();

        buttonSharePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePdf();
            }
        });
    }

    private void generateDocumentContent() {
        Date date = new Date(dateBegin);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = dateFormat.format(date);

        String documentContent = "To,\n" +
                "The Building Manager,\n" +
                "Mior Amir,\n" +
                "Prima Ria Condominium, Dutamas, 51200 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur\n" +
                "\nDate: " + dateString + "\n" +
                "\nSubject: Seeking permission for renovation work\n" +
                "\nRespected Sir/ Madam,\n" +
                "With due respect, I am " + name + " an undersigned owner of unit " + unitNumber + ".\n\n" +
                "I intend to do some renovation work at my home and am respectfully writing to request permission. " +
                "This renovation includes " + renovationType + " and is to begin from " + dateString + ". This will take an approximate time of " + weeksTaken + " weeks.\n" +
                "\nIt is required that you kindly approve this so that we can proceed as soon as possible.\n\n" +
                "Thanking you,\n\n" +
                "Resident Name: " + name + "\n" +
                "Contact Number: " + contactNumber + "\n" +
                "Unit Number: " + unitNumber + "\n\n" +
                "                    Signature is not required as this is a system generated.";

        textViewDocumentContent.setText(documentContent);
    }

    private void sharePdf() {
        try {
            // Create folder and file for the PDF
            File pdfFolder = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "pdfs");
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs();
            }

            String pdfName = "RenovationPermission.pdf";
            File pdfFile = new File(pdfFolder, pdfName);

            // Create a PdfWriter instance
            PdfWriter writer = new PdfWriter(pdfFile);

            // Initialize the PdfDocument
            PdfDocument pdfDocument = new PdfDocument(writer);

            // Initialize the Document
            Document document = new Document(pdfDocument);

            // Set font
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Add content to the PDF
            document.add(new Paragraph("Prima Ria Condominium").setFont(font).setFontSize(18).setBold());
            document.add(new Paragraph("Dutamas, 51200 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur").setFont(font).setFontSize(12));
            document.add(new Paragraph("Office: 03-62501484 | Building Management Manager").setFont(font).setFontSize(12));
            document.add(new Paragraph("\nTo,\nThe Building Manager,\nMior Amir,\n(Address)\nPrima Ria Condominium, Dutamas, 51200 Kuala Lumpur, Wilayah Persekutuan Kuala Lumpur").setFont(font).setFontSize(12));
            document.add(new Paragraph("\nDate: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date())).setFont(font).setFontSize(12));
            document.add(new Paragraph("\nSubject: Seeking permission for renovation work").setFont(font).setFontSize(12).setBold());
            document.add(new Paragraph("\nRespected Sir/ Madam,").setFont(font).setFontSize(12));
            document.add(new Paragraph("\nWith due respect, I am " + name + ", an undersigned owner of unit " + unitNumber + ".").setFont(font).setFontSize(12));
            document.add(new Paragraph("\nI intend to do some renovation work at my home and am respectfully writing to request permission. This renovation includes " + renovationType + " and is to begin from " + new SimpleDateFormat("dd/MM/yyyy").format(new Date(dateBegin)) + ". This will take an approximate time of " + weeksTaken + " weeks.").setFont(font).setFontSize(12));
            document.add(new Paragraph("\nIt is required that you kindly approve this so that we can proceed as soon as possible.").setFont(font).setFontSize(12));
            document.add(new Paragraph("\nThanking you,").setFont(font).setFontSize(12));
            document.add(new Paragraph("\nResident Name: " + name).setFont(font).setFontSize(12));
            document.add(new Paragraph("Contact Number: " + contactNumber).setFont(font).setFontSize(12));
            document.add(new Paragraph("Unit Number: " + unitNumber).setFont(font).setFontSize(12));
            document.add(new Paragraph("\n                    Signature is not required as this is a system generated.").setFont(font).setFontSize(12).setTextAlignment(TextAlignment.CENTER));

            // Close the document
            document.close();

            // Get the Uri for the PDF file
            Uri pdfUri = FileProvider.getUriForFile(this, "com.example.meetme.fileprovider", pdfFile);

            // Create share intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start share activity
            startActivity(Intent.createChooser(shareIntent, "Share PDF"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
