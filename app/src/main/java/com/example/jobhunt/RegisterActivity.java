package com.example.jobhunt;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private EditText dobEditText;
    private Button registerButton;
    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.reg_email);
        passwordEditText = findViewById(R.id.reg_password);
        nameEditText = findViewById(R.id.reg_name);
        dobEditText = findViewById(R.id.reg_dob);
        registerButton = findViewById(R.id.register);
        login = findViewById(R.id.login);

        // Set click listener for the register button
        registerButton.setOnClickListener(v -> registerUser());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String dob = dobEditText.getText().toString().trim();

        // Firebase Authentication: Create user with email and password
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // User registration successful
                            String userId = auth.getCurrentUser().getUid();
                            if (userId != null) {
                                // Firebase Firestore: Save additional user details
                                Map<String, Object> user = new HashMap<>();
                                user.put("name", name);
                                user.put("email", email);
                                user.put("dob", dob);
                                // Add more fields as needed

                                firestore.collection("users").document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid ->
                                                Log.d("RegisterActivity", "User details added to Firestore"))
                                        .addOnFailureListener(e ->
                                                Log.e("RegisterActivity", "Error adding user details to Firestore", e));
                            }
                            Intent in = new Intent(RegisterActivity.this, EducationActivity.class);
//                            Toast.makeText(RegisterActivity.this, "Register Scuscfully", Toast.LENGTH_SHORT).show();
                            startActivity(in);

                            // You can navigate to another activity, show a success message, etc.
                        } else {
                            // If registration fails, display a message to the user.
                            Log.e("RegisterActivity", "User registration failed", task.getException());
                            // You can handle the error appropriately (e.g., show a toast, display an error message)
                        }
                    }
                });
    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view) {
        Log.d("LoginClick", "Login button clicked");
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
