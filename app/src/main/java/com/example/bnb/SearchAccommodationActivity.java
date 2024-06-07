package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchAccommodationActivity extends AppCompatActivity {

    private EditText locationEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private EditText capacityEditText;
    private EditText minPriceEditText;
    private EditText maxPriceEditText;
    private EditText ratingEditText;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_accommodation);

        locationEditText = findViewById(R.id.locationEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        minPriceEditText = findViewById(R.id.minPriceEditText);
        maxPriceEditText = findViewById(R.id.maxPriceEditText);
        ratingEditText = findViewById(R.id.ratingEditText);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> performSearch());

        Button noFiltersButton = findViewById(R.id.noFiltersButton);
        noFiltersButton.setOnClickListener(v -> performSearchWithNoFilters());

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> finish());
    }

    private void showDatePicker(final EditText dateEditText) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSelectedListener(date -> {
            String formattedDate = dateFormatter.format(date);
            dateEditText.setText(formattedDate);
        });
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void performSearch() {
        String location = locationEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        int capacity = capacityEditText.getText().toString().isEmpty() ? -1 : Integer.parseInt(capacityEditText.getText().toString().trim());
        double minPrice = minPriceEditText.getText().toString().isEmpty() ? -1 : Double.parseDouble(minPriceEditText.getText().toString().trim());
        double maxPrice = maxPriceEditText.getText().toString().isEmpty() ? -1 : Double.parseDouble(maxPriceEditText.getText().toString().trim());
        float rating = ratingEditText.getText().toString().isEmpty() ? -1 : Float.parseFloat(ratingEditText.getText().toString().trim());

        try {
            JSONObject filters = new JSONObject();
            if (!location.isEmpty()) {
                filters.put("location", location);
            }
            if (!startDate.isEmpty()) {
                filters.put("startDate", startDate);
            }
            if (!endDate.isEmpty()) {
                filters.put("endDate", endDate);
            }
            if (capacity > 0) {
                filters.put("capacity", capacity);
            }
            if (minPrice >= 0) {
                filters.put("minPrice", minPrice);
            }
            if (maxPrice >= 0) {
                filters.put("maxPrice", maxPrice);
            }
            if (rating >= 0) {
                filters.put("rating", rating);
            }
            String userId = getIntent().getStringExtra("id");

            Intent intent = new Intent(SearchAccommodationActivity.this, SearchResultsActivity.class);
            intent.putExtra("filters", filters.toString());
            intent.putExtra("noFilters", false);
            intent.putExtra("id", userId);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void performSearchWithNoFilters() {
        String userId = getIntent().getStringExtra("id");
        Intent intent = new Intent(SearchAccommodationActivity.this, SearchResultsActivity.class);
        intent.putExtra("noFilters", true);
        intent.putExtra("id", userId);
        startActivity(intent);
    }
}
