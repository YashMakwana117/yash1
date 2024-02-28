package com.example.jobhunt;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

public class TempActivity extends AppCompatActivity {

    private TextView textView,description,txtworkplace,txttime,txtsalary,txtlocation,txtexpr,txteligibility;
    ImageView imgphoto,imageView;
    Button btnapply;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        textView = findViewById(R.id.temp);
        description = findViewById(R.id.description);
        txtworkplace = findViewById(R.id.workplace);
        txttime = findViewById(R.id.time);
        txtsalary = findViewById(R.id.salary);
        txteligibility = findViewById(R.id.eligibility);
        imgphoto = findViewById(R.id.photo);
        txtlocation = findViewById(R.id.location);
        txtexpr = findViewById(R.id.expr);
        btnapply = findViewById(R.id.apply);
        imageView = findViewById(R.id.imageView);

        String title = getIntent().getStringExtra("title");

        String des = getIntent().getStringExtra("description");
        String photo = getIntent().getStringExtra("img");


        textView.setText(title != null ? title : "No Title");
        description.setText(des != null ? des : "No description");

            Glide.with(this)
                    .load(photo)
                    .placeholder(R.drawable.new1removebg)
                    .error(R.drawable.baseline_warning_24)
                    .into(imgphoto);

        String documentId = getIntent().getStringExtra("cid");
        String id = getIntent().getStringExtra("id");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser().getUid();

        resumename(userId);


        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        db.collection("jobs").document(documentId).get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                // Document exists, extract data and display
//                                String workplace = document.getString("workplace");
//                                String time = document.getString("time");
//                                String salary = document.getString("salary");
//                                String location = document.getString("location");
//                                String expr = document.getString("expr");
//
//                                txtworkplace.setText(workplace != null ? workplace : "No workplace");
//                                txttime.setText(time != null ? time : "No time");
//                                txtsalary.setText(salary != null ? salary : "No salary");
//                                txtlocation.setText(location != null ? location : "No location");
//                                txtexpr.setText(expr != null ? expr : "No expr");
//
//                            } else {
//                                // Document does not exist, display default message
//                                txtworkplace.setText("txtworkplace not found");
//                                txttime.setText("No description txttime");
//                                txtsalary.setText("No description txtsalary");
//                            }
//                        } else {
//                            // Handle errors while fetching document
//                            txtworkplace.setText("Error fetching txtworkplace");
//                            txttime.setText("Error fetching txttime");
//                            txtsalary.setText("No txtsalary available");
//                        }
//                    }
//                });

        db.collection("jobs").document(documentId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            CollectionReference jobCollectionRef = db.collection("jobs").document(documentId).collection("job").document(id).getParent();
                            if (document.exists()) {
                                // Document exists, extract data and display
                                jobCollectionRef.get().addOnCompleteListener(jobTask -> {
                                    if (jobTask.isSuccessful()) {
                                        for (QueryDocumentSnapshot jobDocument : jobTask.getResult()) {
                                            // Extract designation, description, etc. from each job document

                                            String location = jobDocument.getString("location");
                                            String experience = jobDocument.getString("experience");
                                            String salary = jobDocument.getString("salary");
                                            String time = jobDocument.getString("time");
                                            String workplace = jobDocument.getString("workplace");
                                            String eligibility = jobDocument.getString("eligibility");

                                            txtlocation.setText(location != null ? location : "No location");
                                            txtsalary.setText(salary != null ? salary : "No salary");
                                            txtexpr.setText(experience != null ? experience : "No experience");
                                            txtworkplace.setText(workplace != null ? workplace : "No expr");
                                            txttime.setText(time != null ? time : "No time");
                                            txteligibility.setText(eligibility != null ? eligibility : "No eligibility");

                                        }
                                        // Notify adapter after loading all data outside the loop

                                    } else {
                                        // Handle error
                                    }
                                });
                            } else {
                                // Document does not exist, display default message
                                txtworkplace.setText("txtworkplace not found");
                                txttime.setText("No description txttime");
                                txtsalary.setText("No description txtsalary");
                            }
                        } else {
                            // Handle errors while fetching document
                            txtworkplace.setText("Error fetching txtworkplace");
                            txttime.setText("Error fetching txttime");
                            txtsalary.setText("No txtsalary available");
                        }
                    }
                });


        //image view chang
        AlreadyApplied(documentId);
        btnapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfAlreadyApplied(documentId);
            }
        });

    }
    private void AlreadyApplied(String jobId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId)
                    .collection("jobApply").document(jobId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // User has already applied for this job
                    imageView.setImageResource(R.drawable.baseline_bookmark);
                } else {
                    // User has not applied for this job yet
//                    applyForJob(jobId);
                }
            }).addOnFailureListener(e -> {
                // Handle failure
                imageView.setImageResource(R.drawable.baseline_bookmark_border_24);
//                Toast.makeText(this, "Failed to check if already applied", Toast.LENGTH_SHORT).show();
            });
        }
    }
    private void checkIfAlreadyApplied(String jobId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId)
                    .collection("jobApply").document(jobId);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // User has already applied for this job
                    Toast.makeText(this, "You have already applied for this job", Toast.LENGTH_SHORT).show();
                } else {
                    // User has not applied for this job yet
//                    applyForJob(jobId);
                    bottomsheet bottomSheet = bottomsheet.newInstance(jobId);
                    bottomSheet.show(getSupportFragmentManager(),null);
                }
            }).addOnFailureListener(e -> {
                // Handle failure
                Toast.makeText(this, "Failed to check if already applied", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void resumename(String userId) {
        // Get the reference to the document containing the user data
        DocumentReference userRef = db.collection("users").document(userId);
        // Fetch the document containing the user data
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    // Get the image URL from the "profileImageUrl" field in the document
                    String resume = documentSnapshot.getString("resumeFileName");


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


    private void applyForJob(String jobId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId)
                    .collection("jobApply").document(jobId); // Use jobId as the document ID
            // Add the job ID as a field in the document within the "jobApply" subcollection
            userRef.set(new HashMap<String, Object>() {{
                        put("jobId", jobId.toString());
                    }})
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Applied successfully", Toast.LENGTH_SHORT).show();
                        // Document created successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(this, "Failed to apply", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}

