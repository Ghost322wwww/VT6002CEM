package com.example.newproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailsActivity extends AppCompatActivity {
    private ImageView backArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        String upcomingItiName = getIntent().getStringExtra("upcomingItiName");
        DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference("upcoming").child(upcomingItiName);

        backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ValueEventListener homesiteListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String photo = dataSnapshot.child("photo").getValue(String.class);
                int imageResourceId = getResources().getIdentifier(photo, "drawable", getPackageName());
                ImageView eventImageView = findViewById(R.id.imageView3);
                if (eventImageView != null) eventImageView.setImageResource(imageResourceId);


                String description = dataSnapshot.child("description").getValue(String.class);
                TextView descriptionView = findViewById(R.id.textView9);
                if (descriptionView != null) descriptionView.setText(description);

                TextView eventNameView = findViewById(R.id.textView7);
                if(eventNameView != null) eventNameView.setText(dataSnapshot.getKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w("DetailsActivity", "loadPost:onCancelled", databaseError.toException());
            }
        };
        eventReference.addValueEventListener(homesiteListener);
    }
}