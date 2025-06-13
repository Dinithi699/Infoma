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

public class FirstFragment extends Fragment implements NewsAdapter.OnNewsItemClickListener {

    private static final String TAG = "FirstFragment";
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> newsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Initialize news list and adapter
            newsList = generateSampleNews();
            newsAdapter = new NewsAdapter(newsList);

            // Set the click listener
            newsAdapter.setOnNewsItemClickListener(this);

            recyclerView.setAdapter(newsAdapter);

            Log.d(TAG, "RecyclerView set up with " + newsList.size() + " items");
        } else {
            Log.e(TAG, "RecyclerView not found in layout!");
        }
    }

    @Override
    public void onNewsItemClick(NewsItem newsItem) {
        Log.d(TAG, "News item clicked: " + newsItem.getTitle());
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
        String additionalContent = getAdditionalContent(newsItem.getCategory());
        return baseDescription + "\n\n" + additionalContent;
    }

    private String getAdditionalContent(String category) {
        switch (category) {
            case "Technology":
                return "The rapid advancement in artificial intelligence and machine learning technologies has led to significant breakthroughs in various sectors. Companies are investing heavily in research and development to stay competitive in this evolving landscape.";

            case "Education":
                return "Educational institutions worldwide are embracing digital transformation to provide better learning experiences. The integration of technology in education has opened new possibilities for remote learning and personalized instruction.";

            case "Sports":
                return "The championship has attracted millions of viewers from around the world. Athletes have been training rigorously for months to prepare for these competitions. The level of skill displayed has been truly remarkable.";

            case "Health":
                return "Medical breakthroughs continue to improve patient outcomes and quality of life. Researchers are working on innovative treatments that target specific conditions with greater precision and fewer side effects.";

            default:
                return "This story continues to develop as more information becomes available. Our team is monitoring the situation closely and will provide updates as they occur.";
        }
    }

    private List<NewsItem> generateSampleNews() {
        List<NewsItem> news = new ArrayList<>();

        news.add(new NewsItem(
                "Breaking: Technology Advances in 2025",
                "Latest developments in artificial intelligence and machine learning continue to reshape industries worldwide. New breakthroughs in quantum computing and neural networks are opening unprecedented possibilities for innovation.",
                "Technology",
                "2 hours ago",
                R.drawable.research5
        ));

        news.add(new NewsItem(
                "New Educational Programs Launch",
                "Universities introduce innovative online courses and hybrid learning models for students. These programs combine traditional classroom instruction with cutting-edge digital platforms.",
                "Education",
                "3 hours ago",
                R.drawable.research3
        ));

        news.add(new NewsItem(
                "Sports Championship Finals",
                "Exciting matches continue as teams compete for the championship title in various sports. Athletes are showcasing exceptional skills and determination in these final competitions.",
                "Sports",
                "6 hours ago",
                R.drawable.research6
        ));

        news.add(new NewsItem(
                "Technology Updates",
                "Highlights of Memorandum of Understanding Signing Ceremony (UoC and RCS2 Technologies (Pvt) Ltd)",
                "Education",
                "8 hours ago",
                R.drawable.research2
        ));

        news.add(new NewsItem(
                "ANNUAL RESEARCH SYMPOSIUM ",
                "ANNUAL RESEARCH SYMPOSIUM - 2025",
                "Education",
                "12 hours ago",
                R.drawable.research
        ));

        news.add(new NewsItem(
                "Healthcare Innovation",
                "Medical researchers announce breakthrough treatments for various conditions. Advanced therapies and precision medicine are revolutionizing patient care worldwide.",
                "Technology",
                "1 day ago",
                R.drawable.research4
        ));

        return news;
    }

    // This method can be called by MainActivity to handle back press
    public boolean onBackPressed() {
        // Fragment doesn't need to handle drawer - MainActivity handles it
        return false;
    }
}