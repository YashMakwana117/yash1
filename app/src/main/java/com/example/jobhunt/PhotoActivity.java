package com.example.jobhunt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class PhotoActivity extends AppCompatActivity {

    private ImageView img;
    private Uri imageUri;
    private Button upload;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        img=findViewById(R.id.profile_image);

        upload = findViewById(R.id.applyButton);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        currentUser = auth.getCurrentUser();



        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PhotoActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }

        });
        upload.setOnClickListener(v -> uploadImage());
    }
    private void uploadImage() {
        if (imageUri != null && currentUser != null) {
            StorageReference storageRef = storage.getReference().child("profile_images/" + currentUser.getUid());
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, get the download URL
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            // Update the user data with the image URL
                            updateUserProfileImage(imageUrl);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        Toast.makeText(PhotoActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(PhotoActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateUserProfileImage(String imageUrl) {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Update the user document in Firestore with the image URL
            DocumentReference userDocRef = db.collection("users").document(userId);
            Map<String, Object> updates = new HashMap<>();
            updates.put("profileImageUrl", imageUrl);

            userDocRef.update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // Document updated successfully
                        Toast.makeText(PhotoActivity.this, "Image uploaded and user updated", Toast.LENGTH_SHORT).show();
                        Intent in= new Intent(PhotoActivity.this,ResumeActivity.class);
                        startActivity(in);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Handle failures
                        Toast.makeText(PhotoActivity.this, "Failed to update user", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == com.github.dhaval2404.imagepicker.ImagePicker.REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                imageUri = data.getData(); // Update the imageUri field
                img.setImageURI(imageUri);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Image Selection is Failed", Toast.LENGTH_SHORT).show();
        }
    }


}