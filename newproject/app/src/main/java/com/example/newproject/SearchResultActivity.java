package com.example.newproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.SearchResultRecyclerAdapter;
import com.example.newproject.model.SearchResultItem;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchResultRecyclerAdapter mAdapter;
    private ArrayList<SearchResultItem> searchResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.recycler_view_search_result_list);
        searchResultList = new ArrayList<>();

        // Hardcode items to add into ArrayList
        addItems();

        mAdapter = new SearchResultRecyclerAdapter(this, searchResultList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        ImageButton backButton = findViewById(R.id.btn_back_home);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addItems() {
        SearchResultItem item1 = new SearchResultItem(R.drawable.tag_religious, "Name1", "Address1");
        searchResultList.add(item1);

        SearchResultItem item2 = new SearchResultItem(R.drawable.recentimage1, "Name2", "Address2");
        searchResultList.add(item2);

        SearchResultItem item3 = new SearchResultItem(R.drawable.tag_beach, "Name3", "Address3");
        searchResultList.add(item3);

    }
}

