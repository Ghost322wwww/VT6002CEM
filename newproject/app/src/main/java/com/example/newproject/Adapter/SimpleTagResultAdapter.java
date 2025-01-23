package com.example.newproject.Adapter;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.ChatRoom;
import com.example.newproject.InfoWindowFragment;
import com.example.newproject.LoginActivity;
import com.example.newproject.R;
import com.example.newproject.Users;
import com.example.newproject.model.SimpleTagResultItem;
import com.example.newproject.msgModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleTagResultAdapter extends RecyclerView.Adapter<SimpleTagResultAdapter.ViewHolder>{
    private List<SimpleTagResultItem> mTagResultList;
    private ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textViewAttrName, textViewAttrDistrict, textViewAttrTags;
        public ImageButton shareButton,bookmarkButton;

        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.tag_result_item_img);
            textViewAttrName = view.findViewById(R.id.tag_result_item_attr_name);
            textViewAttrDistrict = view.findViewById(R.id.tag_result_item_district);
            textViewAttrTags = view.findViewById(R.id.tag_result_item_details);
            shareButton = view.findViewById(R.id.shareButton);
            bookmarkButton= view.findViewById(R.id.bookmarkButton);
        }
    }

    public SimpleTagResultAdapter(List<SimpleTagResultItem> tagResultList) {
        mTagResultList = tagResultList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tag_result_list_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleTagResultItem tag = mTagResultList.get(position);
        holder.imageView.setImageResource(tag.getImageResources());
        holder.textViewAttrName.setText(tag.getAttrName());
        holder.textViewAttrDistrict.setText(tag.getAttrDistrict());
        if (tag.getAttrTags() != null) {
            holder.textViewAttrTags.setText(String.join(", ", tag.getAttrTags()));
        } else {
            holder.textViewAttrTags.setText("");
        }
        holder.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) { // Check if item still exists
                    SimpleTagResultItem item = mTagResultList.get(position);
                    Map<String, String> bookmarkData = new HashMap<>();

                    bookmarkData.put("AttrName", item.getAttrName());
                    bookmarkData.put("AttrDistrict", item.getAttrDistrict());
                    bookmarkData.put("AttrTags", String.join(", ", item.getAttrTags()));


                    saveBookmarkToFirebase(bookmarkData);
                }
            }
        });
        holder.shareButton.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser == null) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Reminder")
                        .setMessage("You are not logged in yet, would you like to log in now?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(v.getContext(), LoginActivity.class);
                                v.getContext().startActivity(i);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            } else {
                loadChatRooms();
                if (chatRoomArrayList.size() == 0) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Reminder")
                            .setMessage("You don't have any chat rooms to share!")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    loadChatRoomNames(chatRoomArrayList, chatRoomNames -> {
                        boolean[] checkedItems = new boolean[chatRoomNames.size()];

                        new AlertDialog.Builder(v.getContext())
                                .setTitle("Share to:")
                                .setMultiChoiceItems(chatRoomNames.values().toArray(new CharSequence[0]), checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked)
                                .setPositiveButton("Share", (dialog, which) -> {
                                    for (int i = 0; i < checkedItems.length; i++) {
                                        if (checkedItems[i]) {
                                            String chatRoomId = chatRoomNames.keySet().toArray(new String[0])[i];

                                            String nameMessageText = "Name: " + tag.getAttrName();
                                            String districtMessageText = String.join(", ", tag.getAttrTags());

                                            // Create and send the name text message
                                            msgModel nameMessage = new msgModel(nameMessageText, currentUser.getUid(), "", System.currentTimeMillis());
                                            DatabaseReference nameRef = FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomId).child("messages");
                                            nameRef.push().setValue(nameMessage);

                                            // Create and send the district text message
                                            msgModel districtMessage = new msgModel(districtMessageText, currentUser.getUid(), "", System.currentTimeMillis());
                                            DatabaseReference districtRef = FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomId).child("messages");
                                            districtRef.push().setValue(districtMessage);
                                        }
                                    }
                                })
                                .show();
                    });
                }
            }
        });
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
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void loadChatRoomNames(ArrayList<ChatRoom> chatRooms, final InfoWindowFragment.OnDataFetchCallback callback) {
        final HashMap<String, String> chatRoomNames = new HashMap<>();

        for (final ChatRoom chatRoom : chatRooms) {
            String isGroupChat = chatRoom.getIsGroupChat();
            if (isGroupChat != null && isGroupChat.equals("true")) {
                chatRoomNames.put(chatRoom.getId(), chatRoom.getName());
            } else {
                String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                for (String otherUserId : chatRoom.getMembers().values()) {
                    if (!otherUserId.equals(currentUserId)) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("user").child(otherUserId);
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Users otherUser = dataSnapshot.getValue(Users.class);
                                if (otherUser != null) {
                                    chatRoomNames.put(chatRoom.getId(), otherUser.getName());
                                }
                                if (chatRoomNames.size() == chatRooms.size()) {
                                    if (callback != null) {
                                        callback.onData(chatRoomNames);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d("InfoWindowFragment", "Failed to load user data for ID: " + otherUserId);
                            }
                        });
                    }
                }
            }
        }
    }
    @Override
    public int getItemCount() {
        return mTagResultList.size();
    }
    private void saveBookmarkToFirebase(Map<String, String> bookmarkData) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference("BookMark").child(uid);
            bookmarkRef.push().setValue(bookmarkData).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Bookmark", "Bookmark saved successfully!");
                    } else {
                        Log.d("Bookmark", "Failed to save bookmark!", task.getException());
                    }
                }
            });
        } else {
            Log.d("Bookmark", "No user is currently signed in");
        }
    }
}
