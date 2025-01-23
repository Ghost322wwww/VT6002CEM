package com.example.newproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.R;
import com.example.newproject.model.SearchResultItem;

import java.util.ArrayList;

public class SearchResultRecyclerAdapter extends RecyclerView.Adapter<SearchResultRecyclerAdapter.MyViewHolder> {

    private ArrayList<SearchResultItem> mDataset;
    private Context mContext;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mNameView;
        public TextView mAddressView;

        public MyViewHolder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.search_result_item_image);
            mNameView = v.findViewById(R.id.search_result_item_name);
            mAddressView = v.findViewById(R.id.search_result_item_address);
        }
    }

    public SearchResultRecyclerAdapter(Context context, ArrayList<SearchResultItem> myDataset) {
        this.mContext = context;
        this.mDataset = myDataset;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mImageView.setImageResource(mDataset.get(position).getImageResource());
        holder.mNameView.setText(mDataset.get(position).getName());
        holder.mAddressView.setText(mDataset.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}