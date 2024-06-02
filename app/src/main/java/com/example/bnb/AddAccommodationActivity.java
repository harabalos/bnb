package com.example.bnb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.content.Intent;

public class AddAccommodationActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText locationEditText;
    private EditText capacityEditText;
    private EditText pricePerNightEditText;
    private EditText ratingEditText;
    private EditText imagePathEditText;
    private EditText startDateEditText;
    private EditText endDateEditText;
    private ArrayList<Date> availableDates;
    private SimpleDateFormat dateFormatter;
    private String managerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_accommodation);

        Intent intent = getIntent();
        managerId = intent.getStringExtra("id");

        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        pricePerNightEditText = findViewById(R.id.pricePerNightEditText);
        ratingEditText = findViewById(R.id.ratingEditText);
        imagePathEditText = findViewById(R.id.imagePathEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        availableDates = new ArrayList<>();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        Button addAccommodationButton = findViewById(R.id.addAccommodationButton);
        addAccommodationButton.setOnClickListener(v -> addAccommodation());
        Button goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(view -> finish());

    }

    private void showDatePicker(final EditText dateEditText) {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setOnDateSelectedListener(date -> {
            String formattedDate = dateFormatter.format(date);
            dateEditText.setText(formattedDate);
            availableDates.add(date);
        });
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void addAccommodation() {
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        int capacity = Integer.parseInt(capacityEditText.getText().toString());
        double pricePerNight = Double.parseDouble(pricePerNightEditText.getText().toString());
        float rating = Float.parseFloat(ratingEditText.getText().toString());
        String imagePath = imagePathEditText.getText().toString();

        Accommodation accommodation = new Accommodation(name, location, capacity, availableDates, pricePerNight, rating, imagePath, new ArrayList<>(), managerId);

        ConsoleClient consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        consoleClient.addAccommodationAsync(accommodation, response -> runOnUiThread(() -> {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            finish();
        }));
    }
}
