package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ClientHomepageActivity extends AppCompatActivity {
    private Button searchAccommodationsButton;
    private Button viewBookingsButton;
    private Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_homepage);

        searchAccommodationsButton = findViewById(R.id.searchAccommodationsButton);
        viewBookingsButton = findViewById(R.id.viewBookingsButton);
        logoutButton = findViewById(R.id.logoutButton);
        String userId = getIntent().getStringExtra("id");

        searchAccommodationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomepageActivity.this, SearchAccommodationActivity.class);
            intent.putExtra("id", userId);
            startActivity(intent);
        });

        viewBookingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomepageActivity.this, ViewBookingActivity.class);
            intent.putExtra("id", userId);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(ClientHomepageActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
            startActivity(intent);
            finish();
        });
    }
}
