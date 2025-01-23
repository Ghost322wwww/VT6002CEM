package com.example.newproject.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.PlanningDetailsActivity;
import com.example.newproject.R;
import com.example.newproject.model.itinerary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItineraryViewHolder> {
    private static final String TAG = "ItineraryAdapter";
    private List<itinerary> itineraryList;
    private  Context context;

    private DatabaseReference databaseReference;
    public ItineraryAdapter(Context context, List<itinerary> itineraryList){
        this.context = context;
        this.itineraryList = itineraryList;
        this.databaseReference = FirebaseDatabase.getInstance().getReference("plan")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }


    @NonNull
    @Override
    public ItineraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder is creating a view for binding");
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_litinerary, parent, false);
        return new ItineraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder is binding at position: " + position);
        holder.bind(itineraryList.get(position));
    }

    @Override
    public int getItemCount() {
        int size = itineraryList != null ? itineraryList.size() : 0;

        Log.d(TAG, "getItemCount returns the size of itineraryList as: " + size);

        if(itineraryList != null){
            for(itinerary item : itineraryList){
                Log.d(TAG, "Itinerary item: " + item.toString());
            }
        }

        return size;
    }


    class ItineraryViewHolder extends RecyclerView.ViewHolder {
        TextView tripName, startDate, endDate;
        ImageView deleteButton;

        ItineraryViewHolder(@NonNull View itemView) {
            super(itemView);
            tripName = itemView.findViewById(R.id.sampleitinerary);
            startDate = itemView.findViewById(R.id.startDay);
            endDate = itemView.findViewById(R.id.endDay);
            deleteButton = itemView.findViewById(R.id.imageView4);
        }

        void bind(itinerary item) {
            tripName.setText(item.getTripName());
            startDate.setText(convertTimestampToDate(item.getStartDate()));
            endDate.setText(convertTimestampToDate(item.getEndDate()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), PlanningDetailsActivity.class);
                    intent.putExtra("DESTINATION_CITY", item.getDestinationCity());
                    intent.putExtra("planID", item.getPlanID());
                    v.getContext().startActivity(intent);
                }
            });
            deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog(getAdapterPosition(), item.getPlanID()));
        }

        private void showDeleteConfirmationDialog(int position, String planID) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete itinerary")
                    .setMessage("Are you sure you want to delete this trip?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteItinerary(position, planID))
                    .setNegativeButton("No", null)
                    .show();
        }

        private void deleteItinerary(int position, String planID) {
            if (itineraryList.size() == 0 || itineraryList.isEmpty()) {
                Log.e(TAG, "Deletion failed: List is empty");
                return;
            }else if (position < 0 || position >= itineraryList.size()) {
                Log.e(TAG, "Delete failed: Invalid location" + position);
                return;
            }else{
                itineraryList.remove(position);
                databaseReference.child(planID).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, itineraryList.size());
                            Toast.makeText(context, "The trip has been deleted", Toast.LENGTH_SHORT).show();
                            return;
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show()
                        );
            }


        }

        String convertTimestampToDate(long timestamp) {
            Date date = new Date(timestamp);
            return DateFormat.getDateInstance().format(date);
        }
    }
}