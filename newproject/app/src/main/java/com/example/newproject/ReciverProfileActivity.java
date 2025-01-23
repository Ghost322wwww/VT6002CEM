package com.example.newproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReciverProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reciver_profile);

        String reciverName = getIntent().getStringExtra("reciverName");
        String reciverPhone = getIntent().getStringExtra("phone");
        String reciverEmail = getIntent().getStringExtra("email");

        TextView profileName = findViewById(R.id.profileName);
        TextView profilePhone = findViewById(R.id.profilePhone);
        TextView profileEmail = findViewById(R.id.profileEmail);
        TextView titleName = findViewById(R.id.titleName);

        ImageButton btn_back = findViewById(R.id.btn_back);

        profileName.setText(reciverName);
        profilePhone.setText(reciverPhone);
        profileEmail.setText(reciverEmail);
        titleName.setText(reciverName);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
