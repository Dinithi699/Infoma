//package com.example.myapplication2;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.appcompat.app.AppCompatActivity;
//
//public class SignInActivity extends AppCompatActivity {
//
//    private EditText etUsername, etPassword;
//    private Button btnLogin;
//    private TextView tvSignup;
//    private SharedPreferences sharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signin);
//
//        // Hide action bar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
//
//        initViews();
//        setupClickListeners();
//
//        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//    }
//
//    private void initViews() {
//        etUsername = findViewById(R.id.et_username);
//        etPassword = findViewById(R.id.et_password);
//        btnLogin = findViewById(R.id.btn_login);
//        tvSignup = findViewById(R.id.tv_signup);
//    }
//
//    private void setupClickListeners() {
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                performLogin();
//            }
//        });
//
//        tvSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void performLogin() {
//        String username = etUsername.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//
//        if (username.isEmpty() || password.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Simple validation - in real app, you'd validate against server/database
//        if (username.length() >= 3 && password.length() >= 6) {
//            // Save user data
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("username", username);
//            editor.putString("email", username + "@example.com");
//            editor.putBoolean("isLoggedIn", true);
//            editor.apply();
//
//            Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
//
//            // Navigate to main activity
//            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
//        }
//    }
//}

package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvSignup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupClickListeners();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email); // Assuming you're using email here
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignup = findViewById(R.id.tv_signup);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            }
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            // Save user data to SharedPreferences for quick access
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            // Save email
                            editor.putString("email", user.getEmail());

                            // Save username (display name or extract from email)
                            String username;
                            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                                username = user.getDisplayName();
                            } else {
                                // Extract username from email (part before @)
                                username = user.getEmail().substring(0, user.getEmail().indexOf('@'));
                            }
                            editor.putString("username", username);

                            editor.apply();
                        }

                        Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                        // Navigate to MainActivity
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Authentication failed: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
