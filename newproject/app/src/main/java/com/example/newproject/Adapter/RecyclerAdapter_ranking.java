package com.example.newproject.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Area;
import com.example.newproject.R;

import java.util.List;

public class RecyclerAdapter_ranking extends RecyclerView.Adapter<RecyclerAdapter_ranking.RankingViewHolder> {

    private List<Area> area;

    public RecyclerAdapter_ranking(List<Area> areaItems) {
        this.area = areaItems;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_rankitem_layout, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Area areas = area.get(position);

        String districtName = areas.getDistricts().get(0).getName();
        holder.rankdistrict.setText(districtName);

        String areaName = areas.getName();
        holder.rankarea.setText(areaName);
    }

    @Override
    public int getItemCount() {
        return area.size();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        ImageView rankimg;
        TextView rankdistrict, rankarea;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            rankimg = itemView.findViewById(R.id.rankimg);
            rankdistrict = itemView.findViewById(R.id.rankdistrict);
            rankarea = itemView.findViewById(R.id.rankarea);
        }
    }
}