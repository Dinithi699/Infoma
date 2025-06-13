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

public class SportFragment extends Fragment implements NewsAdapter.OnNewsItemClickListener {

    private static final String TAG = "SportFragment";
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> sportsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_sport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Initialize sports news list and adapter
            sportsList = generateSportsNews();
            newsAdapter = new NewsAdapter(sportsList);

            // Set the click listener
            newsAdapter.setOnNewsItemClickListener(this);

            recyclerView.setAdapter(newsAdapter);

            Log.d(TAG, "RecyclerView set up with " + sportsList.size() + " items");
        } else {
            Log.e(TAG, "RecyclerView not found in layout!");
        }
    }

    @Override
    public void onNewsItemClick(NewsItem newsItem) {
        Log.d(TAG, "Sports item clicked: " + newsItem.getTitle());
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
        String additionalContent = getSportsAdditionalContent(newsItem.getTitle());
        return baseDescription + "\n\n" + additionalContent;
    }

    private String getSportsAdditionalContent(String title) {
        if (title.toLowerCase().contains("cricket")) {
            return "The cricket world cup has been one of the most anticipated sporting events of the year. Teams have been preparing rigorously, with players showcasing exceptional skills in recent matches. The finals promise to deliver thrilling cricket action with world-class batting, bowling, and fielding performances.";
        } else if (title.toLowerCase().contains("football") || title.toLowerCase().contains("transfer")) {
            return "The football transfer window has seen some major moves with clubs investing heavily in new talent. These strategic acquisitions are expected to strengthen team dynamics and create exciting new playing combinations. Fans are eagerly anticipating how these changes will impact the upcoming season.";
        } else if (title.toLowerCase().contains("tennis")) {
            return "The tennis championship has featured some spectacular matches with players demonstrating incredible skill and determination. The competition has been fierce, with several upsets and breakthrough performances from rising stars. The tournament continues to showcase the highest level of professional tennis.";
        } else if (title.toLowerCase().contains("basketball")) {
            return "The basketball league has reached a critical phase with teams fighting for playoff positions. Star players are delivering outstanding performances, and the level of competition has been exceptional. Fans are witnessing some of the most exciting basketball games in recent years.";
        } else if (title.toLowerCase().contains("olympic")) {
            return "Olympic preparation is in full swing with athletes from various disciplines training at peak intensity. National teams are finalizing their rosters and strategies for the games. The anticipation is building as the world prepares to witness extraordinary athletic achievements and record-breaking performances.";
        } else {
            return "This sporting event continues to captivate audiences worldwide with exceptional athletic performances. Athletes are pushing the boundaries of their respective sports, creating memorable moments for fans. The competition remains intense as participants strive for excellence and sporting glory.";
        }
    }

    private List<NewsItem> generateSportsNews() {
        List<NewsItem> sports = new ArrayList<>();

        sports.add(new NewsItem(
                "Closing Ceremony of the Sri Lanka University Games XIII",
                "The Closing Ceremony of the Sri Lanka University Games XIII 2019 was held on 7th of September 2019 at the Rabindranath Tagore Memorial Auditorium, University of Ruhuna. Mr. Kumar Sangakkara, a former Captain of the Sri Lankan National Cricket team, was the chief guest of this colorful event. University of Sri Jayewardenepura emerged as the winner and University of Colombo became the 1st runner-up. The 2nd runner-up was the University of Moratuwa.",
                "Sports",
                "1 hour ago",
                R.drawable.sports1
        ));

        sports.add(new NewsItem(
                "University of Colombo students won medals in in SAG 2025",
                "Four University of Colombo students won medals for Sri Lanka in the 13th South Asian Games 2025 in Nepal. Ms NIS Mendis, Department of Plant Science-Faculty of Science won the female individual Poomsae-bronze medal, Female team Poomsae-bronze medal and the Pair Poomsae-Gold medal in the age category 17 to 23. ",
                "Sports",
                "3 hours ago",
                R.drawable.sports2
        ));

        sports.add(new NewsItem(
                "Inter-University Championship 2025 Track & field Games",
                "Inter-University Championship 2025 track & field games were held on 28th of October at Sugathadasa Stadium, Colombo.",
                "Sports",
                "5 hours ago",
                R.drawable.sports3
        ));

        sports.add(new NewsItem(
                "Dr Chathuranga Ranasinghe wins Asia Sports Medicine and Science Award 2025",
                "Dr Chathuranga Ranasinghe, The Director of the Sport and Exercise Medicine Unit| Member of the Medical Committee, National Olympic Committee Sri Lanka | Senior Lecturer, Department of Allied Health Sciences, Faculty of Medicine, was awarded the 7th Sheikh Fahad Hiroshima-Asia Sports Medicine and Science Award in line with the Asian Games 2025, by the Hiroshima City Sports Association, Hiroshima, Japan at the General Assembly of the Olympic Council of Asia (OCA) (Mother organization of all 45 National Olympic Committees (NOC)/countries in Asia) on the 4th October 2022 in Phnom Penh, Cambodia.",
                "Sports",
                "7 hours ago",
                R.drawable.sports4
        ));

        sports.add(new NewsItem(
                "Colours Awarding Ceremony 2025",
                "The Annual Colors Awarding Ceremony was held on the 7th of june 2025 at the New Art Theater of University of Colombo and conducted in three sessions by adhering to the prescribed health and safety guidelines. The First session was held under the patronage of Professor K.R. Ranjith Mahanama, the former Chairman of Sports Advisory Board and the former Dean of Faculty of Science as the Chief Guest. The Second session was graced with the presence of Professor Devaka Weerakoon former Chairman of Sports Advisory Board and former Dean of Faculty of Nursing as the chief guest. The final and main session was held under the patronage of Mr. Dian Gomes as the Chief Guest, the Vice Chairman of the University Grants Commission (UGC), Professor Chandana P. Udawatte was the Guest of Honor and the Vice Chancellor of the University of Colombo Professor Chandrika N Wijeyaratne as the special guest.",
                "Sports",
                "10 hours ago",
                R.drawable.sports5
        ));

        return sports;
    }

    // This method can be called by MainActivity to handle back press
    public boolean onBackPressed() {
        // Fragment doesn't need to handle drawer - MainActivity handles it
        return false;
    }
}