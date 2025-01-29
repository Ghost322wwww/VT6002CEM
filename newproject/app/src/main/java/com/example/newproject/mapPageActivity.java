package com.example.newproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class mapPageActivity extends AppCompatActivity implements OnMapReadyCallback, OnConnectionFailedListener {

    private static final String TAG = "mapPageActivity";
    private GoogleMap gMap;
    private PlacesClient gPlacesClient;
    private FusedLocationProviderClient gFusedLocationProviderClient;

    private ImageButton btnHomePage, btnChat, btnProfile, btnPlanning, btnMap;
    private ImageView mGps;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private boolean mLocationPermissionGranted = false;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private SensorEventListener gyroscopeEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // **初始化按鈕**
        btnHomePage = findViewById(R.id.btn_home);
        btnChat = findViewById(R.id.btn_chat);
        btnProfile = findViewById(R.id.btn_profile);
        btnPlanning = findViewById(R.id.btn_planning);
        btnMap = findViewById(R.id.btn_map);
        mGps = findViewById(R.id.ic_gps);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        if (gyroscopeSensor == null) {
            Log.e(TAG, "Gyroscope sensor not available!");
        }

        gyroscopeEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                    Log.d(TAG, "Gyroscope Detected: " + event.values[2]);

                    float rotation = event.values[2];
                    if (gMap != null) {
                        float currentBearing = gMap.getCameraPosition().bearing;
                        gMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(gMap.getCameraPosition().target)
                                        .zoom(gMap.getCameraPosition().zoom)
                                        .bearing(currentBearing + rotation * 30)
                                        .build()
                        ));
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };


        // **確保所有按鈕存在**
        if (btnHomePage == null || btnChat == null || btnProfile == null || btnPlanning == null || btnMap == null || mGps == null) {
            Log.e(TAG, "Error: One or more buttons are NULL!");
        }

        // **設置按鈕點擊事件**
        if (btnHomePage != null) {
            btnHomePage.setOnClickListener(view -> {
                Intent intent = new Intent(mapPageActivity.this, homePage.class);
                startActivity(intent);
                finish();
            });
        }

        if (btnChat != null) {
            btnChat.setOnClickListener(view -> {
                Intent intent = new Intent(mapPageActivity.this, ChatRoomHomePageActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (btnProfile != null) {
            btnProfile.setOnClickListener(view -> {
                Intent intent = new Intent(mapPageActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (btnPlanning != null) {
            btnPlanning.setOnClickListener(view -> {
                Intent intent = new Intent(mapPageActivity.this, planPage.class);
                startActivity(intent);
                finish();
            });
        }

        if (btnMap != null) {
            btnMap.setOnClickListener(view -> {
                Intent intent = new Intent(mapPageActivity.this, mapPageActivity.class);
                startActivity(intent);
                finish();
            });
        }

        if (mGps != null) {
            mGps.setOnClickListener(view -> {
                Log.d(TAG, "GPS button clicked");
                getDeviceLocation();
                restartGyroscope();
            });
        }


        // **初始化 Places API**
        String apiKey = getString(R.string.API_KEY);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }
        gPlacesClient = Places.createClient(this);
        gFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // **檢查定位權限並初始化地圖**
        getLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (gyroscopeSensor != null) {
            sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Gyroscope registered in onResume.");
        }

        if (mLocationPermissionGranted) {
            restartGyroscope();
        }
    }


    private void restartGyroscope() {
        if (gyroscopeSensor != null) {
            sensorManager.unregisterListener(gyroscopeEventListener);

            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

            if (gyroscopeSensor != null) {
                sensorManager.registerListener(gyroscopeEventListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d(TAG, "Gyroscope re-registered successfully after GPS activation.");
            } else {
                Log.e(TAG, "Gyroscope sensor is still unavailable.");
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeEventListener);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        gMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            gMap.setMyLocationEnabled(true);
            gMap.getUiSettings().setMyLocationButtonEnabled(false);
            gMap.getUiSettings().setZoomControlsEnabled(true);

            restartGyroscope();
        }
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: Getting the device's current location");
        try {
            if (mLocationPermissionGranted) {
                gFusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        LatLng currentLocation = new LatLng(task.getResult().getLatitude(), task.getResult().getLongitude());
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                    } else {
                        Log.d(TAG, "Current location is null");
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            initMap();
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map Fragment is null!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                initMap();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Google API Connection Failed");
    }
}
