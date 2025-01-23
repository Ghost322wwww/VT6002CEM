package com.example.newproject;

import android.app.Dialog;
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

import com.example.newproject.Adapter.SimpleTagAdapter;
import com.example.newproject.Adapter.SimpleTagResultAdapter;
import com.example.newproject.model.SimpleTagItem;
import com.example.newproject.model.SimpleTagResultItem;
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
import java.util.HashMap;
import java.util.List;


public class TagResultListActivity extends AppCompatActivity {
    private static final String TAG = "HomePage";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private  String selectedTag;
    private RecyclerView recyclerViewSimpleTag, recyclerViewSimpleTagResult;
    private SimpleTagAdapter mAdapter_simpleTag;
    private SimpleTagResultAdapter mAdapter_simpleTagResult;
    private List<SimpleTagItem> simpleTagItems;
    private List<SimpleTagResultItem> tagItemsOut;
    private RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_result_list);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerViewSimpleTag = findViewById(R.id.recycler_view_tag_filter_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSimpleTag.setLayoutManager(layoutManager);

        recyclerViewSimpleTagResult = findViewById(R.id.recycler_view_result_list);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSimpleTagResult.setLayoutManager(layoutManager);


        ImageButton btnBackHome = findViewById(R.id.btn_back_home);
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = FirebaseDatabase.getInstance();
        fetchAttractionsFromRealtimeDB();
        selectedTag = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedTag = extras.getString("selectedTag");
        }
        simpleTagItems = new ArrayList<>();
        mAdapter_simpleTag = new SimpleTagAdapter(simpleTagItems, itemClickListener);
        recyclerViewSimpleTag.setAdapter(mAdapter_simpleTag);
        DatabaseReference tagsRef = db.getReference("tags");

        tagsRef.orderByChild("name").equalTo(selectedTag).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String tagValue = snapshot.getValue(String.class);
                    simpleTagItems.add(0, new SimpleTagItem(tagValue));
                    mAdapter_simpleTag.notifyDataSetChanged();
                }
                Log.d(TAG, "Loaded selected tags");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Load selected tags failed: ", databaseError.toException());
            }
        });

        loadAllTags();
    }
    SimpleTagAdapter.ItemClickListener itemClickListener = new SimpleTagAdapter.ItemClickListener() {
        @Override
        public void onItemClick(int position) {
            String clickedItemTag = simpleTagItems.get(position).getTag();
            List<SimpleTagResultItem> filteredList = new ArrayList<>();

            for (SimpleTagResultItem item : tagItemsOut) {
                if (item.getAttrTags().contains(clickedItemTag)) {
                    filteredList.add(item);
                }
            }

            mAdapter_simpleTagResult = new SimpleTagResultAdapter(filteredList);
            recyclerViewSimpleTagResult.setAdapter(mAdapter_simpleTagResult);
        }
    };

    public void onLeadingButtonClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void chatRoomButtonClick(View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, ChatRoomHomePageActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
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

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(TagResultListActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(TagResultListActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void loadAllTags() {
        DatabaseReference tagsRef = db.getReference("tags");

        tagsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String tagValue = snapshot.getValue(String.class);

                    if (tagValue.equals(selectedTag)) {
                        simpleTagItems.add(0, new SimpleTagItem(tagValue));
                    } else {

                        simpleTagItems.add(new SimpleTagItem(tagValue));
                    }
                }
                mAdapter_simpleTag.notifyDataSetChanged();
                Log.d(TAG, "Loaded all tags");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Load all tags failed: ", databaseError.toException());
            }
        });
    }

    private HashMap<String, ArrayList<SimpleTagResultItem>> tagsToAttractions = new HashMap<>();

    private void fetchAttractionsFromRealtimeDB() {
        DatabaseReference refAttractions = db.getReference("attractions");
        DatabaseReference refArea = db.getReference("areas");

        refAttractions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotAttr) {
                for (DataSnapshot snapshot : dataSnapshotAttr.getChildren()) {
                    String attrName = (String) snapshot.child("name").getValue();
                    List<String> attrTags = (List<String>) snapshot.child("tags").getValue();


                    final SimpleTagResultItem item = new SimpleTagResultItem(R.drawable.tag_religious, attrName, "", attrTags);
                    final String districtIdAttr = (String) snapshot.child("districtId").getValue();

                    refArea.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotArea) {
                            for (DataSnapshot areaSnapshot : dataSnapshotArea.getChildren()) {
                                for (DataSnapshot districtsSnapshot : areaSnapshot.child("districts").getChildren()) {
                                    String districtId = (String) districtsSnapshot.child("districtId").getValue();
                                    if (districtIdAttr.equals(districtId)) {
                                        String districtName = (String) districtsSnapshot.child("name").getValue();
                                        item.setAttrDistrict(districtName);
                                    }
                                }
                            }

                            for (String tag : attrTags) {
                                if (!tagsToAttractions.containsKey(tag)) {
                                    tagsToAttractions.put(tag, new ArrayList<>());
                                }
                                tagsToAttractions.get(tag).add(item);
                            }

                            List<SimpleTagResultItem> filteredList = tagsToAttractions.get(selectedTag);
                            if (filteredList != null) {
                                mAdapter_simpleTagResult = new SimpleTagResultAdapter(filteredList);
                                recyclerViewSimpleTagResult.setAdapter(mAdapter_simpleTagResult);
                            }
                            tagItemsOut = new ArrayList<>();
                            for (ArrayList<SimpleTagResultItem> items : tagsToAttractions.values()) {
                                tagItemsOut.addAll(items);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Attraction onCancelled: ", databaseError.toException());
            }
        });
    }

}