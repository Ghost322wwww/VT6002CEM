package com.example.newproject.Adapter;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.ChatRoomHomePageActivity;
import com.example.newproject.R;
import com.example.newproject.Users;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.viewholder> {
    Context context;
    ArrayList<Users> friendsArrayList;

    public FriendsAdapter(Context context, ArrayList<Users> friendsArrayList) {
        this.context = context;
        this.friendsArrayList = friendsArrayList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {

        Users users = friendsArrayList.get(position);
        holder.username.setText(users.getName());

        Picasso.get().load(users.getUserImg()).into(holder.userimg);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserID = auth.getCurrentUser().getUid();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you accept the friend request from " + users.getName() + "?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User accepted the friend request, add friend's id to friendList and delete from FriendRequests
                                DatabaseReference friendListRefB = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID).child("FriendList");

                                // Generate unique chat room id
                                DatabaseReference chatRoomRef = FirebaseDatabase.getInstance().getReference().child("chats");
                                String chatRoomId = chatRoomRef.push().getKey();

                                // Update chat room id in the friend's details
                                friendListRefB.child(users.getId()).setValue(true);
                                friendListRefB.child(users.getId()).child("ChatRoomId").setValue(chatRoomId)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DatabaseReference friendListRefA = FirebaseDatabase.getInstance().getReference().child("friends").child(users.getId()).child("FriendList");
                                                friendListRefA.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                                                friendListRefA.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ChatRoomId").setValue(chatRoomId)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID).child("FriendRequests");
                                                                friendRequestRef.child(users.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        friendsArrayList.remove(users); // Remove the user from current list
                                                                        DatabaseReference chatRoomsRef = FirebaseDatabase.getInstance().getReference().child("chatrooms");
                                                                        HashMap<String, String> newChatRoom = new HashMap<>();
                                                                        newChatRoom.put("id", chatRoomId);
                                                                        newChatRoom.put("user1", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                        newChatRoom.put("user2", users.getId());
                                                                        chatRoomsRef.child(chatRoomId).setValue(newChatRoom);

                                                                        refreshPageOrReturnHome();
                                                                    }
                                                                });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User denied the friend request, remove friend's request from FriendRequests
                                DatabaseReference friendRequestRef = FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID).child("FriendRequests");
                                friendRequestRef.child(users.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendsArrayList.remove(users); // Remove the user from current list
                                        refreshPageOrReturnHome();
                                    }
                                });
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void refreshPageOrReturnHome() {
        if (friendsArrayList.isEmpty()) {
            // If there are no more friend requests, return to the ChatRoomHomePageActivity
            Intent intent = new Intent(context, ChatRoomHomePageActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            ((Activity) context).finish();
        } else {
            // If there are still friend requests, refresh the page
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return friendsArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView userimg;
        TextView username;
        TextView userPhone;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            userimg = itemView.findViewById(R.id.userimg);
            username = itemView.findViewById(R.id.username);
        }
    }
}
