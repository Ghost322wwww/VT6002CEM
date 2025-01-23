package com.example.newproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AiChatRoomActivity extends AppCompatActivity {
    private CardView btnSendMsg;
    private EditText textMsg;
    private ImageButton btn_back;
    private RecyclerView chatRecyclerView;
    private AiChatRoomAdapter chatAdapter;
    private ArrayList<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat_room);

        btnSendMsg = findViewById(R.id.btn_sendMsg);
        textMsg = findViewById(R.id.text_msg);
        chatRecyclerView = findViewById(R.id.msgAdapter);
        btn_back = findViewById(R.id.btn_back_home);

        messages = new ArrayList<>();
        chatAdapter = new AiChatRoomAdapter(this, messages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = textMsg.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    chatAdapter.sendMessage(userMessage);
                    textMsg.setText("");
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AiChatRoomActivity.this, homePage.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
