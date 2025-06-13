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

public class TechnologyFragment extends Fragment implements NewsAdapter.OnNewsItemClickListener {

    private static final String TAG = "TechnologyFragment";
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<NewsItem> technologyList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_technology, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated called");

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Initialize technology news list and adapter
            technologyList = generateTechnologyNews();
            newsAdapter = new NewsAdapter(technologyList);

            // Set the click listener
            newsAdapter.setOnNewsItemClickListener(this);

            recyclerView.setAdapter(newsAdapter);

            Log.d(TAG, "RecyclerView set up with " + technologyList.size() + " items");
        } else {
            Log.e(TAG, "RecyclerView not found in layout!");
        }
    }

    @Override
    public void onNewsItemClick(NewsItem newsItem) {
        Log.d(TAG, "Technology news item clicked: " + newsItem.getTitle());
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
        String additionalContent = getTechnologyAdditionalContent(newsItem.getTitle());
        return baseDescription + "\n\n" + additionalContent;
    }

    private String getTechnologyAdditionalContent(String title) {
        if (title.contains("AI Revolution")) {
            return "Machine learning algorithms are becoming more sophisticated, enabling applications in healthcare, finance, and autonomous vehicles. Companies are integrating AI into their core business processes to improve efficiency and decision-making capabilities.";
        } else if (title.contains("5G Network")) {
            return "The expansion of 5G networks is enabling new possibilities for IoT devices, smart cities, and enhanced mobile experiences. This technology promises to revolutionize how we connect and interact with digital services.";
        } else if (title.contains("Quantum Computing")) {
            return "Quantum computing represents a paradigm shift in computational power, with potential applications in cryptography, drug discovery, and complex optimization problems. Research institutions and tech companies are racing to achieve quantum advantage.";
        } else if (title.contains("Electric Vehicle")) {
            return "Thissapura Kanishta Vidayalaya; a secondary school in Ethimalwewa, Monaragala was chosen for this project. The school where more than 225 students are studying neither has a well within the premises nor a public water-line.  Hence water had to be supplied to the school from a tube well located nearly 1 km away from the school. The electricity for the pumps that were used to pump water from the tube well came from the close-by house of a villager who is also a part of the rural community. This was not a sustainable solution and hence the students and the staff of the school were suffering from serious water shortages.";
        } else if (title.contains("Cybersecurity")) {
            return "As digital threats evolve, cybersecurity measures must adapt to protect sensitive data and critical infrastructure. Organizations are investing in advanced threat detection and response systems to stay ahead of cybercriminals.";
        } else {
            return "This technology story continues to develop as innovation drives progress across multiple sectors. The impact of these technological advances will shape how we live, work, and interact in the digital age.";
        }
    }

    private List<NewsItem> generateTechnologyNews() {
        List<NewsItem> technology = new ArrayList<>();

        technology.add(new NewsItem(
                "AI Revolution in 2025",
                "Artificial Intelligence continues to transform industries with breakthrough innovations in machine learning and neural networks. Companies are leveraging AI to automate processes and enhance user experiences.",
                "Technology",
                "30 minutes ago",
                R.drawable.tech1
        ));

        technology.add(new NewsItem(
                "5G Network Expansion",
                "Major telecommunications companies expand 5G coverage to rural areas worldwide, bringing high-speed connectivity to previously underserved regions.",
                "Technology",
                "2 hours ago",
                R.drawable.tech2
        ));

        technology.add(new NewsItem(
                "University of Colombo Signs MOU with JICA for the JICA Chair Program 2024/25",
                "On june 12th, 2025, the University of Colombo and the Japan International Cooperation Agency (JICA) formalized a partnership by signing a Memorandum of Understanding (MOU) for the JICA Chair Program 2024/25. The signing ceremony took place at College House.",
                "Technology",
                "4 hours ago",
                R.drawable.tech3
        ));

        technology.add(new NewsItem(
                "Solar Village Project",
                "The Department of Instrumentation and Automation Technology (IAT) of the Faculty of Technology, together with a UK-based charity organization, Hela Sarana, and Sri Lanka Association for the Advancement of Science (SLAAS)/ Section E1, successfully completed a Solar Village project at Tissapura Kanishta Vidyalaya",
                "Technology",
                "6 hours ago",
                R.drawable.tech6
        ));

        technology.add(new NewsItem(
                "Cybersecurity Updates",
                "Latest security protocols implemented to protect against emerging cyber threats as organizations strengthen their digital defenses.",
                "Technology",
                "8 hours ago",
                R.drawable.tech4
        ));

        technology.add(new NewsItem(
                "Cloud Computing Advances",
                "New cloud infrastructure solutions offer improved scalability and performance for businesses of all sizes, enabling digital transformation initiatives.",
                "Technology",
                "12 hours ago",
                R.drawable.tech5
        ));

        return technology;
    }

    // This method can be called by MainActivity to handle back press
    public boolean onBackPressed() {
        // Fragment doesn't need to handle drawer - MainActivity handles it
        return false;
    }
}