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
//public class SignUpActivity extends AppCompatActivity {
//
//    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
//    private Button btnSignup;
//    private TextView tvSignin;
//    private SharedPreferences sharedPreferences;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_signup);
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
//        etUsername = findViewById(R.id.et_username_signup);
//        etEmail = findViewById(R.id.et_email);
//        etPassword = findViewById(R.id.et_password_signup);
//        etConfirmPassword = findViewById(R.id.et_confirm_password);
//        btnSignup = findViewById(R.id.btn_signup);
//        tvSignin = findViewById(R.id.tv_signin);
//    }
//
//    private void setupClickListeners() {
//        btnSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                performSignup();
//            }
//        });
//
//        tvSignin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish(); // Go back to SignIn activity
//            }
//        });
//    }
//
//    private void performSignup() {
//        String username = etUsername.getText().toString().trim();
//        String email = etEmail.getText().toString().trim();
//        String password = etPassword.getText().toString().trim();
//        String confirmPassword = etConfirmPassword.getText().toString().trim();
//
//        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
//            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!password.equals(confirmPassword)) {
//            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (password.length() < 6) {
//            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Save user data
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("username", username);
//        editor.putString("email", email);
//        editor.putBoolean("isLoggedIn", true);
//        editor.apply();
//
//        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
//
//        // Navigate to main activity
//        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
//        startActivity(intent);
//        finish();
//    }
//}


package com.example.myapplication2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnSignup;
    private TextView tvSignin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username_signup);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password_signup);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
        tvSignin = findViewById(R.id.tv_signin);
    }

    private void setupClickListeners() {
        btnSignup.setOnClickListener(v -> performSignup());

        tvSignin.setOnClickListener(v -> {
            // Open SignIn activity
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void performSignup() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        // Go to MainActivity
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(SignUpActivity.this, "Email already in use", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
