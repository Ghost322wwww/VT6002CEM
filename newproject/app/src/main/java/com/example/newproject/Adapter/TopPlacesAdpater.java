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
import com.example.newproject.model.TopPlacesData;

import java.util.List;


public class TopPlacesAdpater extends RecyclerView.Adapter<TopPlacesAdpater.TopPlacesViewHolder>{

    Context context;
    List<TopPlacesData> topPlacesDataList;

    public TopPlacesAdpater(Context context, List<TopPlacesData> recentsDataList) {
        this.context = context;
        this.topPlacesDataList = recentsDataList;
    }

    @NonNull
    @Override
    public TopPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.top_places_row_item, parent,false);
        return new TopPlacesViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TopPlacesViewHolder holder, int position) {
        holder.countryName.setText(topPlacesDataList.get(position).getCountryName());
        holder.placeName.setText(topPlacesDataList.get(position).getPlaceName());
        holder.placeImage.setImageResource(topPlacesDataList.get(position).getImagerUrl());
    }

    @Override
    public int getItemCount() {
        return topPlacesDataList.size();
    }

    public  static final class TopPlacesViewHolder extends RecyclerView.ViewHolder{
        ImageView placeImage;
        TextView placeName, countryName;

        public TopPlacesViewHolder(@NonNull View itemView){
            super(itemView);

            placeImage = itemView.findViewById(R.id.place_image);
            placeName = itemView.findViewById(R.id.place_name);
            countryName = itemView.findViewById(R.id.country_name);

        }
    }
}
