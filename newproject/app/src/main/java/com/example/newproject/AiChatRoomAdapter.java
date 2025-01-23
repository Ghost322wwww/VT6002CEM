package com.example.newproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.R;
import com.example.newproject.GPTClient;
import com.example.newproject.GPTRequest;
import com.example.newproject.GPTResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

public class AiChatRoomAdapter extends RecyclerView.Adapter<AiChatRoomAdapter.ChatViewHolder> {
    private Context context;
    private ArrayList<String> messages;

    public AiChatRoomAdapter(Context context, ArrayList<String> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.messageText.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
        }
    }

    // 發送用戶訊息並請求 AI 回應
    public void sendMessage(String userMessage) {
        messages.add("你: " + userMessage);
        notifyDataSetChanged();

        GPTRequest request = new GPTRequest(userMessage);
        GPTClient.getGPTService().sendMessage(request).enqueue(new Callback<GPTResponse>() {
            @Override
            public void onResponse(Call<GPTResponse> call, Response<GPTResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messages.add("AI: " + response.body().getText());
                    notifyDataSetChanged();
                } else {
                    messages.add("AI: 無法回應，請稍後再試。");
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<GPTResponse> call, Throwable t) {
                messages.add("AI: 發生錯誤，請檢查網絡連線。");
                notifyDataSetChanged();
            }
        });
    }
}
