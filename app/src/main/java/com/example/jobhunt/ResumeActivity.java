package com.example.jobhunt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;

public class ResumeActivity extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;

    private ImageView imgResume;
    private TextView txtResume;
    Button next;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume);

        imgResume = findViewById(R.id.imgresume);
        txtResume = findViewById(R.id.txtresume);
        next = findViewById(R.id.next);



        Button choosePdfButton = findViewById(R.id.choosePdfButton);
        choosePdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPdfFile();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ResumeActivity.this,MainActivity.class);
                startActivity(in);
            }
        });

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Always display the file name, if available, or "Upload Resume" if not
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userDocRef = db.collection("users").document(userId);
            userDocRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // User document exists, check if resume URL is available
                    String fileName = documentSnapshot.getString("resumeFileName");
                    updateResumeText(fileName);
                } else {
                    // User document does not exist, show "Upload Resume"
                    updateResumeText("Upload Resume");
                }
            }).addOnFailureListener(e -> {
                showToast("Failed to check resume URL: " + e.getMessage());
            });
        }
    }

        private void pickPdfFile() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            startActivityForResult(intent, PICK_PDF_REQUEST);
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Uri pdfUri = data.getData();
                String fileName = getFileName(pdfUri);

                // Upload PDF to Firebase Storage and store its reference in Firestore
                uploadResumeToFirebaseStorage(pdfUri, fileName);
            }
        }

        // Add the rest of your existing onActivityResult logic here...
    }


    @SuppressLint("Range")
    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return result;
    }

    private void updateResumeText(String fileName) {
        txtResume.setText(fileName);
        showToast("Selected PDF file: " + fileName);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void uploadResumeToFirebaseStorage(Uri pdfUri, String fileName) {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create a unique file name in Firebase Storage
            String storageFileName = "resumes/" + userId + "/" + fileName;

            // Create a Storage reference
            StorageReference storageRef = storage.getReference().child(storageFileName);

            try {
                // Open an InputStream from the selected PDF file
                InputStream stream = getContentResolver().openInputStream(pdfUri);

                // Upload PDF file to Firebase Storage
                UploadTask uploadTask = storageRef.putStream(stream);

                // Monitor the upload task
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // File uploaded successfully
                    // Get the download URL of the uploaded file
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Store the download URL and file name in Firestore or perform other actions
                        updateResumeText(fileName);
                        storeDownloadUrlInFirestore(uri.toString(), fileName);
                        showToast("Resume uploaded successfully!");
                    }).addOnFailureListener(e -> {
                        showToast("Failed to get download URL: " + e.getMessage());
                    });
                }).addOnFailureListener(e -> {
                    showToast("Failed to upload resume: " + e.getMessage());
                });
            } catch (Exception e) {
                showToast("Error: " + e.getMessage());
            }
        }
    }


    private void storeDownloadUrlInFirestore(String downloadUrl, String fileName) {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Create or update a document in Firestore with the download URL and file name
            DocumentReference userDocRef = db.collection("users").document(userId);
            userDocRef.update("resumeUrl", downloadUrl, "resumeFileName", fileName)
                    .addOnSuccessListener(aVoid -> {
                        // Document updated successfully
                        showToast("Resume URL and file name stored in Firestore!");
                    })
                    .addOnFailureListener(e -> {
                        showToast("Failed to store resume URL and file name in Firestore: " + e.getMessage());
                    });
        }
    }


    private String getFileNameFromUrl(String fileUrl) {
        // Extract the file name from the URL
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }


}



