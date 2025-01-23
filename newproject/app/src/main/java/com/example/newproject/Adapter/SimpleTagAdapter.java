package com.example.newproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.R;
import com.example.newproject.model.SimpleTagItem;

import java.util.List;

public class SimpleTagAdapter extends RecyclerView.Adapter<SimpleTagAdapter.SimpleTagViewHolder> {

    public interface ItemClickListener {
        void onItemClick(int position);
    }

    private List<SimpleTagItem> simpleTagItems;
    private ItemClickListener clickListener;

    public SimpleTagAdapter(List<SimpleTagItem> simpleTagItems, @NonNull ItemClickListener clickListener){
        this.simpleTagItems = simpleTagItems;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SimpleTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_tag_filter_list_item_layout, parent, false);

        return new SimpleTagViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleTagViewHolder holder, int position) {
        SimpleTagItem item = simpleTagItems.get(position);
        holder.simpleTagTextView.setText(item.getTag());
    }

    @Override
    public int getItemCount() {
        return simpleTagItems.size();
    }

    static class SimpleTagViewHolder extends RecyclerView.ViewHolder {
        TextView simpleTagTextView;

        public SimpleTagViewHolder(@NonNull View itemView, final ItemClickListener listener) {
            super(itemView);
            simpleTagTextView = itemView.findViewById(R.id.tagFilterText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}