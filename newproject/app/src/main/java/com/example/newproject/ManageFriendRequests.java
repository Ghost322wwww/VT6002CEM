package com.example.newproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.FriendsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageFriendRequests extends AppCompatActivity {

    FirebaseAuth auth;
    RecyclerView chatRoomRecycleView;
    FriendsAdapter adapter;
    FirebaseDatabase db;
    ArrayList<Users> friendsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_home_page);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        String currentUserID = auth.getCurrentUser().getUid();
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID).child("FriendRequests");

        friendsArrayList = new ArrayList<>();

        friendRequestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                friendsArrayList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String requestId = snapshot.getKey();
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("user").child(requestId);
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                            Users user = userSnapshot.getValue(Users.class);
                            if (user != null) {
                                friendsArrayList.add(user);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        chatRoomRecycleView = findViewById(R.id.chatRoomRecycleView);
        chatRoomRecycleView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendsAdapter(ManageFriendRequests.this, friendsArrayList);
        chatRoomRecycleView.setAdapter(adapter);

        ImageButton btnProfile = findViewById(R.id.btn_profile);
        ImageButton btnHomePage = findViewById(R.id.btn_Msg);
        ImageButton btn_back_home = findViewById(R.id.btn_back_home);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageFriendRequests.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace HomeActivity with your actual home page activity
                Intent intent = new Intent(ManageFriendRequests.this, ChatRoomHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageFriendRequests.this, ChatRoomHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(ManageFriendRequests.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
