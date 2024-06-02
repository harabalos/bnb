package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ManagerHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_homepage);

        Button addAccommodationButton = findViewById(R.id.addAccommodationButton);
        addAccommodationButton.setOnClickListener(v -> {
            String managerId = getIntent().getStringExtra("id");
            Intent intent = new Intent(ManagerHomepageActivity.this, AddAccommodationActivity.class);
            intent.putExtra("id", managerId);
            startActivity(intent);
        });

        Button viewAccommodationsButton = findViewById(R.id.viewAccommodationsButton);
        viewAccommodationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerHomepageActivity.this, ViewAccommodationActivity.class);
            startActivity(intent);
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            finish();
        });
    }
}
