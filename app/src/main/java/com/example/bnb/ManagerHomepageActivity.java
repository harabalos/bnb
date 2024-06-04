package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class ManagerHomepageActivity extends AppCompatActivity {
    private String managerId;
    private ConsoleClient consoleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_homepage);

        managerId = getIntent().getStringExtra("id");


        consoleClient = new ConsoleClient("192.168.0.6", 4321, this);

        consoleClient.loadAccommodationsFromFile();



        Button addAccommodationButton = findViewById(R.id.addAccommodationButton);
        addAccommodationButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepageActivity.this, AddAccommodationActivity.class);
            intent.putExtra("id", managerId);
            startActivity(intent);
        });

        Button viewAccommodationsButton = findViewById(R.id.viewAccommodationsButton);
        viewAccommodationsButton.setOnClickListener(v -> {
            Intent viewIntent = new Intent(ManagerHomepageActivity.this, ViewAccommodationActivity.class);
            viewIntent.putExtra("id", managerId);
            startActivity(viewIntent);
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            finish();
        });
    }
}
