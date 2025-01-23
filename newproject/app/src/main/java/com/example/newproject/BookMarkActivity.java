package com.example.newproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.SimpleTagResultAdapter;
import com.example.newproject.model.SimpleTagResultItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BookMarkActivity extends AppCompatActivity {
    private ImageButton btn_back_home, btn_planning, btn_map, btn_msg, btn_profile;
    private RecyclerView recyclerViewBookmarks;
    private FirebaseDatabase db;
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark);
        btn_back_home = findViewById(R.id.btn_back_home);
        btn_planning = findViewById(R.id.btn_planning);
        btn_map = findViewById(R.id.btn_map);
        btn_msg = findViewById(R.id.btn_msg);
        btn_profile = findViewById(R.id.btn_profile);
        recyclerViewBookmarks = findViewById(R.id.recycler_view_bookmarks);

        btn_back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookMarkActivity.this, homePage.class);
                startActivity(intent);
                finish();
            }
        });
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookMarkActivity.this, mapPageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_planning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookMarkActivity.this, planPage.class);
                startActivity(intent);
                finish();
            }
        });
        btn_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookMarkActivity.this, ChatRoomHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookMarkActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference("BookMark").child(uid);
            bookmarkRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<SimpleTagResultItem> bookmarkList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Map<String, Object> data = (Map<String, Object>) snapshot.getValue();

                        String attrName = (String) data.get("AttrName");
                        String attrDistrict = (String) data.get("AttrDistrict");
                        List<String> attrTags = Arrays.asList(((String) data.get("AttrTags")).split(", "));
                        SimpleTagResultItem item = new SimpleTagResultItem(R.drawable.tag_religious, attrName, attrDistrict, attrTags);

                        bookmarkList.add(item);
                    }
                    SimpleTagResultAdapter adapter = new SimpleTagResultAdapter(bookmarkList);
                    recyclerViewBookmarks.setLayoutManager(new LinearLayoutManager(BookMarkActivity.this));
                    recyclerViewBookmarks.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Bookmark", "Load bookmark failed: ", error.toException());
                }
            });
        } else {
            Log.d("Bookmark", "No user is currently signed in");
        }
    }
}