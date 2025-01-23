package com.example.newproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class AiChatRoomActivity extends AppCompatActivity {
    ImageButton btn_back;
    EditText text_msg;
    CardView btn_sendMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat_room); // Ensure this layout file exists

        btn_sendMsg = findViewById(R.id.btn_sendMsg);
        btn_back = findViewById(R.id.btn_back_home);
        text_msg = findViewById(R.id.text_msg);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatRoomActivity.this, ChatRoomHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
        
    }

}