package com.example.newproject;

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
    private RecyclerView chatRecyclerView;
    private AiChatRoomAdapter chatAdapter;
    private ArrayList<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aichat_room);

        // 初始化 UI 元件
        btnSendMsg = findViewById(R.id.btn_sendMsg);
        textMsg = findViewById(R.id.text_msg);
        chatRecyclerView = findViewById(R.id.msgAdapter);

        messages = new ArrayList<>();
        chatAdapter = new AiChatRoomAdapter(this, messages);

        // 設置 RecyclerView
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // 發送訊息按鈕事件
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
    }
}
