package com.example.newproject;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class AiChatRoomActivity extends AppCompatActivity {
    ImageButton btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat_room); // Ensure this layout file exists
    }
}