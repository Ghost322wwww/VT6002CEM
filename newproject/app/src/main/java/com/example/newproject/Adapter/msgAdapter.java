package com.example.newproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.R;
import com.example.newproject.Users;
import com.example.newproject.msgModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class msgAdapter extends RecyclerView.Adapter {
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private String senderUid;
    Context context;
    ArrayList<msgModel> messagesAdapterArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECIVE = 2;
    boolean isGroupChat;

    public msgAdapter(Context context, ArrayList<msgModel> messagesAdapterArrayList, String senderUid,boolean isGroupChat) {
        this.context = context;
        this.messagesAdapterArrayList = messagesAdapterArrayList;
        this.senderUid = senderUid;
        this.isGroupChat = isGroupChat;
    }

    class senderViewHoler extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtext;
        ImageView imgView;

        public senderViewHoler(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtext = itemView.findViewById(R.id.msgsendertyp);
            imgView = itemView.findViewById(R.id.imgsendertyp);

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            reference = FirebaseDatabase.getInstance().getReference("user").child(senderUid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    String sender_img_url = user.getUserImg();
                    Picasso.get().load(sender_img_url).resize(200, 200).centerCrop().into(circleImageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    class reciverViewHoler extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtext;
        ImageView imgView;
        msgModel message;

        public reciverViewHoler(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            msgtext = itemView.findViewById(R.id.recivertextset);
            imgView = itemView.findViewById(R.id.imgrecivertype);
        }

        public void setData(msgModel message){
            this.message = message;
            String senderId = message.getSenderid();
            reference = FirebaseDatabase.getInstance().getReference("user").child(senderId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users user = dataSnapshot.getValue(Users.class);
                    String sender_img_url = user.getUserImg();
                    Picasso.get().load(sender_img_url).resize(200, 200).centerCrop().into(circleImageView);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHoler(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new reciverViewHoler(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModel messages = messagesAdapterArrayList.get(position);

        if (holder instanceof senderViewHoler) {
            senderViewHoler viewHolder = (senderViewHoler) holder;

            if (messages.getImagUrl() != null && !messages.getImagUrl().isEmpty()){
                Picasso.get().load(messages.getImagUrl()).resize(200, 200).centerCrop().into(viewHolder.imgView);
                viewHolder.msgtext.setVisibility(View.GONE);
                viewHolder.imgView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.msgtext.setText(messages.getMsg());
                viewHolder.msgtext.setVisibility(View.VISIBLE);
                viewHolder.imgView.setVisibility(View.GONE);
            }
        }
        else if (holder instanceof reciverViewHoler) {
            reciverViewHoler viewHolder = (reciverViewHoler) holder;
            viewHolder.setData(messages);
            if (messages.getImagUrl() != null && !messages.getImagUrl().isEmpty()){
                Picasso.get().load(messages.getImagUrl()).resize(200, 200).centerCrop().into(viewHolder.imgView);
                viewHolder.msgtext.setVisibility(View.GONE);
                viewHolder.imgView.setVisibility(View.VISIBLE);
            } else {
                viewHolder.msgtext.setText(messages.getMsg());
                viewHolder.msgtext.setVisibility(View.VISIBLE);
                viewHolder.imgView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messagesAdapterArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        msgModel messages = messagesAdapterArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }
}