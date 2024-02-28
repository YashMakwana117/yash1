package com.example.jobhunt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteTextView;
    private ChipGroup chipGroup;
    private Button applyButton;
    private List<String> selectedChips = new ArrayList<>();
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interst);

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        chipGroup = findViewById(R.id.chipGroup);
        applyButton = findViewById(R.id.applyButton);

        String longCsvData = "1,Software Developer\\n2,Data Scientist\\n3,Web Developer\\n4,Mobile App Developer\\n5,UI/UX Designer\\n6,Network Engineer\\n7,System Administrator\\n8,Database Administrator\\n9,DevOps Engineer\\n10,Cybersecurity Analyst\\n11,Cloud Architect\\n12,IT Project Manager\\n13,Business Analyst\\n14,Quality Assurance Engineer\\n15,Technical Support Specialist\\n16,Marketing Specialist\\n17,Financial Analyst\\n18,Human Resources Manager\\n19,Sales Representative\\n20,Accountant\\n21,Operations Manager\\n22,Project Manager\\n23,Event Planner\\n24,Executive Assistant\\n25,Customer Service Representative\\n26,Graphic Designer\\n27,Content Writer\\n28,Social Media Manager\\n29,Market Research Analyst\\n30,Logistics Coordinator\\n31,Supply Chain Manager\\n32,Data Entry Operator\\n33,Transcriptionist\\n34,Typist\\n35,Office Manager\n";
        List<String> allChips = getAllChipsFromDatabase(longCsvData);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, allChips);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedChip = (String) parent.getItemAtPosition(position);
            addChip(selectedChip);
            autoCompleteTextView.setText("");
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle apply button click
                Toast.makeText(InterstActivity.this, "Apply button clicked", Toast.LENGTH_SHORT).show();
                storeSelectedChipsInFirestore();
            }
        });
    }

    private List<String> getAllChipsFromDatabase(String csvData) {
        List<String> chips = new ArrayList<>();

        // Split the CSV data into lines
        String[] lines = csvData.split("\\\\n");

        for (String line : lines) {
            // Split each line into values
            String[] values = line.split(",");

            if (values.length >= 2) {
                String id = values[0].trim();
                String name = values[1].trim();
                chips.add(name);
            }
        }

        return chips;
    }

    private void addChip(String chipText) {
        Chip chip = new Chip(this);
        chip.setText(chipText);
        chip.setCheckable(false);
        chip.setClickable(true);
        chipGroup.addView(chip);

        // Update selectedChips list
        selectedChips.add(chipText);
    }
    private void storeSelectedChipsInFirestore() {
        // Store the selected chips in Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        DocumentReference userDocRef = db.collection("users").document(userId); // Replace "user_id" with the actual user ID

        Map<String, Object> data = new HashMap<>();
        userDocRef.update("selectedChips", selectedChips)
                .addOnSuccessListener(aVoid -> {
            Intent in = new Intent(InterstActivity.this, work.class);
            in.putExtra("id",userId);
            startActivity(in);
            // Document updated successfully
            // You can add more logic here or show a success message
        }).addOnFailureListener(e -> {
            // Handle failures
            // Log.e(TAG, "Error updating document: ", e);
        });


    }
}
