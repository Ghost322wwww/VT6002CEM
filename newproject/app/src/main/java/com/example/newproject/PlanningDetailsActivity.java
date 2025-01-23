package com.example.newproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.DetailAdapter;
import com.example.newproject.model.Activity123;
import com.google.android.material.tabs.TabLayout;
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
import java.util.Map;

public class PlanningDetailsActivity extends AppCompatActivity {
    private TextView nameOfTrip;
    private ImageView imageView;
    private EditText editTextActivity;
    private DatabaseReference mDatabase;
    private Button buttonAdd;
    private RecyclerView detailRecyclerView;
    private Map<String, List<String>> activitiesMap = new HashMap<>();
    private DetailAdapter adapter;
    private static final String[] initialDays = {"One", "Two", "Three"};
    static int count = 0;
    static String[] daysOfWeek = {"Four", "Five", "Six", "Sev"};
    private TimePicker timePicker;
    private String planID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planningdetails);
        planID = getIntent().getStringExtra("planID");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uID = firebaseUser.getUid();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Activity").child(uID).child(planID);
        nameOfTrip = findViewById(R.id.nameOfTirp);
        imageView = findViewById(R.id.back_arrow);
        editTextActivity = findViewById(R.id.editTextActivity);
        buttonAdd = findViewById(R.id.buttonAdd);
        detailRecyclerView = findViewById(R.id.detail_recycler);
        timePicker = findViewById(R.id.time_picker);

        String destinationCity = getIntent().getStringExtra("DESTINATION_CITY");
        nameOfTrip.setText(destinationCity);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final TabLayout tabLayout = findViewById(R.id.timeTagLayout);
        // Initialize first three tabs and "+" tab
        for (String day : initialDays) {
            tabLayout.addTab(tabLayout.newTab().setText(day));
            activitiesMap.put(day, new ArrayList<>());
        }
        tabLayout.addTab(tabLayout.newTab().setText("+"));

        setCurrentTabActivities("One");  // Set initial tab

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabName = tab.getText().toString();
                if (tabName.equals("+")) {
                    if (count < daysOfWeek.length) {
                        String newTabName = daysOfWeek[count];
                        tabLayout.addTab(tabLayout.newTab().setText(newTabName), tabLayout.getTabCount() - 1);
                        activitiesMap.put(newTabName, new ArrayList<>());
                        count++;
                    }
                } else {
                    setCurrentTabActivities(tabName);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (!tab.getText().toString().equals("+")) {
                    setCurrentTabActivities(tab.getText().toString());
                }
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activity = editTextActivity.getText().toString();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                String time = hour + ":" + minute;
                if (!activity.isEmpty() && !time.isEmpty()) {
                    String currentTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();
                    if (!currentTab.equals("+")) {
                        List<String> currentList = activitiesMap.get(currentTab);
                        if(currentList == null) {
                            currentList = new ArrayList<>(); // Initialize the list if it is null
                            activitiesMap.put(currentTab, currentList); // Put it back in the map
                        }
                        String activityWithTime = activity + "," + time;
                        currentList.add(activityWithTime);
                        activitiesMap.put(currentTab, currentList);
                        adapter.notifyDataSetChanged();
                        editTextActivity.setText("");
                        Activity123 activity123 = new Activity123(activity, time);

                        // Save activity under current day (currentTab)
                        DatabaseReference actRef = database.getReference("Activity")
                                .child(uID)
                                .child(planID)
                                .child(currentTab)
                                .child(activity);
                        actRef.setValue(activity123);
                    }
                }
            }
        });

        // Listen for changes in Firebase database
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Clear current activities map
                activitiesMap.clear();
                // Iterate through each tab
                for (DataSnapshot tabSnapshot : dataSnapshot.getChildren()) {
                    String tabName = tabSnapshot.getKey();  // Get tab name
                    List<String> activityWithTimeList = new ArrayList<>();
                    // Iterate through each activity in current tab
                    for (DataSnapshot activitySnapshot : tabSnapshot.getChildren()) {
                        Activity123 activity123 = activitySnapshot.getValue(Activity123.class);
                        String activityWithTime = activity123.getName() + "," + activity123.getTime();
                        activityWithTimeList.add(activityWithTime);
                    }
                    // Update activities map with new data
                    activitiesMap.put(tabName, activityWithTimeList);
                }
                // Update UI
                setCurrentTabActivities(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }

    private void setCurrentTabActivities(String tabName) {
        List<String> activities = activitiesMap.getOrDefault(tabName, new ArrayList<>());
        adapter = new DetailAdapter(activities, PlanningDetailsActivity.this, planID, tabName);
        detailRecyclerView.setAdapter(adapter);
        detailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}