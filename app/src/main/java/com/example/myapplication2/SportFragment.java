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

public class SportFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> sportsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sport, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize sports news list and adapter
        sportsList = generateSportsNews();
        newsAdapter = new NewsAdapter(sportsList);
        recyclerView.setAdapter(newsAdapter);
    }

    private List<NewsItem> generateSportsNews() {
        List<NewsItem> sports = new ArrayList<>();

        sports.add(new NewsItem(
                "Cricket World Cup Finals Approaching",
                "The much-awaited cricket world cup finals are set to begin next week with exciting matchups...",
                "Sports",
                "1 hour ago",
                R.drawable.ic_sports
        ));

        sports.add(new NewsItem(
                "Football Transfer News",
                "Major football clubs announce significant player transfers for the upcoming season...",
                "Sports",
                "3 hours ago",
                R.drawable.ic_sports
        ));

        sports.add(new NewsItem(
                "Tennis Championship Updates",
                "Top tennis players compete in the international championship with surprising results...",
                "Sports",
                "5 hours ago",
                R.drawable.ic_sports
        ));

        sports.add(new NewsItem(
                "Basketball League Highlights",
                "Basketball season reaches its peak with intense competition between leading teams...",
                "Sports",
                "7 hours ago",
                R.drawable.ic_sports
        ));

        sports.add(new NewsItem(
                "Olympic Games Preparation",
                "Athletes from around the world prepare for the upcoming Olympic games with intensive training...",
                "Sports",
                "10 hours ago",
                R.drawable.ic_sports
        ));

        return sports;
    }
}