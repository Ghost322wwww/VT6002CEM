package com.example.newproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.RecyclerAdapter;
import com.example.newproject.Adapter.RecyclerAdapter_ranking;
import com.example.newproject.Adapter.RecyclerAdapter_upcoming_iti;
import com.example.newproject.model.RankingItem;
import com.example.newproject.model.TagItem;
import com.example.newproject.model.UpcomingItiItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class homePage extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    ImageButton bookmarkButton,btn_chat,btn_moreDetail, btn_map,btn_plan;

    private EditText searchEditText;
    private RecyclerView recyclerViewTag, recyclerViewUpcomingIti, recyclerViewRanking;
    private RecyclerAdapter mAdapter_tag;
    private RecyclerAdapter_upcoming_iti mAdapter_upcoming_iti;
    private RecyclerAdapter_ranking mAdapter_ranking;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton imageButton6;
    List<TagItem> tagItems;
    List<UpcomingItiItem> UpcomingItiItems;
    List<RankingItem> RankItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        btn_map = findViewById(R.id.btn_map);
        btn_chat = findViewById(R.id.btn_chat);
        btn_plan = findViewById(R.id.btn_plan);
        bookmarkButton = findViewById(R.id.bookmarkButton);
        btn_moreDetail = findViewById(R.id.btn_moreDetail);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    Intent intent = new Intent(homePage.this, ChatRoomHomePageActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(homePage.this, "Please log in first", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(homePage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btn_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homePage.this, planPage.class);
                startActivity(intent);
                finish();
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homePage.this, mapPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homePage.this, BookMarkActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_moreDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(homePage.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        searchEditText = findViewById(R.id.searchEditText);
        imageButton6 = findViewById(R.id.imageButton6);

        imageButton6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString();
                if (!searchText.isEmpty()) {
                    Intent intent = new Intent(homePage.this, SearchResultActivity.class);
                    intent.putExtra("searchQuery", searchText);
                    startActivity(intent);
                } else {
                    Toast.makeText(homePage.this, "Please enter something to search!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        // Find and setup the first RecyclerView
        recyclerViewTag = findViewById(R.id.recycler_view_tag);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTag.setLayoutManager(layoutManager);

        // Instantiate your list of TagItems
        tagItems = new ArrayList<>();
        // Add the items into the list. You'll need to replace the image and text with your actual data
        ArrayList<TagItem> tagItems = new ArrayList<>();
        tagItems.add(new TagItem(R.drawable.tag_adventure, "Adventure_Sports"));
        tagItems.add(new TagItem(R.drawable.tag_historical,"Historical"));
        tagItems.add(new TagItem(R.drawable.tag_landmark,"Landmark"));
        tagItems.add(new TagItem(R.drawable.tag_nature,"Natural"));
        tagItems.add(new TagItem(R.drawable.tag_shopping,"Shopping"));
        tagItems.add(new TagItem(R.drawable.tag_viewpoint, "Viewpoint"));
        tagItems.add(new TagItem(R.drawable.tag_accommodation,"Accommodation"));
        tagItems.add(new TagItem(R.drawable.tag_architecture, "Architecture"));
        tagItems.add(new TagItem(R.drawable.tag_beach, "Beach"));
        tagItems.add(new TagItem(R.drawable.tag_culture, "Art_Culture"));
        tagItems.add(new TagItem(R.drawable.tag_educational, "Educational"));
        tagItems.add(new TagItem(R.drawable.tag_event, "Event"));
        tagItems.add(new TagItem(R.drawable.tag_food, "Food_Drink"));
        tagItems.add(new TagItem(R.drawable.tag_heritage,"Heritage"));
        tagItems.add(new TagItem(R.drawable.tag_park,"Park"));
        tagItems.add(new TagItem(R.drawable.tag_relaxing,"Relaxing"));
        tagItems.add(new TagItem(R.drawable.tag_religious, "Religious"));

        mAdapter_tag = new RecyclerAdapter(tagItems);
        mAdapter_tag.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(homePage.this, TagResultListActivity.class);
                intent.putExtra("selectedTag", tagItems.get(position).getText());
                startActivity(intent);
            }
        });
        recyclerViewTag.setAdapter(mAdapter_tag);
        loadAndDisplayAreas();


        // Repeat the above for the other two RecyclerViews
        recyclerViewUpcomingIti = findViewById(R.id.recycler_view_upcoming_iti);
        recyclerViewUpcomingIti.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        UpcomingItiItems = new ArrayList<>();
//        UpcomingItiItems.add(new UpcomingItiItem(R.drawable.upcomephoto1, "2023-04-20","TeamLab:continuous"));
//        UpcomingItiItems.add(new UpcomingItiItem(R.drawable.upcomephoto2, "2023-04-25","Fullmetal Alchemist Exhibition"));
//        UpcomingItiItems.add(new UpcomingItiItem(R.drawable.upcomephoto3, "2023-04-30","Voyage with Van Gogh"));
//        UpcomingItiItems.add(new UpcomingItiItem(R.drawable.upcomephoto4, "2023-05-30","ARTE MUSEUM"));
        mAdapter_upcoming_iti = new RecyclerAdapter_upcoming_iti(UpcomingItiItems);
        mAdapter_upcoming_iti.setOnItemClickListener(new RecyclerAdapter_upcoming_iti.OnItemClickListener() {
            @Override
            public void onItemClick(UpcomingItiItem item) {
                Intent intent = new Intent(homePage.this, DetailsActivity.class);
                intent.putExtra("upcomingItiName", item.getupcomingitiname());
                startActivity(intent);
            }
        });
        recyclerViewUpcomingIti.setAdapter(mAdapter_upcoming_iti);
        updateHomepageUpcoming();

    }

    public void onLeadingButtonClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void chatRoomButtonClick(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // User is signed in, direct to ChatRoomHomePageActivity
            Intent intent = new Intent(this, ChatRoomHomePageActivity.class);
            startActivity(intent);
            finish();
        } else {
            // No user is currently signed in, prompt to login
            Toast.makeText(this, "Please login before entering the chatroom", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void mapButtonClick(View view) {
        if (isServicesOK()) {
            Intent intent = new Intent(this, mapPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void PlannerButtonClick(View view) {
        if (isServicesOK()) {
            Intent intent = new Intent(this, planPage.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(homePage.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){

            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(homePage.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void loadAndDisplayAreas() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("areas");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Area> areas = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String areaId = snapshot.child("areaId").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    List<District> districts = new ArrayList<>();
                    for (DataSnapshot districtSnapshot : snapshot.child("districts").getChildren()) {
                        String districtId = districtSnapshot.child("districtId").getValue(String.class);
                        String districtName = districtSnapshot.child("name").getValue(String.class);
                        districts.add(new District(districtId, districtName));
                    }
                    areas.add(new Area(areaId, name, districts));
                }

                recyclerViewRanking = findViewById(R.id.recycler_view_rankings);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(homePage.this, RecyclerView.HORIZONTAL, false);
                recyclerViewRanking.setLayoutManager(layoutManager);
                mAdapter_ranking = new RecyclerAdapter_ranking(areas);
                recyclerViewRanking.setAdapter(mAdapter_ranking);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("FirebaseData", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }
    private void updateHomepageUpcoming() {
        DatabaseReference upcomingItiReference = FirebaseDatabase.getInstance().getReference("upcoming");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String photo = snapshot.child("photo").getValue(String.class);
                    int imageResourceId = getResources().getIdentifier(photo, "drawable", getPackageName());
                    String date = snapshot.child("date").getValue(String.class);
                    String eventName = snapshot.getKey();
                    String description = snapshot.child("description").getValue(String.class);
                    String url = snapshot.child("url").getValue(String.class);

                    // Create a new UpcomingItiItem with this data
                    UpcomingItiItem newItem = new UpcomingItiItem(imageResourceId, date, eventName, description);

                    // Add item to your list
                    UpcomingItiItems.add(newItem);
                }
                // Update recycler view adapter here after list has received all items
                mAdapter_upcoming_iti.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // log error
            }
        };

        upcomingItiReference.addValueEventListener(postListener);
    }
}