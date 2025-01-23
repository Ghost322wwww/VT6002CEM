package com.example.newproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.newproject.model.PlaceInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoWindowFragment extends Fragment {
    private ArrayList<ChatRoom> chatRoomArrayList = new ArrayList<>();
    private TextView title;
    private ImageView placephoto;
    private TextView snippet;
    private ImageButton shareButton, bookmarkButton, cancelButton;
    private PlaceInfo placeInfo;

    public interface OnInfoWindowFragmentInteractionListener {
        void onCloseInfoWindowFragment();
    }

    private OnInfoWindowFragmentInteractionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnInfoWindowFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnInfoWindowFragmentInteractionListener");
        }
    }

    public InfoWindowFragment() {}

    public void setPlaceInfo(PlaceInfo placeInfo) {
        this.placeInfo = placeInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_info_window, container, false);

        this.title = view.findViewById(R.id.title);
        this.placephoto = view.findViewById(R.id.placephoto);
        this.snippet = view.findViewById(R.id.snippet);

        this.shareButton = view.findViewById(R.id.shareButton);
        this.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();

                if (currentUser == null) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Reminder")
                            .setMessage("You are not logged in yet, do you want to log in now?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else if (chatRoomArrayList.size() == 0) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Reminder")
                            .setMessage("You don't have any chat room to share!")
                            .setPositiveButton("OK", null)
                            .show();
                } else {
                    loadChatRoomNames(chatRoomArrayList, data -> {
                        CharSequence[] chatRoomNames = data.values().toArray(new CharSequence[0]);

                        boolean[] checkedItems = new boolean[chatRoomNames.length];

                        new AlertDialog.Builder(getContext())
                                .setTitle("Share to:")
                                .setMultiChoiceItems(chatRoomNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                        checkedItems[which] = isChecked;
                                    }
                                })
                                .setPositiveButton("Share", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Upload the bitmap to Firebase first and then create the message model with the image URL.
                                        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        placeInfo.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] byteArray = baos.toByteArray();

                                        // Create a temporary file, write the byte data into the file
                                        try {
                                            File tempImageFile = File.createTempFile("photo", "jpg", getContext().getCacheDir());
                                            try (FileOutputStream fos = new FileOutputStream(tempImageFile)) {
                                                fos.write(byteArray);
                                                fos.flush();
                                            }

                                            Uri fileUri = Uri.fromFile(tempImageFile);
                                            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");
                                            fileReference.putFile(fileUri)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    String imageURL = uri.toString();

                                                                    // Create the image message model with the image URL
                                                                    msgModel imageMessage = new msgModel("", currentUser.getUid(), imageURL, System.currentTimeMillis());

                                                                    // Create and send the text message
                                                                    msgModel textMessage = new msgModel(placeInfo.getName(), currentUser.getUid(), "", System.currentTimeMillis());

                                                                    // Loop through all the chat rooms and send the messages
                                                                    for (int i = 0; i < checkedItems.length; i++) {
                                                                        if (checkedItems[i]) {
                                                                            String chatRoomId = data.keySet().toArray(new String[0])[i];
                                                                            Log.d("InfoWindowFragment", "chatRoomId: "+chatRoomId);
                                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("chats").child(chatRoomId).child("messages");

                                                                            ref.push().setValue(imageMessage);
                                                                            ref.push().setValue(textMessage);
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception exception) {
                                                            Toast.makeText(getContext(), "Upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    });
                }
            }
        });
        this.bookmarkButton = view.findViewById(R.id.bookmarkButton);
        this.bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        this.cancelButton = view.findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCloseInfoWindowFragment();
            }
        });

        if (placeInfo != null) {
            title.setText(placeInfo.getName());
            snippet.setText(placeInfo.getAddress());
            placephoto.setImageBitmap(placeInfo.getPhoto());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadChatRooms();
    }

    @Override
    public void onResume() {
        super.onResume();
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
    private void loadChatRoomNames(ArrayList<ChatRoom> chatRooms, final OnDataFetchCallback callback) {
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

    public interface OnDataFetchCallback {
        void onData(HashMap<String, String> data);
    }
}