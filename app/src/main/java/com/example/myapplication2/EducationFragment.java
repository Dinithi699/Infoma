package com.example.myapplication2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EducationFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> educationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_education, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize education news list and adapter
        educationList = generateEducationNews();
        newsAdapter = new NewsAdapter(educationList);
        recyclerView.setAdapter(newsAdapter);
    }

    private List<NewsItem> generateEducationNews() {
        List<NewsItem> education = new ArrayList<>();

        education.add(new NewsItem(
                "Online Learning Platforms Expand",
                "Educational institutions adopt new digital learning platforms to enhance student experience...",
                "Education",
                "1 hour ago",
                R.drawable.ic_education
        ));

        education.add(new NewsItem(
                "STEM Education Initiatives",
                "Government launches new STEM education programs to prepare students for future careers...",
                "Education",
                "3 hours ago",
                R.drawable.ic_education
        ));

        education.add(new NewsItem(
                "University Scholarship Programs",
                "Major universities announce increased scholarship opportunities for deserving students...",
                "Education",
                "5 hours ago",
                R.drawable.ic_education
        ));

        education.add(new NewsItem(
                "Digital Literacy Campaigns",
                "Schools implement comprehensive digital literacy programs to bridge the technology gap...",
                "Education",
                "7 hours ago",
                R.drawable.ic_education
        ));

        education.add(new NewsItem(
                "Teacher Training Programs",
                "New teacher training initiatives focus on modern pedagogical methods and technology integration...",
                "Education",
                "9 hours ago",
                R.drawable.ic_education
        ));

        return education;
    }
}