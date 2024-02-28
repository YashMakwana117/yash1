package com.example.jobhunt;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText emailEditText, passwordEditText;
    private TextView register,forgot;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.log_email);
        passwordEditText = findViewById(R.id.log_pass);
        loginButton = findViewById(R.id.btn_login);
        register = findViewById(R.id.reg);
        forgot = findViewById(R.id.Forgot);

        // Set click listener for the login button
        loginButton.setOnClickListener(v -> loginUser());

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, work.class);

                startActivity(in);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(in);
            }
        });

    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Firebase Authentication: Sign in with email and password
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Sign in successful
                            Log.d("LoginActivity", "User login successful");
                            Intent in = new Intent(LoginActivity.this, MainActivity.class);
                            Toast.makeText(LoginActivity.this, "LogIn Scuscfully", Toast.LENGTH_SHORT).show();
                            startActivity(in);

                            // You can navigate to another activity, show a success message, etc.
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("LoginActivity", "User login failed", task.getException());
                            // You can handle the error appropriately (e.g., show a toast, display an error message)
                        }
                    }
                });
    }
    public void onLoginClick(View view)
    {
        startActivity(new Intent(this,RegisterActivity.class));
        overridePendingTransition(R.anim.slide_in_right,R.anim.stay);
    }
}
