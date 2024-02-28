package com.example.jobhunt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class work extends AppCompatActivity {
    Button btn;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        db = FirebaseFirestore.getInstance();

        String documentId = getIntent().getStringExtra("id");
        // Find the views by their IDs
        RelativeLayout ddlLayout = findViewById(R.id.fullTimeLayout); // Correct ID for the RelativeLayout
        CheckBox fullTimeCheckBox = findViewById(R.id.f1);
        // Set the OnCheckedChangeListener for the CheckBox
        fullTimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Change the background drawable of the RelativeLayout based on checkbox state
                int backgroundDrawable = isChecked ? R.drawable.checked_ddl : R.drawable.ddl;
                ddlLayout.setBackgroundResource(backgroundDrawable);
            }
        });
        RelativeLayout partTimeLayout = findViewById(R.id.part);
        CheckBox partTimeCheckBox = findViewById(R.id.f2);
        partTimeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int backgroundDrawable = isChecked ? R.drawable.checked_ddl : R.drawable.ddl;
                partTimeLayout.setBackgroundResource(backgroundDrawable);
            }
        });
        RelativeLayout job = findViewById(R.id.j1);
        CheckBox f3 = findViewById(R.id.f3);
        f3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int backgroundDrawable = isChecked ? R.drawable.checked_ddl : R.drawable.ddl;
                job.setBackgroundResource(backgroundDrawable);
            }
        });
        RelativeLayout job1 = findViewById(R.id.j2);
        CheckBox f4 = findViewById(R.id.f4);
        f4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int backgroundDrawable = isChecked ? R.drawable.checked_ddl : R.drawable.ddl;
                job1.setBackgroundResource(backgroundDrawable);
            }
        });
        RelativeLayout job2 = findViewById(R.id.j3);
        CheckBox f5 = findViewById(R.id.f5);
        f5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int backgroundDrawable = isChecked ? R.drawable.checked_ddl : R.drawable.ddl;
                job2.setBackgroundResource(backgroundDrawable);
            }
        });
        RelativeLayout s1 = findViewById(R.id.s1);
        CheckBox f6 = findViewById(R.id.f6);
        f6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int backgroundDrawable = isChecked ? R.drawable.checked_ddl : R.drawable.ddl;
                s1.setBackgroundResource(backgroundDrawable);
            }
        });
        RelativeLayout s2 = findViewById(R.id.s2);
        CheckBox f7 = findViewById(R.id.f7);
        f7.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int backgroundDrawable = isChecked ? R.drawable.checked_ddl : R.drawable.ddl;
                s2.setBackgroundResource(backgroundDrawable);
            }
        });
        btn = findViewById(R.id.applyButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn = findViewById(R.id.applyButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the function to upload data to Firestore
                uploadDataToFirestore(documentId);
            }
        });
    }


    // Function to upload data to Firestore
    private void uploadDataToFirestore(String documentId) {
        // Get the values of the checkboxes
        boolean fullTimeChecked = ((CheckBox) findViewById(R.id.f1)).isChecked();
        boolean partTimeChecked = ((CheckBox) findViewById(R.id.f2)).isChecked();
        boolean job1Checked = ((CheckBox) findViewById(R.id.f3)).isChecked();
        boolean job2Checked = ((CheckBox) findViewById(R.id.f4)).isChecked();
        boolean job3Checked = ((CheckBox) findViewById(R.id.f5)).isChecked();
        boolean s1Checked = ((CheckBox) findViewById(R.id.f6)).isChecked();
        boolean s2Checked = ((CheckBox) findViewById(R.id.f7)).isChecked();

        // Create a map to store the user's selections
        Map<String, Object> userSelections = new HashMap<>();
        userSelections.put("fullTime", fullTimeChecked);
        userSelections.put("partTime", partTimeChecked);
        userSelections.put("Field Job", job1Checked);
        userSelections.put("Work from Office", job2Checked);
        userSelections.put("Work from Home", job3Checked);
        userSelections.put("Day Shift", s1Checked);
        userSelections.put("Night Shift", s2Checked);

        // Add the user's selections to Firestore
        db.collection("users").document(documentId)
                .update("userSelections",userSelections)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully uploaded
                        Toast.makeText(work.this, "Data uploaded successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(work.this, PhotoActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failures
                        Log.e("work", "Error uploading data", e);
                        Toast.makeText(work.this, "Failed to upload data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



}
