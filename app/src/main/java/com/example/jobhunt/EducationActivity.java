package com.example.jobhunt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EducationActivity extends AppCompatActivity {

    String[] courses,specItems;
    private Button next;
    private PopupWindow popupWindow;
    private EditText etSelectedMonthYear;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteTextView spec1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();
        next = findViewById(R.id.applyButton);



        etSelectedMonthYear = findViewById(R.id.etSelectedMonthYear);
        autoCompleteTextView = findViewById(R.id.dropdownAutoCompleteTextView);
        autoCompleteTextView.setKeyListener(null); // Disable keyboard input
        fetchItemsFromFirestore();

        autoCompleteTextView.setOnClickListener(v -> showDropdown(autoCompleteTextView, courses));

        spec1 = findViewById(R.id.special);
        spec1.setKeyListener(null); // Disable keyboard input

        spec1.setOnClickListener(v -> showDropdown(spec1, specItems));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDataToFirestore();
            }
        });
    }

    private void showDropdown(final AutoCompleteTextView autoCompleteTextView, String[] dropdownItems) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.costum_dropdown, null);
        ListView listView = popupView.findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_dropdown_item, dropdownItems);
        listView.setAdapter(adapter);

        popupWindow = new PopupWindow(popupView, autoCompleteTextView.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.showAsDropDown(autoCompleteTextView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            autoCompleteTextView.setText(dropdownItems[position]);
            popupWindow.dismiss();
        });

        popupWindow.setOnDismissListener(() -> autoCompleteTextView.clearFocus());
    }

    public void showMonthYearPicker(View view) {
        // Create a MaterialDatePicker.Builder for Month and Year
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Month and Year")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .build();

        // Set up listener to get the selected date
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Convert the selected timestamp to a Calendar object
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selection);

                // Format the date as needed (Month and Year)
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String formattedDate = sdf.format(calendar.getTime());

                // Update the EditText with the selected month and year
                etSelectedMonthYear.setText(formattedDate);
            }
        });

        // Show the date picker dialog
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
    }
    private void fetchItemsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("data").document("courses")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> itemList = new ArrayList<>();
                        if (task.getResult() != null && task.getResult().exists()) {
                            // Assuming the fields are stored as a map
                            Map<String, Object> data = task.getResult().getData();
                            if (data != null) {
                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                    String fieldValue = entry.getValue().toString();
                                    itemList.add(fieldValue);
                                    Log.d("item adds", "iteam add" + fieldValue);
                                }
                                Log.d("item adds", "iteam add");
                            }
                        }

                        // Convert the list to an array
                        courses = itemList.toArray(new String[0]);
                    } else {
                        // Handle failures
                        // Log.e(TAG, "Error getting document: ", task.getException());
                    }
                });
        db.collection("data").document("specialization")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> itemList = new ArrayList<>();
                        if (task.getResult() != null && task.getResult().exists()) {
                            // Assuming the fields are stored as a map
                            Map<String, Object> data = task.getResult().getData();
                            if (data != null) {
                                for (Map.Entry<String, Object> entry : data.entrySet()) {
                                    String fieldValue = entry.getValue().toString();
                                    itemList.add(fieldValue);
                                    Log.d("item adds", "iteam add" + fieldValue);
                                }
                                Log.d("item adds", "iteam add");
                            }
                        }

                        // Convert the list to an array
                        specItems = itemList.toArray(new String[0]);
                    } else {
                        // Handle failures
                        // Log.e(TAG, "Error getting document: ", task.getException());
                    }
                });
    }
    private void uploadDataToFirestore() {
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Get values from your UI elements
            String selectedCourse = autoCompleteTextView.getText().toString();
            String selectedSpecialization = spec1.getText().toString();
            String selectedMonthYear = etSelectedMonthYear.getText().toString();

            // Create or update a document in Firestore with the user's education data
            DocumentReference userDocRef = db.collection("users").document(userId);
            userDocRef.update(
                    "course", selectedCourse,
                    "specialization", selectedSpecialization,
                    "monthYear", selectedMonthYear
            ).addOnSuccessListener(aVoid -> {
                Intent in = new Intent(EducationActivity.this, InterstActivity.class);
                startActivity(in);
                // Document updated successfully
                // You can add more logic here or show a success message
            }).addOnFailureListener(e -> {
                // Handle failures
                // Log.e(TAG, "Error updating document: ", e);
            });
        }
    }
}