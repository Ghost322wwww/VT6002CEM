package com.example.newproject.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Plan;
import com.example.newproject.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

    private List<Plan> planList;

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        public TextView tripNameTextView;
        public TextView startDayTextView;
        public TextView endDayTextView;

        public PlanViewHolder(View v) {
            super(v);
            tripNameTextView = v.findViewById(R.id.sampleitinerary);
            startDayTextView = v.findViewById(R.id.startDay);
            endDayTextView = v.findViewById(R.id.endDay);
        }
    }

    public PlanAdapter(List<Plan> planList) {
        this.planList = planList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_litinerary, parent, false);
        return new PlanViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = planList.get(position);
        holder.tripNameTextView.setText(plan.getTripName());
        holder.startDayTextView.setText(convertTimestampToDate(plan.getStartDate()));
        holder.endDayTextView.setText(convertTimestampToDate(plan.getEndDate()));
    }

    @Override
    public int getItemCount() {
        return planList.size();
    }

    private String convertTimestampToDate(long timestamp) {
        Date date = new Date(timestamp);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
        return df.format(date);
    }
}