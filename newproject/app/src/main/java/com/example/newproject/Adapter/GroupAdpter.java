package com.example.newproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.ChatRoomActivity;
import com.example.newproject.Group;
import com.example.newproject.R;

import java.util.ArrayList;

public class GroupAdpter extends RecyclerView.Adapter<GroupAdpter.viewholder> {
    Context context;
    ArrayList<Group> groupArrayList;

    public GroupAdpter(Context context, ArrayList<Group> groupArrayList) {
        this.context = context;
        this.groupArrayList = groupArrayList;
    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewholder holder, int position) {
        Group group = groupArrayList.get(position);
        holder.groupName.setText(group.getGroupName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("groupname",group.getGroupName());
                intent.putExtra("groupId",group.getGroupId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupArrayList.size();
    }

    class viewholder extends RecyclerView.ViewHolder {
        TextView groupName;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.username);
        }
    }
}