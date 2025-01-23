package com.example.newproject;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place.Field;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NearbySearch {
    private static final String TAG = "NearbySearch";

    private PlacesClient placesClient;
    private Context mContext;
    private List<PlaceLikelihood> placesList = new ArrayList<>();

    public NearbySearch(PlacesClient placesClient, Context context) {
        this.placesClient = placesClient;
        this.mContext = context;
    }

    public void execute() {
        List<Field> placeFields = Arrays.asList(Field.NAME, Field.ADDRESS, Field.LAT_LNG);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);
        Task<FindCurrentPlaceResponse> findCurrentPlaceTask = placesClient.findCurrentPlace(request);
        findCurrentPlaceTask.addOnSuccessListener((response) -> {
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                String name = placeLikelihood.getPlace().getName();
                String address = placeLikelihood.getPlace().getAddress();
                LatLng latLng = placeLikelihood.getPlace().getLatLng();
                double likelihood = placeLikelihood.getLikelihood();
                String placeStr = String.format("Place '%s' has likelihood: %f\n", name, likelihood);
                placesList.add(placeLikelihood);
                Log.i(TAG, placeStr);
            }
            Log.i(TAG, "Nearby places: " + placesList.toString());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }
    public List<PlaceLikelihood> getPlacesList() {
        return placesList;
    }
}