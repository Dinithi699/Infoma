package com.example.myapplication2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UserInfoFragment extends Fragment {

    private TextView usernameField;
    private TextView emailField;
    private Button editInfoButton;
    private Button signOutButton;

    // Default user info (you can replace this with actual user data)
    private String currentUsername = "johndoe123";
    private String currentEmail = "john.doe@example.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.userinfo_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        usernameField = view.findViewById(R.id.username_field);
        emailField = view.findViewById(R.id.email_field);
        editInfoButton = view.findViewById(R.id.edit_info_button);
        signOutButton = view.findViewById(R.id.sign_out_button);

        // Set initial user information
        updateUserInfo();

        // Set button listeners
        if (editInfoButton != null) {
            editInfoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditDialog();
                }
            });
        }

        if (signOutButton != null) {
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignOutDialog();
                }
            });
        }
    }

    private void updateUserInfo() {
        if (usernameField != null) {
            usernameField.setText(currentUsername);
        }
        if (emailField != null) {
            emailField.setText(currentEmail);
        }
    }

    private void showEditDialog() {
        if (getContext() == null) return;

        // Create dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_edit_user_info, null);

        // Find dialog views
        EditText usernameEditText = dialogView.findViewById(R.id.edit_username);
        EditText emailEditText = dialogView.findViewById(R.id.edit_email);

        // Set current values
        if (usernameEditText != null) {
            usernameEditText.setText(currentUsername);
        }
        if (emailEditText != null) {
            emailEditText.setText(currentEmail);
        }

        // Create and show dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit User Information")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get new values
                    String newUsername = usernameEditText != null ? usernameEditText.getText().toString().trim() : "";
                    String newEmail = emailEditText != null ? emailEditText.getText().toString().trim() : "";

                    // Validate input
                    if (validateInput(newUsername, newEmail)) {
                        // Update user info
                        currentUsername = newUsername;
                        currentEmail = newEmail;
                        updateUserInfo();

                        Toast.makeText(getContext(), "User information updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showSignOutDialog() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Handle sign out action
                    Toast.makeText(getContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();

                    // Navigate back to news fragment or login screen
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadNewsFragment();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private boolean validateInput(String username, String email) {
        if (username.isEmpty()) {
            Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (email.isEmpty()) {
            Toast.makeText(getContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!isValidEmail(email)) {
            Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".") && email.length() > 5;
    }
}