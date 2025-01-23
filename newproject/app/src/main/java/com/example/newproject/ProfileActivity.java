package com.example.newproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 123;
    TextView profileName, profileEmail, profilePhone;
    TextView titleName;
    ImageView profileImg;
    Button editProfile, completeEdit,logoutButton, AiChat;

    FirebaseAuth auth;
    DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("user");

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);
        titleName = findViewById(R.id.titleName);
        editProfile = findViewById(R.id.editButton);
        completeEdit = findViewById(R.id.completeEditButton);
        profileImg = findViewById(R.id.profileImg);
        logoutButton = findViewById(R.id.logout_button);
        AiChat = findViewById(R.id.AiChatButton);

        titleName.setText("User upload the profile picture");

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        titleName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            showUserData();
        } else {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });

        completeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, homePage.class);
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        AiChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, AiChatRoomActivity.class);
                startActivity(intent);
            }
        });
    }

    public void showUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();

        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nameFromDB = snapshot.child("name").getValue(String.class);
                    String emailFromDB = snapshot.child("email").getValue(String.class);
                    String phoneFromDB = snapshot.child("phone").getValue(String.class);
                    String userImgFromDB = snapshot.child("userImg").getValue(String.class);

                    profileName.setText(nameFromDB);
                    profileEmail.setText(emailFromDB);
                    profilePhone.setText(phoneFromDB);
                    Glide.with(ProfileActivity.this).load(userImgFromDB).into(profileImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void passUserData() {
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE) {
            if(resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("user_images");
                StorageReference imageReference = storageReference.child(imageUri.getLastPathSegment());
                imageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageURL = uri.toString();
                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("user").child(firebaseUser.getUid());
                                userReference.child("userImg").setValue(imageURL);
                                Glide.with(ProfileActivity.this).load(imageURL).into(profileImg);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Upload error", e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Upload error", e.getMessage());
                    }
                });

            } else if(resultCode == RESULT_CANCELED) {
                // User cancelled the action
            }
        }
    }
    public void logout() {
        auth.signOut();  // sign out from firebase
        Intent intent = new Intent(ProfileActivity.this, homePage.class);
        startActivity(intent);
        finish();  // end this activity
    }
}