package com.example.myapplication2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<NewsItem> newsList;
    private OnNewsItemClickListener onNewsItemClickListener;

    // Interface for handling item clicks
    public interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem newsItem);
    }

    public NewsAdapter(List<NewsItem> newsList) {
        this.newsList = newsList;
    }

    public void setOnNewsItemClickListener(OnNewsItemClickListener listener) {
        this.onNewsItemClickListener = listener;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem newsItem = newsList.get(position);

        holder.titleTextView.setText(newsItem.getTitle());
        holder.descriptionTextView.setText(newsItem.getDescription());
        holder.categoryTextView.setText(newsItem.getCategory());
        holder.timeTextView.setText(newsItem.getTimeAgo());
        holder.newsImageView.setImageResource(newsItem.getImageResource());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView categoryTextView;
        TextView timeTextView;
        ImageView newsImageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.news_title);
            descriptionTextView = itemView.findViewById(R.id.news_description);
            categoryTextView = itemView.findViewById(R.id.news_category);
            timeTextView = itemView.findViewById(R.id.news_time);
            newsImageView = itemView.findViewById(R.id.news_image);

            // Set click listener for the entire item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onNewsItemClickListener != null) {
                        onNewsItemClickListener.onNewsItemClick(newsList.get(position));
                    }
                }
            });
        }
    }
}