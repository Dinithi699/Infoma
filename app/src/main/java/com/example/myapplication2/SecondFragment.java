package com.example.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SecondFragment extends Fragment {

    private TextView tvUsername, tvEmail;
    private Button btnEditInfo, btnSignOut;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnEditInfo = view.findViewById(R.id.btn_edit_info);
        btnSignOut = view.findViewById(R.id.btn_sign_out);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Load user data from Firebase
        loadUserDataFromFirebase();

        // Set click listeners
        btnEditInfo.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            Toast.makeText(getContext(), "Edit functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnSignOut.setOnClickListener(v -> {
            signOut();
        });
    }

    private void loadUserDataFromFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Get email from Firebase Auth
            String email = currentUser.getEmail();

            // Get display name from Firebase Auth (if set)
            String displayName = currentUser.getDisplayName();

            // If display name is not set, extract username from email
            String username;
            if (displayName != null && !displayName.isEmpty()) {
                username = displayName;
            } else if (email != null) {
                // Extract username from email (part before @)
                username = email.substring(0, email.indexOf('@'));
            } else {
                username = "Guest User";
            }

            // Update UI
            tvUsername.setText(username);
            tvEmail.setText(email != null ? email : "No email available");

            // Optionally save to SharedPreferences for offline access
            saveUserDataToPreferences(username, email);

        } else {
            // No user is signed in, redirect to login
            redirectToLogin();
        }
    }

    private void saveUserDataToPreferences(String username, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("email", email);
        editor.apply();
    }

    private void loadUserDataFromPreferences() {
        // Fallback method to load from SharedPreferences if needed
        String username = sharedPreferences.getString("username", "Guest User");
        String email = sharedPreferences.getString("email", "guest@example.com");

        tvUsername.setText(username);
        tvEmail.setText(email);
    }

    private void signOut() {
        // Sign out from Firebase
        mAuth.signOut();

        // Clear user data from SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Show sign out message
        Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();

        // Navigate back to SignIn activity
        redirectToLogin();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is still signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        }
    }
}