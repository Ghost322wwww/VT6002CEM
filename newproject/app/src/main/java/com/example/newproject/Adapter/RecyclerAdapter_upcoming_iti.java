package com.example.newproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.R;
import com.example.newproject.model.UpcomingItiItem;

import java.util.List;

public class RecyclerAdapter_upcoming_iti extends RecyclerView.Adapter<RecyclerAdapter_upcoming_iti.UpcomingItiViewHolder> {

    private List<UpcomingItiItem> upcomingItiItems;
    private OnItemClickListener mListener;

    public RecyclerAdapter_upcoming_iti(List<UpcomingItiItem> upcomingItiItems){
        this.upcomingItiItems = upcomingItiItems;
    }
    public interface OnItemClickListener {
        void onItemClick(UpcomingItiItem item);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UpcomingItiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_upcomingitiitem_layout, parent, false);
        return new UpcomingItiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingItiViewHolder holder, int position) {
        UpcomingItiItem item = upcomingItiItems.get(position);
        holder.itiimageView.setImageResource(item.getImageResources());
        holder.itidatetextview.setText(item.getupcomingitidate());
        holder.itinameview.setText(item.getupcomingitiname());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return upcomingItiItems.size();
    }

    static class UpcomingItiViewHolder extends RecyclerView.ViewHolder {
        ImageView itiimageView;
        TextView itidatetextview, itinameview;

        public UpcomingItiViewHolder(@NonNull View itemView) {
            super(itemView);
            itiimageView = itemView.findViewById(R.id.upcomingitiimg);
            itidatetextview = itemView.findViewById(R.id.upcomingitidate);
            itinameview = itemView.findViewById(R.id.upcomingitiname);

        }
    }
}