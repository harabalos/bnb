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
        });
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void addAccommodation() {
        String name = nameEditText.getText().toString();
        String location = locationEditText.getText().toString();
        String capacityStr = capacityEditText.getText().toString();
        String pricePerNightStr = pricePerNightEditText.getText().toString();
        String ratingStr = ratingEditText.getText().toString();
        String imagePath = imagePathEditText.getText().toString();
        String startDateStr = startDateEditText.getText().toString();
        String endDateStr = endDateEditText.getText().toString();

        boolean valid = true;

        if (name.isEmpty() || containsNumber(name)) {
            nameEditText.setError("Invalid name. Name cannot be empty or contain numbers.");
            valid = false;
        }

        if (location.isEmpty() || containsNumber(location)) {
            locationEditText.setError("Invalid location. Location cannot be empty or contain numbers.");
            valid = false;
        }

        if (capacityStr.isEmpty() || !isNumeric(capacityStr) || Integer.parseInt(capacityStr) > 1000) {
            capacityEditText.setError("Invalid capacity. Capacity must be a number and less than 1000.");
            valid = false;
        }

        if (pricePerNightStr.isEmpty() || !isNumeric(pricePerNightStr)) {
            pricePerNightEditText.setError("Invalid price per night. Price must be a number.");
            valid = false;
        }

        if (ratingStr.isEmpty() || !isNumeric(ratingStr) || Float.parseFloat(ratingStr) < 1 || Float.parseFloat(ratingStr) > 5) {
            ratingEditText.setError("Invalid rating. Rating must be a number between 1 and 5.");
            valid = false;
        }

        if (imagePath.isEmpty()) {
            imagePathEditText.setError("Invalid image path. Image path cannot be empty.");
            valid = false;
        }

        if (startDateStr.isEmpty()) {
            startDateEditText.setError("Start date is required.");
            valid = false;
        }

        if (endDateStr.isEmpty()) {
            endDateEditText.setError("End date is required.");
            valid = false;
        }

        if (!startDateStr.isEmpty() && !endDateStr.isEmpty()) {
            try {
                Date start = dateFormatter.parse(startDateStr);
                Date end = dateFormatter.parse(endDateStr);
                if (!start.before(end)) {
                    startDateEditText.setError("Start date must be before end date.");
                    endDateEditText.setError("End date must be after start date.");
                    valid = false;
                }
            } catch (Exception e) {
                startDateEditText.setError("Invalid date format.");
                endDateEditText.setError("Invalid date format.");
                valid = false;
            }
        }

        if (!valid) {
            return;
        }

        int capacity = Integer.parseInt(capacityStr);
        double pricePerNight = Double.parseDouble(pricePerNightStr);
        float rating = Float.parseFloat(ratingStr);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date startDate = null;
        Date endDate = null;

        try {
            startDate = dateFormatter.parse(startDateStr);
            endDate = dateFormatter.parse(endDateStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Date> availableStartDates = new ArrayList<>();
        ArrayList<Date> availableEndDates = new ArrayList<>();
        availableStartDates.add(startDate);
        availableEndDates.add(endDate);

        Accommodation accommodation = new Accommodation(name, location, capacity, availableStartDates, availableEndDates, pricePerNight, rating, imagePath, new ArrayList<>(), managerId,1);

        ConsoleClient consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        consoleClient.addAccommodationAsync(accommodation, response -> runOnUiThread(() -> {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            finish();
        }));
    }

    private boolean containsNumber(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

//Na ftiaksw to viewAccommodation