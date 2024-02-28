package com.example.jobhunt;

import static com.example.jobhunt.R.id.img1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfile extends AppCompatActivity {

    ImageView img, userPhoto;
    LinearLayout linearLayout, linearLayout1, linearLayout2, linearLayout3;
    TextView txtresume;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Find views
        img = findViewById(R.id.dot);
        linearLayout = findViewById(R.id.q1);
        linearLayout1 = findViewById(R.id.j1);
        linearLayout2 = findViewById(R.id.r1);
        linearLayout3 = findViewById(img1);
        userPhoto = findViewById(R.id.userPhoto);
        txtresume = findViewById(R.id.txtresume);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        }

        // Set click listeners
        img.setOnClickListener(view -> {
            UpdateResume updateResume = new UpdateResume();
            updateResume.show(getSupportFragmentManager(), updateResume.getTag());
        });

        linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, EducationActivity.class);
            startActivity(intent);
        });

        linearLayout1.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, EducationActivity.class);
            startActivity(intent);
        });

        linearLayout2.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfile.this, ReadyWork.class);
            startActivity(intent);
        });

        linearLayout3.setOnClickListener(view -> {
            // Handle click on user photo if needed
            ImagePicker.with(EditProfile.this)
                    .crop()	    			//Crop image(Optional), Check Customization for more option
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });
        // Load profile image from URL
        loadProfileImageFromURL(userId);
    }
    private void loadProfileImageFromURL(String userId) {
        // Get the reference to the document containing the user data
        DocumentReference userRef = db.collection("users").document(userId);
        // Fetch the document containing the user data
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Get the image URL from the "profileImageUrl" field in the document
                    String imageURL = documentSnapshot.getString("profileImageUrl");
                    String resume = documentSnapshot.getString("resumeFileName");

                    if (imageURL != null && !imageURL.isEmpty()) {
                        // Load the image into userPhoto ImageView using Glide
                        Glide.with(EditProfile.this)
                                .load(imageURL)
                                .placeholder(R.drawable.baseline_warning_24) // Placeholder image while loading
                                .error(R.drawable.baseline_person_24) // Error image if unable to load
                                .into(userPhoto);
                        txtresume.setText(resume);
                    } else {
                        // Handle case where imageURL is null or empty
                        // You can set a default image or display an error message
                        userPhoto.setImageResource(R.drawable.baseline_person_24);
                    }
                } else {
                    // Document does not exist
                    // You can handle this case accordingly
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure to fetch user data
                Log.e("EditProfile", "Error fetching user data", e);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ImagePicker.REQUEST_CODE) {
            // Get the selected image URI
            Uri selectedImageUri = data.getData();

            // Upload the selected image to Firebase Storage
            uploadImageToFirebaseStorage(selectedImageUri);
        }
    }

    // Upload image to Firebase Storage
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        if (imageUri != null) {
            // Create a storage reference
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images").child(userId);

            // Upload the image to Firebase Storage
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL of the uploaded image
                        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update the image URL in Firestore
                            updateProfileImageInFirestore(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle error uploading image
                        Toast.makeText(EditProfile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // Update profile image URL in Firestore
    private void updateProfileImageInFirestore(String imageUrl) {
        // Get reference to the user document in Firestore
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);
        // Update the profileImageUrl field with the new image URL
        userRef.update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Image URL updated successfully
                    // Reload the profile image using Glide
                    loadProfileImageFromURL(userId);
                })
                .addOnFailureListener(e -> {
                    // Handle error updating image URL
                    Toast.makeText(EditProfile.this,"Failed to update profile image", Toast.LENGTH_SHORT).show();
                });
    }
}