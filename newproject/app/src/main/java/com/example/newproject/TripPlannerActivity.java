package com.example.newproject;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newproject.Adapter.RecentsAdpater;
import com.example.newproject.Adapter.TopPlacesAdpater;
import com.example.newproject.model.RecentsData;
import com.example.newproject.model.TopPlacesData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

public class TripPlannerActivity extends AppCompatActivity {

    private static final String TAG = "TripPlannerActivity";
    RecyclerView recentRecycler, topPlacesRecycler;
    RecentsAdpater recentsAdpater;
    TopPlacesAdpater topPlacesAdpater;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onTripPlan: 987654321");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tirpplanner);

        List<RecentsData> recentsDataList = new ArrayList<>();
        recentsDataList.add(new RecentsData("AM Lake", "India", R.drawable.recentimage1));
        recentsDataList.add(new RecentsData("Nilqiri Hills", "India", R.drawable.recentimage2));
        recentsDataList.add(new RecentsData("AM Lake", "India", R.drawable.recentimage1));
        recentsDataList.add(new RecentsData("Nilqiri Hills", "India", R.drawable.recentimage2));
        recentsDataList.add(new RecentsData("AM Lake", "India", R.drawable.recentimage1));
        recentsDataList.add(new RecentsData("Nilqiri Hills", "India", R.drawable.recentimage2));

        setRecentRecycler(recentsDataList);

        List<TopPlacesData> topPlacesDataList = new ArrayList<>();
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", R.drawable.topplaces));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", R.drawable.topplaces));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", R.drawable.topplaces));
        topPlacesDataList.add(new TopPlacesData("Kasimir Hill", "India", R.drawable.topplaces));

        setTopPlacesRecycler(topPlacesDataList);
    }

    public void onLeadingButtonClick(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    public void chatRoomButtonClick(View view) {
        Intent intent = new Intent(this, ChatRoomHomePageActivity.class);
        startActivity(intent);
        finish();
    }

    public void mapButtonClick(View view) {
        if (isServicesOK()) {
            Intent intent = new Intent(this, mapPageActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void homeButton(View view) {
        if (isServicesOK()) {
            Intent intent = new Intent(this, homePage.class);
            startActivity(intent);
            finish();
        }
    }

    public void profileButton(View view) {
        if (isServicesOK()) {
            Intent intent = new Intent(this, homePage.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(TripPlannerActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(TripPlannerActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void setRecentRecycler(List<RecentsData> recentsDataList){
        Log.d(TAG, "onTripPlan: 123456789");
        recentRecycler = findViewById(R.id.recent_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recentRecycler.setLayoutManager(layoutManager);
        recentsAdpater = new RecentsAdpater(this, recentsDataList);
        recentRecycler.setAdapter(recentsAdpater);
    }

    private void setTopPlacesRecycler(List<TopPlacesData> topPlacesDataList){
        Log.d(TAG, "onTripPlan: 322168231");
        topPlacesRecycler = findViewById(R.id.top_places_recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        topPlacesRecycler.setLayoutManager(layoutManager);
        topPlacesAdpater = new TopPlacesAdpater(this, topPlacesDataList);
        topPlacesRecycler.setAdapter(topPlacesAdpater);
    }
}
