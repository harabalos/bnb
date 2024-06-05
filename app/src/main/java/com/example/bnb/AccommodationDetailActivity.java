// src/main/java/com/example/bnb/AccommodationDetailActivity.java
package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AccommodationDetailActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView locationTextView;
    private TextView capacityTextView;
    private TextView pricePerNightTextView;
    private TextView ratingTextView;
    private TextView availableDatesTextView;
    private ImageView accommodationImageView;
    private Button goBackButton;
    private Button bookButton;
    private Accommodation accommodation;
    private SimpleDateFormat dateFormatter;
    private boolean isManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accommodation_detail);

        nameTextView = findViewById(R.id.nameTextView);
        locationTextView = findViewById(R.id.locationTextView);
        capacityTextView = findViewById(R.id.capacityTextView);
        pricePerNightTextView = findViewById(R.id.pricePerNightTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        availableDatesTextView = findViewById(R.id.availableDatesTextView);
        accommodationImageView = findViewById(R.id.accommodationImageView);
        goBackButton = findViewById(R.id.goBackButton);
        bookButton = findViewById(R.id.bookButton);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Intent intent = getIntent();
        accommodation = (Accommodation) intent.getSerializableExtra("accommodation");
        isManager = intent.getBooleanExtra("isManager", false);

        populateDetails();

        goBackButton.setOnClickListener(v -> finish());

        if (!isManager) {
            bookButton.setVisibility(View.VISIBLE);
            bookButton.setOnClickListener(v -> {
                // Implement booking logic here
            });
        }
    }

    private void populateDetails() {
        nameTextView.setText(accommodation.getName());
        locationTextView.setText("Location: " + accommodation.getLocation());
        capacityTextView.setText("Capacity: " + accommodation.getCapacity());
        pricePerNightTextView.setText("Price Per Night: $" + accommodation.getPricePerNight());
        ratingTextView.setText("Rating: " + accommodation.getRating() + "/5");

        StringBuilder dates = new StringBuilder();
        for (int i = 0; i < accommodation.getAvailableStartDates().size(); i++) {
            dates.append(dateFormatter.format(accommodation.getAvailableStartDates().get(i)))
                    .append(" to ")
                    .append(dateFormatter.format(accommodation.getAvailableEndDates().get(i)))
                    .append("\n");
        }
        availableDatesTextView.setText("Available Dates: \n" + dates.toString());

        // Load image from drawable
        int imageResource = getResources().getIdentifier(accommodation.getImagePath(), "drawable", getPackageName());
        accommodationImageView.setImageResource(imageResource);
    }
}
