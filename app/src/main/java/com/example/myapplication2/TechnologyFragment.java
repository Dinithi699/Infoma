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

public class TechnologyFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> technologyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_technology, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize technology news list and adapter
        technologyList = generateTechnologyNews();
        newsAdapter = new NewsAdapter(technologyList);
        recyclerView.setAdapter(newsAdapter);
    }

    private List<NewsItem> generateTechnologyNews() {
        List<NewsItem> technology = new ArrayList<>();

        technology.add(new NewsItem(
                "AI Revolution in 2025",
                "Artificial Intelligence continues to transform industries with breakthrough innovations in machine learning...",
                "Technology",
                "30 minutes ago",
                R.drawable.ic_technology
        ));

        technology.add(new NewsItem(
                "5G Network Expansion",
                "Major telecommunications companies expand 5G coverage to rural areas worldwide...",
                "Technology",
                "2 hours ago",
                R.drawable.ic_technology
        ));

        technology.add(new NewsItem(
                "Quantum Computing Breakthrough",
                "Scientists achieve new milestone in quantum computing with improved stability and processing power...",
                "Technology",
                "4 hours ago",
                R.drawable.ic_technology
        ));

        technology.add(new NewsItem(
                "Electric Vehicle Innovation",
                "New battery technology promises longer range and faster charging for electric vehicles...",
                "Technology",
                "6 hours ago",
                R.drawable.ic_technology
        ));

        technology.add(new NewsItem(
                "Cybersecurity Updates",
                "Latest security protocols implemented to protect against emerging cyber threats...",
                "Technology",
                "8 hours ago",
                R.drawable.ic_technology
        ));

        return technology;
    }
}