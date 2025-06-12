package com.example.myapplication2;

import android.app.AlertDialog;
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

    private void showDeleteAccountDialog() {
        if (getContext() == null) return;

        try {
            // Create warning dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("⚠️ Delete Account")
                    .setMessage("Are you sure you want to delete your account?\n\n" +
                            "This action is PERMANENT and cannot be undone. All your data will be lost forever.\n\n" +
                            "Please enter your current password to confirm:")
                    .setCancelable(true);

            // Create password input layout
            LinearLayout dialogLayout = new LinearLayout(getContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(50, 40, 50, 10);

            EditText passwordEditText = new EditText(getContext());
            passwordEditText.setHint("Enter your current password");
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setPadding(20, 20, 20, 20);

            dialogLayout.addView(passwordEditText);

            builder.setView(dialogLayout)
                    .setPositiveButton("DELETE ACCOUNT", (dialog, which) -> {
                        String password = passwordEditText.getText().toString();
                        if (password.isEmpty()) {
                            Toast.makeText(getContext(), "Password is required to delete account", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        deleteUserAccount(password);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();

        } catch (Exception e) {
            Log.e(TAG, "Error showing delete account dialog", e);
            Toast.makeText(getContext(), "Error showing delete dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getContext(), "Account deleted successfully", Toast.LENGTH_LONG).show();

                                    // Sign out and navigate to home
                                    if (mAuth != null) {
                                        mAuth.signOut();
                                    }

                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).loadNewsFragment();
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
            // Create dialog layout programmatically
            LinearLayout dialogLayout = new LinearLayout(getContext());
            dialogLayout.setOrientation(LinearLayout.VERTICAL);
            dialogLayout.setPadding(50, 40, 50, 10);

            // Username input
            TextView usernameLabel = new TextView(getContext());
            usernameLabel.setText("Username:");
            usernameLabel.setTextSize(16);
            usernameLabel.setPadding(0, 0, 0, 10);

            EditText usernameEditText = new EditText(getContext());
            usernameEditText.setText(currentUsername);
            usernameEditText.setHint("Enter username");
            usernameEditText.setPadding(20, 20, 20, 20);

            // Email input
            TextView emailLabel = new TextView(getContext());
            emailLabel.setText("Email:");
            emailLabel.setTextSize(16);
            emailLabel.setPadding(0, 20, 0, 10);

            EditText emailEditText = new EditText(getContext());
            emailEditText.setText(currentEmail);
            emailEditText.setHint("Enter email address");
            emailEditText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            emailEditText.setPadding(20, 20, 20, 20);

            // Password input for re-authentication
            TextView passwordLabel = new TextView(getContext());
            passwordLabel.setText("Current Password (required for email change):");
            passwordLabel.setTextSize(16);
            passwordLabel.setPadding(0, 20, 0, 10);

            EditText passwordEditText = new EditText(getContext());
            passwordEditText.setHint("Enter your current password");
            passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setPadding(20, 20, 20, 20);

            // Add views to layout
            dialogLayout.addView(usernameLabel);
            dialogLayout.addView(usernameEditText);
            dialogLayout.addView(emailLabel);
            dialogLayout.addView(emailEditText);
            dialogLayout.addView(passwordLabel);
            dialogLayout.addView(passwordEditText);

            // Create and show dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Edit User Information")
                    .setView(dialogLayout)
                    .setPositiveButton("Save", (dialog, which) -> {
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
                        } catch (Exception e) {
                            Log.e(TAG, "Error saving changes", e);
                            Toast.makeText(getContext(), "Error saving changes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();

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

    private void showSignOutDialog() {
        if (getContext() == null) return;

        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            // Handle sign out action
                            if (mAuth != null) {
                                mAuth.signOut();
                                Toast.makeText(getContext(), "Signed out successfully!", Toast.LENGTH_SHORT).show();

                                // Navigate back to news fragment or login screen
                                if (getActivity() instanceof MainActivity) {
                                    ((MainActivity) getActivity()).loadNewsFragment();
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error signing out", e);
                            Toast.makeText(getContext(), "Error signing out: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing sign out dialog", e);
            Toast.makeText(getContext(), "Error showing sign out dialog: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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