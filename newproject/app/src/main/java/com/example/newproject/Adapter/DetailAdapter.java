package com.example.newproject.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private final Context context;
    private final List<String> activities;
    private final String planID;
    private final String tabName;

    public DetailAdapter(List<String> activities, Context context, String planID, String tabName) {
        this.activities = activities;
        this.context = context;
        this.planID = planID;
        this.tabName = tabName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_detailitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String activityDetail = activities.get(position);
        String[] splitDetail = activityDetail.split(",");
        holder.textViewTime.setText(splitDetail[1]);
        holder.textViewPlan.setText(splitDetail[0]);

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    String activityWithTime = activities.get(pos);
                    String[] parts = activityWithTime.split(",");
                    String activityName = parts[0];

                    activities.remove(pos);
                    notifyItemRemoved(pos);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Activity")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(planID)
                            .child(tabName)
                            .child(activityName);
                    ref.removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTime;
        public TextView textViewPlan;
        public ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.txt_time);
            textViewPlan = itemView.findViewById(R.id.txt_plan);
            btnDelete = itemView.findViewById(R.id.btn_del);
        }
    }
}