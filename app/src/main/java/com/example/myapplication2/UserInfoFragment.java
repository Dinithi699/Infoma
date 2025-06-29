package com.example.myapplication2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserInfoFragment extends Fragment {

    private static final String TAG = "UserInfoFragment";

    private TextView usernameField;
    private TextView emailField;
    private Button editInfoButton;
    private Button signOutButton;
    private Button backButton;
    private Button deleteAccountButton;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Default fallback values
    private String currentUsername = "Guest User";
    private String currentEmail = "guest@example.com";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.userinfo_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Initialize Firebase Auth
            if (getActivity() instanceof MainActivity) {
                mAuth = ((MainActivity) getActivity()).getFirebaseAuth();
            } else {
                mAuth = FirebaseAuth.getInstance();
            }

            // Ensure we have a current user
            refreshCurrentUser();

            // Initialize views with null checks
            usernameField = view.findViewById(R.id.username_field);
            emailField = view.findViewById(R.id.email_field);
            editInfoButton = view.findViewById(R.id.edit_info_button);
            signOutButton = view.findViewById(R.id.sign_out_button);
            backButton = view.findViewById(R.id.back_button);
            deleteAccountButton = view.findViewById(R.id.delete_account_button);

            // Load user information from Firebase
            loadUserInfoFromFirebase();

            // Set button listeners with null checks
            if (editInfoButton != null) {
                editInfoButton.setOnClickListener(v -> showEditDialog());
            }

            if (signOutButton != null) {
                signOutButton.setOnClickListener(v -> showSignOutDialog());
            }

            if (backButton != null) {
                backButton.setOnClickListener(v -> navigateToHome());
            }

            if (deleteAccountButton != null) {
                deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing user info", e);
            Toast.makeText(getContext(), "Error initializing user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHome() {
        try {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadNewsFragment();
                Toast.makeText(getContext(), "Returning to home", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to home", e);
            Toast.makeText(getContext(), "Error navigating to home: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }






    private void deleteUserAccount(String password) {
        // Refresh current user before deletion
        refreshCurrentUser();

        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(getContext(), "User session expired. Please sign in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Starting account deletion process");

        try {
            // Create credential for re-authentication
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);

            Log.d(TAG, "Re-authenticating for account deletion");

            // Re-authenticate user before deletion
            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Re-authentication successful, proceeding with account deletion");
                        // Re-authentication successful, now delete account
                        currentUser.delete()
                                .addOnSuccessListener(deleteVoid -> {
                                    Log.d(TAG, "Account deleted successfully");

                                    // Clear SharedPreferences
                                    clearUserPreferences();

                                    Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_LONG).show();

                                    // Sign out and navigate to SignInActivity
                                    if (mAuth != null) {
                                        mAuth.signOut();
                                    }

                                    // Navigate to SignInActivity instead of FirstFragment
                                    try {
                                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                        // Finish the current activity
                                        if (getActivity() != null) {
                                            getActivity().finish();
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error navigating to SignInActivity after account deletion", e);
                                        // Fallback: try to navigate to MainActivity and then SignInActivity
                                        if (getActivity() instanceof MainActivity) {
                                            ((MainActivity) getActivity()).navigateToSignIn();
                                        }
                                    }
                                })
                                .addOnFailureListener(deleteException -> {
                                    Log.e(TAG, "Failed to delete account", deleteException);
                                    String errorMessage = deleteException.getMessage();

                                    if (errorMessage != null) {
                                        if (errorMessage.contains("network")) {
                                            errorMessage = "Network error. Please check your connection and try again.";
                                        } else if (errorMessage.contains("requires-recent-login")) {
                                            errorMessage = "Session expired. Please sign out and sign in again.";
                                        } else {
                                            errorMessage = "Failed to delete account: " + errorMessage;
                                        }
                                    } else {
                                        errorMessage = "Failed to delete account. Please try again.";
                                    }

                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(exception -> {
                        Log.e(TAG, "Re-authentication failed for account deletion", exception);
                        String errorMessage = exception.getMessage();

                        // Provide more specific error messages
                        if (errorMessage != null) {
                            if (errorMessage.contains("password")) {
                                errorMessage = "Incorrect password. Please try again.";
                            } else if (errorMessage.contains("network")) {
                                errorMessage = "Network error. Please check your connection.";
                            } else if (errorMessage.contains("too-many-requests")) {
                                errorMessage = "Too many failed attempts. Please try again later.";
                            } else if (errorMessage.contains("user-disabled")) {
                                errorMessage = "This account has been disabled.";
                            } else if (errorMessage.contains("user-not-found")) {
                                errorMessage = "User account not found. Please sign in again.";
                            }
                        } else {
                            errorMessage = "Authentication failed. Please check your password.";
                        }

                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error during account deletion process", e);
            Toast.makeText(getContext(), "Error during account deletion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    // In UserInfoFragment.java - Updated showDeleteAccountDialog method
    private void showDeleteAccountDialog() {
        if (getContext() == null) return;

        try {
            // Create custom layout
            LinearLayout dialogLayout = new LinearLayout(getContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(60, 50, 60, 40);

            // Set the light blue background color
            GradientDrawable dialogBackground = new GradientDrawable();
            dialogBackground.setShape(GradientDrawable.RECTANGLE);
            dialogBackground.setCornerRadius(30f); // Rounded corners
            dialogBackground.setColor(Color.parseColor("#E3F2FD")); // Light blue background
            dialogLayout.setBackground(dialogBackground);

            // Create title
            TextView titleView = new TextView(getContext());
            titleView.setText("⚠️ DELETE ACCOUNT");
            titleView.setTextColor(Color.parseColor("#D32F2F")); // Red for warning
            titleView.setTextSize(16);
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleView.setGravity(android.view.Gravity.CENTER);
            titleView.setPadding(0, 0, 0, 20);

            // Create message
            TextView messageView = new TextView(getContext());
            messageView.setText("This action is PERMANENT and cannot be undone. All your data will be lost forever.\n\nPlease enter your current password to confirm:");
            messageView.setTextColor(Color.parseColor("#1976D2"));
            messageView.setTextSize(14);
            messageView.setGravity(android.view.Gravity.CENTER);
            messageView.setPadding(0, 0, 0, 20);

            // Create password input
            EditText passwordEditText = new EditText(getContext());
            passwordEditText.setHint("Enter your current password");
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setPadding(30, 20, 30, 20);

            // Style password input
            GradientDrawable inputBackground = new GradientDrawable();
            inputBackground.setShape(GradientDrawable.RECTANGLE);
            inputBackground.setCornerRadius(25f);
            inputBackground.setColor(Color.WHITE);
            inputBackground.setStroke(2, Color.parseColor("#1976D2"));
            passwordEditText.setBackground(inputBackground);
            passwordEditText.setTextColor(Color.BLACK);
            passwordEditText.setHintTextColor(Color.GRAY);

            // Add views to layout
            dialogLayout.addView(titleView);
            dialogLayout.addView(messageView);
            dialogLayout.addView(passwordEditText);

            // Create button layout
            LinearLayout buttonLayout = new LinearLayout(getContext());
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setGravity(android.view.Gravity.CENTER);
            buttonLayout.setPadding(0, 30, 0, 0);

            // Delete button
            Button deleteButton = new Button(getContext());
            deleteButton.setText("DELETE");
            deleteButton.setTextColor(Color.WHITE);
            deleteButton.setTextSize(14);
            deleteButton.setTypeface(null, android.graphics.Typeface.BOLD);

            GradientDrawable deleteButtonDrawable = new GradientDrawable();
            deleteButtonDrawable.setShape(GradientDrawable.RECTANGLE);
            deleteButtonDrawable.setCornerRadius(50f);
            deleteButtonDrawable.setColor(Color.parseColor("#D32F2F")); // Red
            deleteButton.setBackground(deleteButtonDrawable);

            // Cancel button
            Button cancelButton = new Button(getContext());
            cancelButton.setText("CANCEL");
            cancelButton.setTextColor(Color.parseColor("#1976D2"));
            cancelButton.setTextSize(14);
            cancelButton.setTypeface(null, android.graphics.Typeface.BOLD);

            GradientDrawable cancelButtonDrawable = new GradientDrawable();
            cancelButtonDrawable.setShape(GradientDrawable.RECTANGLE);
            cancelButtonDrawable.setCornerRadius(50f);
            cancelButtonDrawable.setColor(Color.parseColor("#B3E5FC"));
            cancelButton.setBackground(cancelButtonDrawable);

            // Set button dimensions
            LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(200, 120);
            deleteParams.setMargins(0, 0, 20, 0);
            deleteButton.setLayoutParams(deleteParams);

            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(200, 120);
            cancelParams.setMargins(20, 0, 0, 0);
            cancelButton.setLayoutParams(cancelParams);

            buttonLayout.addView(deleteButton);
            buttonLayout.addView(cancelButton);
            dialogLayout.addView(buttonLayout);

            // Create dialog
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogLayout)
                    .setCancelable(true)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            // Set button listeners
            deleteButton.setOnClickListener(v -> {
                String password = passwordEditText.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(getContext(), "Password is required to delete account", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Deleting account...", Toast.LENGTH_SHORT).show();
                deleteUserAccount(password);
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing delete account dialog", e);
            Toast.makeText(getContext(), "Error showing delete dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshCurrentUser() {
        try {
            if (mAuth != null) {
                currentUser = mAuth.getCurrentUser();
                Log.d(TAG, "Current user: " + (currentUser != null ? currentUser.getUid() : "null"));

                if (currentUser == null) {
                    Log.w(TAG, "No authenticated user found");
                    Toast.makeText(getContext(), "Please sign in to view user information", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error refreshing current user", e);
        }
    }

    private void loadUserInfoFromFirebase() {
        try {
            if (currentUser != null) {
                Log.d(TAG, "Loading user info for: " + currentUser.getUid());

                // Get display name
                String displayName = currentUser.getDisplayName();
                if (displayName != null && !displayName.isEmpty()) {
                    currentUsername = displayName;
                } else {
                    // Extract username from email if display name is not set
                    String email = currentUser.getEmail();
                    if (email != null && email.contains("@")) {
                        currentUsername = email.substring(0, email.indexOf("@"));
                    }
                }

                // Get email
                String email = currentUser.getEmail();
                if (email != null && !email.isEmpty()) {
                    currentEmail = email;
                }

                Log.d(TAG, "Loaded username: " + currentUsername + ", email: " + currentEmail);
            } else {
                Log.w(TAG, "No current user found");
            }

            // Update UI
            updateUserInfo();
        } catch (Exception e) {
            Log.e(TAG, "Error loading user info", e);
            Toast.makeText(getContext(), "Error loading user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfo() {
        try {
            if (usernameField != null) {
                usernameField.setText(currentUsername);
            }
            if (emailField != null) {
                emailField.setText(currentEmail);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating UI", e);
        }
    }

    private void showEditDialog() {
        if (getContext() == null) return;

        try {
            // Create main dialog layout
            LinearLayout dialogLayout = new LinearLayout(getContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(40, 30, 40, 30);

            // Set the light blue background color with rounded corners
            GradientDrawable dialogBackground = new GradientDrawable();
            dialogBackground.setShape(GradientDrawable.RECTANGLE);
            dialogBackground.setCornerRadius(30f); // Rounded corners
            dialogBackground.setColor(Color.parseColor("#E3F2FD")); // Light blue background
            dialogLayout.setBackground(dialogBackground);

            // Create title
            TextView titleView = new TextView(getContext());
            titleView.setText("EDIT USER INFORMATION");
            titleView.setTextColor(Color.parseColor("#1976D2")); // Dark blue text
            titleView.setTextSize(16);
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleView.setGravity(android.view.Gravity.CENTER);
            titleView.setPadding(0, 0, 0, 30);

            // Username section
            TextView usernameLabel = new TextView(getContext());
            usernameLabel.setText("Username:");
            usernameLabel.setTextColor(Color.parseColor("#1976D2"));
            usernameLabel.setTextSize(14);
            usernameLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            usernameLabel.setPadding(10, 0, 0, 8);

            EditText usernameEditText = new EditText(getContext());
            usernameEditText.setText(currentUsername);
            usernameEditText.setHint("Enter username");
            usernameEditText.setPadding(25, 15, 25, 15);
            usernameEditText.setTextColor(Color.BLACK);
            usernameEditText.setHintTextColor(Color.GRAY);

            // Style username input with rounded corners and white background
            GradientDrawable usernameInputBackground = new GradientDrawable();
            usernameInputBackground.setShape(GradientDrawable.RECTANGLE);
            usernameInputBackground.setCornerRadius(25f);
            usernameInputBackground.setColor(Color.WHITE);
            usernameInputBackground.setStroke(2, Color.parseColor("#1976D2"));
            usernameEditText.setBackground(usernameInputBackground);

            // Email section
            TextView emailLabel = new TextView(getContext());
            emailLabel.setText("Email:");
            emailLabel.setTextColor(Color.parseColor("#1976D2"));
            emailLabel.setTextSize(14);
            emailLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            emailLabel.setPadding(10, 20, 0, 8);

            EditText emailEditText = new EditText(getContext());
            emailEditText.setText(currentEmail);
            emailEditText.setHint("Enter email address");
            emailEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            emailEditText.setPadding(25, 15, 25, 15);
            emailEditText.setTextColor(Color.BLACK);
            emailEditText.setHintTextColor(Color.GRAY);

            // Style email input with rounded corners and white background
            GradientDrawable emailInputBackground = new GradientDrawable();
            emailInputBackground.setShape(GradientDrawable.RECTANGLE);
            emailInputBackground.setCornerRadius(25f);
            emailInputBackground.setColor(Color.WHITE);
            emailInputBackground.setStroke(2, Color.parseColor("#1976D2"));
            emailEditText.setBackground(emailInputBackground);

            // Password section
            TextView passwordLabel = new TextView(getContext());
            passwordLabel.setText("Current Password:");
            passwordLabel.setTextColor(Color.parseColor("#1976D2"));
            passwordLabel.setTextSize(14);
            passwordLabel.setTypeface(null, android.graphics.Typeface.BOLD);
            passwordLabel.setPadding(10, 20, 0, 5);

            TextView passwordHint = new TextView(getContext());
            passwordHint.setText("(Required for email changes)");
            passwordHint.setTextColor(Color.parseColor("#666666"));
            passwordHint.setTextSize(12);
            passwordHint.setPadding(10, 0, 0, 8);

            EditText passwordEditText = new EditText(getContext());
            passwordEditText.setHint("Enter your current password");
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setPadding(25, 15, 25, 15);
            passwordEditText.setTextColor(Color.BLACK);
            passwordEditText.setHintTextColor(Color.GRAY);

            // Style password input with rounded corners and white background
            GradientDrawable passwordInputBackground = new GradientDrawable();
            passwordInputBackground.setShape(GradientDrawable.RECTANGLE);
            passwordInputBackground.setCornerRadius(25f);
            passwordInputBackground.setColor(Color.WHITE);
            passwordInputBackground.setStroke(2, Color.parseColor("#1976D2"));
            passwordEditText.setBackground(passwordInputBackground);

            // Create button layout
            LinearLayout buttonLayout = new LinearLayout(getContext());
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setGravity(android.view.Gravity.CENTER);
            buttonLayout.setPadding(0, 30, 0, 0);

            // Save button (like OK in your sign-out dialog)
            Button saveButton = new Button(getContext());
            saveButton.setText("SAVE");
            saveButton.setTextColor(Color.WHITE);
            saveButton.setTextSize(14);
            saveButton.setTypeface(null, android.graphics.Typeface.BOLD);

            // Style Save button with rounded corners and dark blue background
            GradientDrawable saveButtonDrawable = new GradientDrawable();
            saveButtonDrawable.setShape(GradientDrawable.RECTANGLE);
            saveButtonDrawable.setCornerRadius(50f); // Rounded corners
            saveButtonDrawable.setColor(Color.parseColor("#1976D2")); // Dark blue
            saveButton.setBackground(saveButtonDrawable);

            // Cancel button
            Button cancelButton = new Button(getContext());
            cancelButton.setText("CANCEL");
            cancelButton.setTextColor(Color.parseColor("#1976D2"));
            cancelButton.setTextSize(14);
            cancelButton.setTypeface(null, android.graphics.Typeface.BOLD);

            // Style Cancel button with rounded corners and light blue background
            GradientDrawable cancelButtonDrawable = new GradientDrawable();
            cancelButtonDrawable.setShape(GradientDrawable.RECTANGLE);
            cancelButtonDrawable.setCornerRadius(50f); // Rounded corners
            cancelButtonDrawable.setColor(Color.parseColor("#B3E5FC")); // Light blue
            cancelButton.setBackground(cancelButtonDrawable);

            // Set button dimensions and margins (same as your sign-out dialog)
            LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(200, 120);
            saveParams.setMargins(0, 0, 20, 0);
            saveButton.setLayoutParams(saveParams);

            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(200, 120);
            cancelParams.setMargins(20, 0, 0, 0);
            cancelButton.setLayoutParams(cancelParams);

            // Add all views to dialog layout
            dialogLayout.addView(titleView);
            dialogLayout.addView(usernameLabel);
            dialogLayout.addView(usernameEditText);
            dialogLayout.addView(emailLabel);
            dialogLayout.addView(emailEditText);
            dialogLayout.addView(passwordLabel);
            dialogLayout.addView(passwordHint);
            dialogLayout.addView(passwordEditText);

            // Add buttons to button layout
            buttonLayout.addView(saveButton);
            buttonLayout.addView(cancelButton);
            dialogLayout.addView(buttonLayout);

            // Create dialog
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogLayout)
                    .setCancelable(true)
                    .create();

            // Set dialog window background to be transparent (so our custom background shows)
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            // Set button click listeners
            saveButton.setOnClickListener(v -> {
                try {
                    // Get new values
                    String newUsername = usernameEditText.getText().toString().trim();
                    String newEmail = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString();

                    Log.d(TAG, "Attempting to update - Username: " + newUsername + ", Email: " + newEmail);

                    // Show progress message
                    Toast.makeText(getContext(), "Updating user information...", Toast.LENGTH_SHORT).show();

                    // Validate inputs
                    if (validateUsername(newUsername) && validateEmail(newEmail)) {
                        // Check if email changed
                        if (!newEmail.equals(currentEmail)) {
                            Log.d(TAG, "Email changed from " + currentEmail + " to " + newEmail);
                            // Email changed, need re-authentication
                            if (password.isEmpty()) {
                                Toast.makeText(getContext(), "Password is required to change email", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            updateUserInfoWithReauth(newUsername, newEmail, password);
                        } else {
                            Log.d(TAG, "Only username changed");
                            // Only username changed
                            updateDisplayNameInFirebase(newUsername);
                        }
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, "Error saving changes", e);
                    Toast.makeText(getContext(), "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, "Error opening edit dialog", e);
            Toast.makeText(getContext(), "Error opening edit dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDisplayNameInFirebase(String newDisplayName) {
        // Refresh current user reference
        refreshCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User session expired. Please sign in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Updating display name to: " + newDisplayName);

        try {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newDisplayName)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        try {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Display name updated successfully");
                                currentUsername = newDisplayName;
                                updateUserInfo();
                                Toast.makeText(getContext(), "Username updated successfully!", Toast.LENGTH_SHORT).show();

                                // Refresh user data after update
                                currentUser.reload().addOnCompleteListener(reloadTask -> {
                                    if (reloadTask.isSuccessful()) {
                                        currentUser = mAuth.getCurrentUser();
                                        loadUserInfoFromFirebase();
                                    }
                                });
                            } else {
                                String errorMessage = task.getException() != null ?
                                        task.getException().getMessage() : "Unknown error";
                                Log.e(TAG, "Failed to update display name: " + errorMessage);
                                Toast.makeText(getContext(), "Failed to update display name: " + errorMessage,
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error in display name update callback", e);
                            Toast.makeText(getContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error updating Firebase profile", e);
            Toast.makeText(getContext(), "Error updating Firebase profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInfoWithReauth(String newUsername, String newEmail, String password) {
        // Refresh current user before re-authentication
        refreshCurrentUser();

        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(getContext(), "User session expired. Please sign in again.", Toast.LENGTH_SHORT).show();
            // Navigate to login screen if available
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadNewsFragment();
            }
            return;
        }

        Log.d(TAG, "Starting re-authentication for email update");
        Log.d(TAG, "Current user email: " + currentUser.getEmail());

        try {
            // Create credential for re-authentication
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);

            Log.d(TAG, "Re-authenticating with email: " + currentUser.getEmail());

            // Re-authenticate user
            currentUser.reauthenticate(credential)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Re-authentication successful");
                        // Refresh user reference after re-authentication
                        currentUser = mAuth.getCurrentUser();
                        // Re-authentication successful, now update email
                        updateEmailInFirebase(newUsername, newEmail);
                    })
                    .addOnFailureListener(exception -> {
                        Log.e(TAG, "Re-authentication failed", exception);
                        String errorMessage = exception.getMessage();

                        // Provide more specific error messages
                        if (errorMessage != null) {
                            if (errorMessage.contains("password")) {
                                errorMessage = "Incorrect password. Please try again.";
                            } else if (errorMessage.contains("network")) {
                                errorMessage = "Network error. Please check your connection.";
                            } else if (errorMessage.contains("too-many-requests")) {
                                errorMessage = "Too many failed attempts. Please try again later.";
                            } else if (errorMessage.contains("user-disabled")) {
                                errorMessage = "This account has been disabled.";
                            } else if (errorMessage.contains("user-not-found")) {
                                errorMessage = "User account not found. Please sign in again.";
                            }
                        } else {
                            errorMessage = "Authentication failed. Please check your password.";
                        }

                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error during re-authentication setup", e);
            Toast.makeText(getContext(), "Error during re-authentication: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmailInFirebase(String newUsername, String newEmail) {
        // Refresh current user reference
        refreshCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "User session expired. Please sign in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Updating email to: " + newEmail);

        try {
            // Update email directly in Firebase without verification
            currentUser.updateEmail(newEmail)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Email updated successfully");
                        // Email updated successfully, now update display name
                        currentEmail = newEmail;
                        updateDisplayNameInFirebase(newUsername);
                        Toast.makeText(getContext(), "Email updated successfully!", Toast.LENGTH_SHORT).show();

                        // Refresh user data after update
                        currentUser.reload().addOnCompleteListener(reloadTask -> {
                            if (reloadTask.isSuccessful()) {
                                Log.d(TAG, "User data reloaded successfully");
                                currentUser = mAuth.getCurrentUser();
                                loadUserInfoFromFirebase();
                            } else {
                                Log.e(TAG, "Failed to reload user data");
                            }
                        });
                    })
                    .addOnFailureListener(exception -> {
                        Log.e(TAG, "Failed to update email", exception);
                        String errorMessage = exception.getMessage();

                        // Provide specific error messages
                        if (errorMessage != null) {
                            if (errorMessage.contains("already-in-use")) {
                                errorMessage = "This email is already in use by another account.";
                            } else if (errorMessage.contains("invalid-email")) {
                                errorMessage = "Invalid email format.";
                            } else if (errorMessage.contains("requires-recent-login")) {
                                errorMessage = "Session expired. Please sign out and sign in again.";
                            } else if (errorMessage.contains("network")) {
                                errorMessage = "Network error. Please check your connection.";
                            } else if (errorMessage.contains("operation-not-allowed")) {
                                errorMessage = "Email updates are not enabled. Please contact support.";
                            }
                        } else {
                            errorMessage = "Failed to update email. Please try again.";
                        }

                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "Error updating email", e);
            Toast.makeText(getContext(), "Error updating email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // In UserInfoFragment.java - Replace the showSignOutDialog method with this updated version

    private void showSignOutDialog() {
        if (getContext() == null) return;

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // Create custom layout
            LinearLayout dialogLayout = new LinearLayout(getContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(60, 50, 60, 40);

            // Set the light blue background color like in your image
            dialogLayout.setBackgroundColor(Color.parseColor("#E3F2FD")); // Light blue background

            // Create and style the title
            TextView titleView = new TextView(getContext());
            titleView.setText("DO YOU WANT TO SIGN OUT ?");
            titleView.setTextColor(Color.parseColor("#1976D2")); // Dark blue text
            titleView.setTextSize(16);
            titleView.setTypeface(null, android.graphics.Typeface.BOLD);
            titleView.setGravity(android.view.Gravity.CENTER);
            titleView.setPadding(0, 0, 0, 150);

            dialogLayout.addView(titleView);

            // Create button layout
            LinearLayout buttonLayout = new LinearLayout(getContext());
            buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
            buttonLayout.setGravity(android.view.Gravity.CENTER);

            // Create OK button (like in your image)
            Button okButton = new Button(getContext());
            okButton.setText("YES");
            okButton.setTextColor(Color.WHITE);
            okButton.setTextSize(14);
            okButton.setTypeface(null, android.graphics.Typeface.BOLD);

            // Style OK button with rounded corners and blue background
            GradientDrawable okButtonDrawable = new GradientDrawable();
            okButtonDrawable.setShape(GradientDrawable.RECTANGLE);
            okButtonDrawable.setCornerRadius(50f); // Rounded corners
            okButtonDrawable.setColor(Color.parseColor("#1976D2")); // Dark blue
            okButton.setBackground(okButtonDrawable);

            // Create CANCEL button
            Button cancelButton = new Button(getContext());
            cancelButton.setText("NO");
            cancelButton.setTextColor(Color.parseColor("#1976D2"));
            cancelButton.setTextSize(14);
            cancelButton.setTypeface(null, android.graphics.Typeface.BOLD);

            // Style CANCEL button with rounded corners and light blue background
            GradientDrawable cancelButtonDrawable = new GradientDrawable();
            cancelButtonDrawable.setShape(GradientDrawable.RECTANGLE);
            cancelButtonDrawable.setCornerRadius(50f); // Rounded corners
            cancelButtonDrawable.setColor(Color.parseColor("#B3E5FC")); // Light blue
            cancelButton.setBackground(cancelButtonDrawable);

            // Set button dimensions and margins
            LinearLayout.LayoutParams okParams = new LinearLayout.LayoutParams(200, 120);
            okParams.setMargins(0, 0, 20, 0);
            okButton.setLayoutParams(okParams);

            LinearLayout.LayoutParams cancelParams = new LinearLayout.LayoutParams(200, 120);
            cancelParams.setMargins(20, 0, 0, 0);
            cancelButton.setLayoutParams(cancelParams);

            // Add buttons to button layout
            buttonLayout.addView(okButton);
            buttonLayout.addView(cancelButton);

            // Add button layout to main dialog layout
            dialogLayout.addView(buttonLayout);

            // Create dialog
            AlertDialog dialog = new AlertDialog.Builder(getContext())
                    .setView(dialogLayout)
                    .setCancelable(true)
                    .create();

            // Set dialog window background to be rounded
            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // Create rounded background for the entire dialog
                GradientDrawable dialogBackground = new GradientDrawable();
                dialogBackground.setShape(GradientDrawable.RECTANGLE);
                dialogBackground.setCornerRadius(30f); // Rounded corners for dialog
                dialogBackground.setColor(Color.parseColor("#E3F2FD"));
                dialogLayout.setBackground(dialogBackground);
            }

            // Set button click listeners
            okButton.setOnClickListener(v -> {
                try {
                    // Handle sign out action
                    if (mAuth != null) {
                        mAuth.signOut();
                        clearUserPreferences();
                        Toast.makeText(getContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, "Error signing out", e);
                    Toast.makeText(getContext(), "Error signing out: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());

            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing sign out dialog", e);
            Toast.makeText(getContext(), "Error showing sign out dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Add this new method to clear user preferences
    // In UserInfoFragment.java - Updated clearUserPreferences method

    // Add this method to clear user preferences (updated version)
    private void clearUserPreferences() {
        try {
            if (getContext() != null) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Clear all saved user data
                editor.apply();
                Log.d(TAG, "User preferences cleared successfully");
            } else {
                Log.w(TAG, "Context is null, cannot clear preferences");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error clearing user preferences", e);
        }
    }

    private boolean validateEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                Toast.makeText(getContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error validating email", e);
            Toast.makeText(getContext(), "Error validating email: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validateUsername(String username) {
        try {
            if (username == null || username.isEmpty()) {
                Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (username.length() < 3) {
                Toast.makeText(getContext(), "Username must be at least 3 characters long", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (username.length() > 30) {
                Toast.makeText(getContext(), "Username cannot be longer than 30 characters", Toast.LENGTH_SHORT).show();
                return false;
            }

            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error validating username", e);
            Toast.makeText(getContext(), "Error validating username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "onResume called - refreshing user data");
            // Refresh user info when fragment becomes visible
            refreshCurrentUser();
            if (currentUser != null) {
                // Reload user data to check for any email changes
                currentUser.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User data reloaded in onResume");
                        currentUser = mAuth.getCurrentUser(); // Refresh reference
                        loadUserInfoFromFirebase();
                    } else {
                        Log.e(TAG, "Failed to reload user in onResume");
                        loadUserInfoFromFirebase(); // Try loading anyway
                    }
                });
            } else {
                Log.w(TAG, "No current user in onResume");
                loadUserInfoFromFirebase();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume", e);
        }
    }
}