package com.example.myapplication2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DevInfoFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dev_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        TextView nameField = view.findViewById(R.id.name_field);
        TextView studentNoField = view.findViewById(R.id.student_no_field);
        TextView personalStatementField = view.findViewById(R.id.personal_statement_field);
        TextView releaseVersionField = view.findViewById(R.id.release_version_field);
        Button exitButton = view.findViewById(R.id.exit_button);

        // Set developer information
        if (nameField != null) {
            nameField.setText("John Doe");
        }
        if (studentNoField != null) {
            studentNoField.setText("STU001234");
        }
        if (personalStatementField != null) {
            personalStatementField.setText("Passionate mobile app developer with expertise in Android development. Focused on creating user-friendly applications with modern design principles.");
        }
        if (releaseVersionField != null) {
            releaseVersionField.setText("Version 1.0.0");
        }

        // Set exit button listener
        if (exitButton != null) {
            exitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).loadNewsFragment();
                    }
                }
            });
        }
    }
}