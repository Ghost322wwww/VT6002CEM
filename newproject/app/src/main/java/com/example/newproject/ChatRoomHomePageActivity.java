package com.example.newproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.ChatRoomAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomHomePageActivity extends AppCompatActivity {

    FirebaseAuth auth;;
    FirebaseDatabase db;
    ArrayList<ChatRoom> chatRoomArrayList;
    ChatRoomAdapter chatRoomAdapter;
    FirebaseUser currentUser;
    Map<String, String> friendsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_home_page);

        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        friendsMap = loadFriendsList(currentUser.getUid());

        RecyclerView chatRoomRecyclerView = findViewById(R.id.chatRoomRecycleView);
        chatRoomRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatRoomArrayList = new ArrayList<>();
        chatRoomAdapter = new ChatRoomAdapter(ChatRoomHomePageActivity.this, chatRoomArrayList);
        chatRoomRecyclerView.setAdapter(chatRoomAdapter);

        if (currentUser != null) {
            loadChatRooms();
        }

        ImageButton btnPlanning = findViewById(R.id.btn_planning);
        ImageButton btnProfile = findViewById(R.id.btn_profile);
        ImageButton btnHomePage = findViewById(R.id.btn_Msg);
        ImageButton btn_back_home = findViewById(R.id.btn_back_home);
        ImageButton btnChatRoomCtrl = findViewById(R.id.btn_chatRoomCtorl);
        ImageButton btnMapPage = findViewById(R.id.btn_map);
        btnChatRoomCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        btnPlanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomHomePageActivity.this, planPage.class);
                startActivity(intent);
            }
        });


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomHomePageActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        btnMapPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomHomePageActivity.this, mapPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatRoomHomePageActivity.this, homePage.class);
                startActivity(intent);
                finish();
            }
        });

        btn_back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoomHomePageActivity.this, homePage.class);
                startActivity(intent);
                finish();
            }
        });

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(ChatRoomHomePageActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        chatRoomAdapter.notifyDataSetChanged();
    }

    private Map<String, String> loadFriendsList(String currentUserId) {
        Map<String, String> friendsMap = new HashMap<>();

        DatabaseReference ref = db.getReference().child("friends").child(currentUserId).child("FriendList");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();

                    // Get friend's name
                    db.getReference().child("user").child(id).child("name")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String name = dataSnapshot.getValue(String.class);
                                    friendsMap.put(id, name);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle cancelled error
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle cancelled error
            }
        });

        return friendsMap;
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chat Room Control");
        builder.setItems(new String[]{"Find new friends", "Manage friend requests", "Create Group Chat"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        handleFindNewFriends();
                        break;
                    case 1:
                        handleManageRequests();
                        break;
                    case 2:
                        handleCreateGroupChat();
                        break;
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void handleFindNewFriends() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Find New Friends");

        // Set up the input
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint("Enter email or phone to search user"); // Add hint to the input field
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String enteredData = input.getText().toString();
                boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(enteredData).matches();
                boolean isPhone = Patterns.PHONE.matcher(enteredData).matches();

                if (isEmail || isPhone) {
                    // Search for the user in the database
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("user");
                    Query query = reference.orderByChild(isEmail ? "email" : "phone").equalTo(enteredData);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Get the username from the snapshot
                                String username = "";
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    username = snapshot.child("name").getValue(String.class);
                                }
                                // Ask the user if they want to send a friend request
                                new AlertDialog.Builder(ChatRoomHomePageActivity.this)
                                        .setTitle("User Found")
                                        .setMessage("Do you want to send a friend request to " + username + "?") // Update message
                                        .setNegativeButton("No", null)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                sendFriendRequest(dataSnapshot);
                                            }
                                        })
                                        .show();
                            } else {
                                Toast.makeText(ChatRoomHomePageActivity.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    Toast.makeText(ChatRoomHomePageActivity.this, "Please enter a valid email or phone number.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void sendFriendRequest(DataSnapshot userSnapshot) {
        // Get the current user's ID
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (DataSnapshot snapshot : userSnapshot.getChildren()) {
            // Get the friend's ID
            String friendId = snapshot.getKey();

            // Get a reference to the 'FriendRequests' node for the friend (receiver)
            DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friends").child(friendId).child("FriendRequests");

            // Set the friend request's value to "pending". Use the current user's ID as the key.
            friendRequestRef.child(currentUserId).setValue("pending")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ChatRoomHomePageActivity.this, "Friend request sent.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatRoomHomePageActivity.this, "Failed to send friend request.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void handleManageRequests() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String currentUserID = mAuth.getCurrentUser().getUid();
        DatabaseReference friendRequestsRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID).child("FriendRequests");

        friendRequestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user has received friend requests
                    // Redirect to the ManageFriendRequests activity
                    Intent intent = new Intent(ChatRoomHomePageActivity.this, ManageFriendRequests.class);
                    startActivity(intent);
                } else {
                    // The user has not received any friend requests
                    Toast.makeText(ChatRoomHomePageActivity.this, "There are currently no friend requests", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Failed to read value: " + databaseError.toException());
            }
        });
    }

    private void handleCreateGroupChat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Group Chat");

        final EditText groupNameInput = new EditText(this);
        groupNameInput.setHint("Enter the group name");

        builder.setView(groupNameInput);

        builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameInput.getText().toString().trim();

                // Validate group name
                if (groupName.isEmpty()) {
                    Toast.makeText(ChatRoomHomePageActivity.this, "Group name is required.", Toast.LENGTH_SHORT).show();
                } else {
                    // Create a new group chat in Firebase
                    DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("groups");
                    String groupId = groupsRef.push().getKey();

                    HashMap<String, Object> members = new HashMap<>();
                    members.put(currentUser.getUid(), "admin"); // the group creator is an admin

                    HashMap<String, Object> group = new HashMap<>();
                    group.put("name", groupName);
                    group.put("members", members); // members and roles

                    // Create a chatroom for the group
                    DatabaseReference chatroomsRef = FirebaseDatabase.getInstance().getReference().child("chatrooms");

                    String chatroomId = chatroomsRef.push().getKey();

                    handleAddFriendsToGroupChat(group, groupId, chatroomId); // show dialog to add friends to group chat


                    HashMap<String, String> chatroom = new HashMap<>();
                    chatroom.put("id", chatroomId);
                    chatroom.put("name", groupName);
                    chatroom.put("isGroupChat", "true");

                    // Adding users to the chatroom
                    chatroom.put("user1", currentUser.getUid());

                    chatroomsRef.child(chatroomId).setValue(chatroom);

                    chatroomsRef.child(chatroomId).setValue(chatroom)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ChatRoomHomePageActivity.this, "Chatroom created successfully.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChatRoomHomePageActivity.this, "Failed to create chatroom.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
    private void handleAddFriendsToGroupChat(HashMap<String, Object> group, String groupId, String chatroomId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Friends to Group Chat");

        // Convert map values to an array
        String[] friendsArray = friendsMap.values().toArray(new String[0]);
        boolean[] checkedItems = new boolean[friendsArray.length];

        builder.setMultiChoiceItems(friendsArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Update the current checked items
                checkedItems[which] = isChecked;
            }
        });

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Object> members = new HashMap<>(); // to store members and their roles
                members.put(currentUser.getUid(), "admin"); // the group creator is an admin

                Map<String, Object> chatroomUsersAdded = new HashMap<>(); // to store members added to the chatroom
                chatroomUsersAdded.put("user1", currentUser.getUid());

                int userCounter = 2;

                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        members.put((new ArrayList<>(friendsMap.keySet())).get(i), "user"); // others are users
                        chatroomUsersAdded.put("user"+userCounter, (new ArrayList<>(friendsMap.keySet())).get(i));
                        userCounter++;
                    }
                }
                group.put("members", members); // add members to the group

                DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference().child("groups");

                groupsRef.child(groupId).setValue(group)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChatRoomHomePageActivity.this, "Group chat created successfully.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatRoomHomePageActivity.this, "Failed to create group chat.", Toast.LENGTH_SHORT).show();
                            }
                        });

                DatabaseReference chatroomsRef = FirebaseDatabase.getInstance().getReference().child("chatrooms");

                chatroomsRef.child(chatroomId).updateChildren(chatroomUsersAdded);
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }
    private void loadChatRooms() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            return;
        }
        String currentUserID = currentUser.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("chatrooms");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRoomArrayList.clear();

                for (DataSnapshot chatRoomSnapshot : dataSnapshot.getChildren()) {
                    ChatRoom chatRoom = chatRoomSnapshot.getValue(ChatRoom.class);
                    if (chatRoom != null) {
                        HashMap<String,String> members = new HashMap<>();
                        boolean currentUserIsMember = false;
                        for (DataSnapshot userSnapshot : chatRoomSnapshot.getChildren()) {
                            if (userSnapshot.getKey().equals("id") || userSnapshot.getKey().equals("name") || userSnapshot.getKey().equals("isGroupChat")) {
                                continue;
                            }
                            Object snapshotValue = userSnapshot.getValue();
                            if (snapshotValue instanceof String) {
                                members.put(userSnapshot.getKey(), (String) snapshotValue);
                                if (((String) snapshotValue).equals(currentUserID)) {
                                    currentUserIsMember = true;
                                }
                            } else {
                                GenericTypeIndicator<HashMap<String,Object>> t = new GenericTypeIndicator<HashMap<String, Object>>() {};
                                HashMap<String,Object> userIdMap = userSnapshot.getValue(t);

                                for (Map.Entry<String,Object> entry : userIdMap.entrySet()) {
                                    String key = entry.getKey();
                                    String value = String.valueOf(entry.getValue());
                                    members.put(key, value);
                                    if (value.equals(currentUserID)) {
                                        currentUserIsMember = true;
                                    }
                                }
                            }
                        }

                        if (currentUserIsMember) {
                            chatRoom.setMembers(members);
                            chatRoomArrayList.add(chatRoom);
                        }
                        chatRoomAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("InfoWindowFragment", "DatabaseError: " + databaseError.getMessage());
            }
        });
    }
}



