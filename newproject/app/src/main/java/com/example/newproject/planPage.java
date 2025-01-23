package com.example.newproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newproject.Adapter.ItineraryAdapter;
import com.example.newproject.model.itinerary;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class planPage extends AppCompatActivity {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private ImageView btn_addNewPlan;

    private PopupWindow popupWindow;
    private ImageView btn_save;
    private PlacesClient placesClient;
    private PopupWindow popupPlace;
    private Context context;
    private DatabaseReference mDatabase;
    private CalendarView startDay;
    private CalendarView endDay;
    private RecyclerView recyclerView;
    private ItineraryAdapter adapter;
    private List<itinerary> itineraryList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_planpage);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uID = firebaseUser.getUid();
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("plan").child(uID);

            // Attach a listener to read the data at our posts reference
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    itineraryList.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Plan plan = postSnapshot.getValue(Plan.class);
                        if (plan != null) {
                            itinerary newTrip = new itinerary(
                                    plan.getTripName(), plan.getStartDate(),
                                    plan.getEndDate(), plan.getDestinationCity(), plan.getPlanID()
                            );
                            itineraryList.add(newTrip);
                        }
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);


        if (itineraryList == null) {
            itineraryList = new ArrayList<>();
        }

        adapter = new ItineraryAdapter(this,itineraryList);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //setContentView(R.layout.activity_planpage);
        btn_addNewPlan = findViewById(R.id.btn_addNewPlan);
        initPopupWindow();


        btn_addNewPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
            }
        });

        loadUserInfo();

        ImageButton btnPlanning = findViewById(R.id.btn_planning);
        ImageButton btnProfile = findViewById(R.id.btn_moreDetail);
        ImageButton btn_Msg = findViewById(R.id.btn_Msg);
        ImageButton btnMapPage = findViewById(R.id.btn_map);

        btnPlanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(planPage.this, homePage.class);
                startActivity(intent);
            }
        });


        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(planPage.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
        btnMapPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(planPage.this, mapPageActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_Msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(planPage.this, ChatRoomHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void initPopupWindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);

        Calendar calendar = Calendar.getInstance();
        EditText edit_tripName = view.findViewById(R.id.et_tripName);
        EditText edit_destinationCity = view.findViewById(R.id.et_destinationCity);

        startDay = view.findViewById(R.id.startDay);
        endDay = view.findViewById(R.id.endDay);
        CalendarView startDay = view.findViewById(R.id.startDay);
        CalendarView endDay = view.findViewById(R.id.endDay);
        TextView txt_totalDay = view.findViewById(R.id.txt_totalDay);

        final long[] startDate = {System.currentTimeMillis()};
        final long[] endDate = {System.currentTimeMillis()};

        startDay.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.set(year, month , dayOfMonth);
            calendarStart.set(Calendar.HOUR_OF_DAY, 0);
            calendarStart.set(Calendar.MINUTE, 0);
            calendarStart.set(Calendar.SECOND, 0);
            calendarStart.set(Calendar.MILLISECOND, 0);
            startDate[0] = calendarStart.getTimeInMillis();
            updateTotalDays(startDate[0], endDate[0], txt_totalDay);
        });

        endDay.setOnDateChangeListener((view12, year, month, dayOfMonth) -> {
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.set(year, month, dayOfMonth);
            calendarEnd.set(Calendar.HOUR_OF_DAY, 23);
            calendarEnd.set(Calendar.MINUTE, 59);
            calendarEnd.set(Calendar.SECOND, 59);
            calendarEnd.set(Calendar.MILLISECOND, 999);
            endDate[0] = calendarEnd.getTimeInMillis();
            updateTotalDays(startDate[0], endDate[0], txt_totalDay);
        });



        ImageView btn_cancel = view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(v -> {
            popupWindow.dismiss();
            adapter.notifyItemInserted(itineraryList.size() - 1);
        });

        btn_save = view.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(v -> {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String tripName = edit_tripName.getText().toString();
                String destinationCity = edit_destinationCity.getText().toString();
                long startDateTimestamp = startDate[0];
                long endDateTimestamp = endDate[0];
                Plan plan=new Plan("",tripName,destinationCity,startDateTimestamp,endDateTimestamp);

                String uID = firebaseUser.getUid();
                String planID = mDatabase.child("plan").child(uID).push().getKey();

                plan.setPlanID(planID);
                AtomicBoolean state = new AtomicBoolean(false);

                mDatabase.child("plan").child(uID).child(planID).setValue(plan)
                        .addOnSuccessListener(aVoid -> {
                            state.set(true);
                        });
                if(state.get() == true){
                    itinerary newTrip = new itinerary(tripName, startDateTimestamp,  endDateTimestamp, destinationCity,planID);

                    adapter.notifyItemInserted(itineraryList.size() - 1);
                    itineraryList.add(newTrip);
                }


                popupWindow.dismiss();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(planPage.this);
                builder.setMessage("Please login account")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(planPage.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.create().show();
            }
        });

    }

    private void updateTotalDays(long start, long end, TextView textView) {
        if (end >= start) {
            long diff = end - start;
            long days = TimeUnit.MILLISECONDS.toDays(diff) + 1;
            textView.setText(days + " Days");
        } else {
            textView.setText("End date must be after start date");
        }
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
        Intent intent = new Intent(this, homePage.class);
        startActivity(intent);
        finish();

    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(planPage.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(planPage.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void loadUserInfo() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ImageView userimg = findViewById(R.id.userimg);
        TextView username = findViewById(R.id.username);

        if (firebaseUser != null) {
            DatabaseReference userDbRef = FirebaseDatabase.getInstance().getReference("user");
            userDbRef.child(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        Users user = task.getResult().getValue(Users.class);
                        if (user != null) {
                            username.setText(user.getName());

                            // Use Glide or Picasso to load user image into ImageView
                            if (user.getUserImg() != null) {
                                Glide.with(getApplicationContext()).load(user.getUserImg()).into(userimg);
                            }
                        } else {
                            Log.d(TAG, "No such user");
                        }
                    } else {
                        Log.e(TAG, "Error getting user", task.getException());
                    }
                }
            });
        }
    }

}