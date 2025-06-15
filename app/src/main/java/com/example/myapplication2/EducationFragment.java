package com.example.myapplication2;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EducationFragment extends Fragment implements NewsAdapter.OnNewsItemClickListener {

    private static final String TAG = "EducationFragment";
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> educationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_education, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Initialize education news list and adapter
            educationList = generateEducationNews();
            newsAdapter = new NewsAdapter(educationList);

            // Set the click listener
            newsAdapter.setOnNewsItemClickListener(this);

            recyclerView.setAdapter(newsAdapter);

            Log.d(TAG, "RecyclerView set up with " + educationList.size() + " items");
        } else {
            Log.e(TAG, "RecyclerView not found in layout!");
        }
    }

    @Override
    public void onNewsItemClick(NewsItem newsItem) {
        Log.d(TAG, "Education news item clicked: " + newsItem.getTitle());
        showNewsDetailDialog(newsItem);
    }

    private void showNewsDetailDialog(NewsItem newsItem) {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_news_detail);

        // Set dialog properties
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        // Find views in dialog
        ImageView dialogImage = dialog.findViewById(R.id.dialog_image);
        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        TextView dialogDescription = dialog.findViewById(R.id.dialog_description);
        TextView dialogCategory = dialog.findViewById(R.id.dialog_category);
        TextView dialogTime = dialog.findViewById(R.id.dialog_time);
        Button btnClose = dialog.findViewById(R.id.btn_close);

        // Set data to views
        dialogImage.setImageResource(newsItem.getImageResource());
        dialogTitle.setText(newsItem.getTitle());
        dialogDescription.setText(getExpandedDescription(newsItem));
        dialogCategory.setText(newsItem.getCategory());
        dialogTime.setText(newsItem.getTimeAgo());

        // Set close button click listener
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private String getExpandedDescription(NewsItem newsItem) {
        String baseDescription = newsItem.getDescription();
        String additionalContent = getEducationAdditionalContent(newsItem.getTitle());
        return baseDescription + "\n\n" + additionalContent;
    }

    private String getEducationAdditionalContent(String title) {
        if (title.contains("General Convocation 2025")) {
            return "Bachelor of Engineering Technology Honours in Instrumentation & Automation, Bachelor of Information and Communication Technology Honours. 12 University of Colombo Academic Excellence Awards were awarded at this Convocation, and 14 Faculty Awards were presented to the Faculty of Management and Finance.";
        } else if (title.contains("Beach Cleanup and Basic Water Safety Project")) {
            return "Recognizing the dire state of Sri Lanka’s polluted beaches, driven by their commitment to environmental protection, embarked on a mission to safeguard the coastline.\n" +
                    "\n" +
                    "Every participant understood the significance of seemingly trivial items—straws, bottle caps, fishing lines, plastic spoons, and toffee wrappers—as they meticulously combed the beach during the cleanup. More than just waste collection, the event served as a call to action, transforming participants into ambassadors for change. ";
        } else if (title.contains("Scholarship")) {
            return "Scholarship programs play a vital role in making quality education accessible to talented students from diverse backgrounds. These opportunities not only provide financial support but also create pathways for academic excellence and career advancement in various fields.";
        } else if (title.contains("Intensive Course")) {
            return "Improve time management abilities, enhance problem-solving skills, and boost students' confidence levels as they transition to higher education. This initiative represents the university's commitment to supporting students in their academic journey and preparing them for the challenges of university-level studies.";
        } else if (title.contains("Registrations are open for the certificate courses")) {
            return "Modern teacher training programs focus on equipping educators with 21st-century skills and pedagogical approaches. These initiatives emphasize student-centered learning, technology integration, and inclusive teaching methods that cater to diverse learning styles and needs.";
        } else if (title.contains("Research")) {
            return "Academic research continues to drive innovation and knowledge creation across disciplines. Universities are fostering collaborative research environments where students and faculty work together on projects that address real-world challenges and contribute to scientific advancement.";
        } else if (title.contains("ART FOR EARTH")) {
            return "This comprehensive program encourages students aged 6-13 to explore environmental themes through various art forms including drawing, painting, and creative workshops. The campaign aims to foster environmental consciousness among young minds while developing their artistic talents. Participants will create artwork that highlights the importance of protecting our planet, with special focus on climate change, biodiversity conservation, and sustainable living practices.";
        } else {
            return "This educational development represents part of the ongoing transformation in how we approach learning and teaching. Educational institutions continue to evolve their methods to better serve students and prepare them for future challenges and opportunities.";
        }
    }

    private List<NewsItem> generateEducationNews() {
        List<NewsItem> education = new ArrayList<>();

        education.add(new NewsItem(
                "General Convocation 2025",
                "The University of Colombo conducted its General Convocation 2024 (Phase I) at the Bandaranaike Memorial International Conference Hall on 2nd june 2025 under the patronage of the Vice Chancellor, Senior Professor (Chair) H D Karunaratne, Deans of the Faculty of Management and Finance, Technology and Medicine, Registrar, Bursar, Librarian, Academic Staff, Administrative and Finance Officers.",
                "Academic",
                "1 hour ago",
                R.drawable.edu1
        ));

        education.add(new NewsItem(
                "Beach Cleanup and Basic Water Safety Project- \"සිදුතෙර පියසටහන්\"",
                "In a commendable effort on 6th of june, 2024, members of the Environmental Technology Society, Faculty of Technology, University of Colombo united for a crucial cause at Panadura Beach.",
                "Academic",
                "3 hours ago",
                R.drawable.edu2
        ));

        education.add(new NewsItem(
                "University Scholarship Programs",
                "Major universities announce increased scholarship opportunities for deserving students, focusing on merit-based and need-based financial assistance.",
                "Academic",
                "5 hours ago",
                R.drawable.edu3
        ));

        education.add(new NewsItem(
                "Intensive Course",
                "The Faculty of Technology at University of Colombo introduces an accelerated learning program designed to bridge the gap between school and university education. This intensive course, running from July 17th to 28th via Zoom platform, focuses on enhancing academic readiness, building strong foundations, and developing critical thinking skills to ensure student success in university life.",
                "Academic",
                "7 hours ago",
                R.drawable.edu4
        ));

        education.add(new NewsItem(
                "Registrations are open for the certificate courses",
                "Registrations are open for you to enroll in the certificate courses conducted by the Department of Instrumentation and Automation Technology, University of Colombo." +
                        "Registration deadline: 24th March 2023",
                "Academic",
                "9 hours ago",
                R.drawable.edu5
        ));

        education.add(new NewsItem(
                "Research Collaboration Network",
                "Universities establish new research collaboration networks to foster innovation and knowledge sharing across institutions.",
                "Academic",
                "12 hours ago",
                R.drawable.edu6
        ));

        education.add(new NewsItem(
                "ART FOR EARTH",
                "The University of Colombo's Faculty of Arts introduces an innovative 'Art for Earth' initiative that combines creative expression with environmental conservation awareness.",
                "Academic",
                "1 day ago",
                R.drawable.edu7
        ));

        return education;
    }

    // This method can be called by MainActivity to handle back press
    public boolean onBackPressed() {
        // Fragment doesn't need to handle drawer - MainActivity handles it
        return false;
    }
}