package com.example.newproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.msgAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ChatRoomActivity extends AppCompatActivity {

    String reciverUid, chatRoomName, senderUid;
    ImageButton btn_back;

    private static final int CAMERA_REQUEST_CODE = 100;
    private Uri cameraImageUri;
    CardView btn_sendMsg;
    EditText text_msg;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    String senderRoom, reciverRoom;
    RecyclerView messagesAdapter;
    ArrayList<msgModel> msgModelArrayList;
    msgAdapter msgAdapter;
    TextView TV_recivername;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference mStorageRef;
    String chatRoomId;
    boolean isGroupChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        mAuth = FirebaseAuth.getInstance();
        messagesAdapter = findViewById(R.id.msgAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        msgModelArrayList = new ArrayList<>();
        messagesAdapter.setLayoutManager(linearLayoutManager);

        chatRoomName = getIntent().getStringExtra("chatRoomName");
        TV_recivername = findViewById(R.id.receivernametv);
        TV_recivername.setText(chatRoomName);

        btn_sendMsg = findViewById(R.id.btn_sendMsg);
        btn_back = findViewById(R.id.btn_back_home);
        text_msg = findViewById(R.id.text_msg);

        senderUid = FirebaseAuth.getInstance().getUid();
        senderRoom =  senderUid + "/" + reciverUid;
        reciverRoom = reciverUid + "/" + senderUid;
        messagesAdapter.setAdapter(msgAdapter);

        db = FirebaseDatabase.getInstance();
        chatRoomId = getIntent().getStringExtra("chatRoomId");
        DatabaseReference chatRoomRef = db.getReference().child("chatRooms").child(chatRoomId);
        chatRoomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                isGroupChat = chatRoom != null && "true".equals(chatRoom.getIsGroupChat());
                msgAdapter = new msgAdapter(ChatRoomActivity.this, msgModelArrayList, senderUid, isGroupChat);
                messagesAdapter.setAdapter(msgAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        DatabaseReference chatReference = db.getReference().child("chats").child(chatRoomId).child("messages");

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        chatReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msgModelArrayList.clear();
                for (DataSnapshot dbSnapshot : snapshot.getChildren()) {
                    msgModel message = dbSnapshot.getValue(msgModel.class);
                    msgModelArrayList.add(message);
                }
                if (msgAdapter != null) {
                    msgAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoomActivity.this, ChatRoomHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        text_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendMessage();
                    return true;
                }
                return false;
            }
        });

        // image upload button click event
        CardView uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        CardView cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

    }

    void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                cameraImageUri = FileProvider.getUriForFile(
                        this,
                        "com.example.newproject.fileprovider",
                        photoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No Camera App Available", Toast.LENGTH_SHORT).show();
        }
    }


    private File createImageFile() {
        String fileName = "IMG_" + System.currentTimeMillis();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = null;
        try {
            imageFile = File.createTempFile(
                    fileName,
                    ".jpg",
                    storageDir
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }




    void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            sendImageMessage(selectedImageUri);
        }

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (cameraImageUri != null) {
                sendImageMessage(cameraImageUri);
            } else {
                Toast.makeText(this, "Camera image capture failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void sendImageMessage(Uri imageUri) {
        if (imageUri == null) {
            Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 1. 读取图片并压缩
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] compressedData = baos.toByteArray();

            final Date date = new Date();
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");
            
            UploadTask uploadTask = fileReference.putBytes(compressedData);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            msgModel imageMessage = new msgModel("", senderUid, imageUrl, date.getTime());
                            sendMessage(imageMessage);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatRoomActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Image compression failed", Toast.LENGTH_SHORT).show();
        }
    }


    void sendMessage(msgModel message) {
        db.getReference().child("chats").child(chatRoomId).child("messages").push()
                .setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    void sendMessage() {
        final String msg = text_msg.getText().toString();
        if (msg.isEmpty() && imageUri == null) {
            Toast.makeText(ChatRoomActivity.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
        }
        else {
            text_msg.setText("");
            final Date date = new Date();
            if (imageUri != null) {
                final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String imageUrl = uri.toString();
                                        msgModel imageMessage = new msgModel("", senderUid, imageUrl, date.getTime());
                                        sendMessage(imageMessage);
                                        imageUri = null;
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ChatRoomActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else {
                // No image to send, just a regular message
                msgModel messages = new msgModel(msg, senderUid, date.getTime());
                // send the text message
                sendMessage(messages);
            }
        }
    }
    }