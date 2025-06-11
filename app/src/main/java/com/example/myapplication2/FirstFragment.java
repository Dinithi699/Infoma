package com.example.myapplication2;

import android.os.Bundle;
import android.util.Log;
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

public class FirstFragment extends Fragment {

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
            recyclerView.setAdapter(newsAdapter);

            Log.d(TAG, "RecyclerView set up with " + newsList.size() + " items");
        } else {
            Log.e(TAG, "RecyclerView not found in layout!");
        }

        // DO NOT set up toolbar or drawer here - MainActivity handles that
        // The fragment should only handle its own content
    }

    private List<NewsItem> generateSampleNews() {
        List<NewsItem> news = new ArrayList<>();

        news.add(new NewsItem(
                "Breaking: Technology Advances in 2025",
                "Latest developments in artificial intelligence and machine learning continue to reshape industries worldwide. New breakthroughs in quantum computing and neural networks are opening unprecedented possibilities for innovation.",
                "Technology",
                "2 hours ago",
                R.drawable.ic_news
        ));

        news.add(new NewsItem(
                "New Educational Programs Launch",
                "Universities introduce innovative online courses and hybrid learning models for students. These programs combine traditional classroom instruction with cutting-edge digital platforms.",
                "Education",
                "3 hours ago",
                R.drawable.ic_news
        ));

        news.add(new NewsItem(
                "Sports Championship Finals",
                "Exciting matches continue as teams compete for the championship title in various sports. Athletes are showcasing exceptional skills and determination in these final competitions.",
                "Sports",
                "6 hours ago",
                R.drawable.ic_news
        ));

        news.add(new NewsItem(
                "Economic Market Updates",
                "Stock markets show positive trends as new policies take effect across major economies. Investors are responding favorably to recent government initiatives and corporate earnings reports.",
                "Business",
                "8 hours ago",
                R.drawable.ic_news
        ));

        news.add(new NewsItem(
                "Climate Change Initiative",
                "New environmental protection measures are being implemented globally. Countries are working together to reduce carbon emissions and promote sustainable energy solutions.",
                "Environment",
                "12 hours ago",
                R.drawable.ic_news
        ));

        news.add(new NewsItem(
                "Healthcare Innovation",
                "Medical researchers announce breakthrough treatments for various conditions. Advanced therapies and precision medicine are revolutionizing patient care worldwide.",
                "Health",
                "1 day ago",
                R.drawable.ic_news
        ));

        return news;
    }

    // This method can be called by MainActivity to handle back press
    public boolean onBackPressed() {
        // Fragment doesn't need to handle drawer - MainActivity handles it
        return false;
    }
}