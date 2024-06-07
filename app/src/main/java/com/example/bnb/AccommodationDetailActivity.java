// src/main/java/com/example/bnb/AccommodationDetailActivity.java
package com.example.bnb;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

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
    private Button reviewButton;
    private Accommodation accommodation;
    private SimpleDateFormat dateFormatter;
    private boolean isManager;
    private boolean isFromBookings;
    private Date startDate;
    private Date endDate;
    private String userId;

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
        reviewButton = findViewById(R.id.reviewButton);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Intent intent = getIntent();
        accommodation = (Accommodation) intent.getSerializableExtra("accommodation");
        isManager = intent.getBooleanExtra("isManager", false);
        isFromBookings = intent.getBooleanExtra("isFromBookings", false);
        userId = intent.getStringExtra("id");

        populateDetails();

        goBackButton.setOnClickListener(v -> finish());

        if (isManager) {
            bookButton.setVisibility(View.GONE);
            reviewButton.setVisibility(View.GONE);
        } else if (isFromBookings) {
            bookButton.setVisibility(View.GONE);
            reviewButton.setVisibility(View.VISIBLE);
            reviewButton.setOnClickListener(v -> showReviewDialog());
        } else {
            bookButton.setVisibility(View.VISIBLE);
            reviewButton.setVisibility(View.GONE);
            bookButton.setOnClickListener(v -> showStartDatePicker());
        }
    }

    private void showReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Review Accommodation");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Submit", (dialog, which) -> {
            int rating = Integer.parseInt(input.getText().toString());
            if (rating < 1 || rating > 5) {
                Toast.makeText(this, "Please enter a rating between 1 and 5", Toast.LENGTH_SHORT).show();
            } else {
                updateRating(rating);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void submitReview(int rating) {
        int currentNumberOfReviews = accommodation.getNumberOfReviews();
        float currentRating = accommodation.getRating();

        float newRating = (currentRating * currentNumberOfReviews + rating) / (currentNumberOfReviews + 1);
        accommodation.setRating(newRating);
        accommodation.setNumberOfReviews(currentNumberOfReviews + 1);

        ConsoleClient consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        consoleClient.updateAccommodationAsync(accommodation, response -> runOnUiThread(() -> {
            Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
            finish();
        }));
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

    private void showStartDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog startDatePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            startDate = calendar.getTime();
            showEndDatePicker();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        startDatePicker.show();
    }

    private void showEndDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog endDatePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            endDate = calendar.getTime();
            if (validateBookingDates()) {
                bookAccommodation();
            } else {
                Toast.makeText(this, "Selected dates are not available", Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        endDatePicker.show();
    }

    private boolean validateBookingDates() {
        for (int i = 0; i < accommodation.getAvailableStartDates().size(); i++) {
            Date availableStart = accommodation.getAvailableStartDates().get(i);
            Date availableEnd = accommodation.getAvailableEndDates().get(i);
            if ((startDate.compareTo(availableStart) >= 0 && endDate.compareTo(availableEnd) <= 0)) {
                return true;
            }
        }
        return false;
    }

    private void bookAccommodation() { //book function as described in the description
        String userId = getIntent().getStringExtra("id");

        // Update the available dates in the accommodation
        for (int i = 0; i < accommodation.getAvailableStartDates().size(); i++) {
            Date availableStart = accommodation.getAvailableStartDates().get(i);
            Date availableEnd = accommodation.getAvailableEndDates().get(i);
            if (startDate.compareTo(availableStart) >= 0 && endDate.compareTo(availableEnd) <= 0) {
                accommodation.getAvailableStartDates().remove(i);
                accommodation.getAvailableEndDates().remove(i);
                if (startDate.compareTo(availableStart) > 0) {
                    accommodation.getAvailableStartDates().add(availableStart);
                    accommodation.getAvailableEndDates().add(startDate);
                }
                if (endDate.compareTo(availableEnd) < 0) {
                    accommodation.getAvailableStartDates().add(endDate);
                    accommodation.getAvailableEndDates().add(availableEnd);
                }
                break;
            }
        }

        // Create a new booking
        String bookingId = UUID.randomUUID().toString();
        Booking booking = new Booking(bookingId, userId, startDate, endDate);
        accommodation.getBookings().add(booking);

        ConsoleClient consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        consoleClient.updateAccommodationAsync(accommodation, response -> runOnUiThread(() -> {
            Toast.makeText(this, "Booking successful", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }

    private void updateRating(int newRating) {
        int totalReviews = accommodation.getNumberOfReviews();
        float currentRating = accommodation.getRating();

        // Calculate new average rating
        float updatedRating = ((currentRating * totalReviews) + newRating) / (totalReviews + 1);

        accommodation.setRating(updatedRating);
        accommodation.setNumberOfReviews(totalReviews + 1);

        ConsoleClient consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        consoleClient.updateAccommodationAsync(accommodation, response -> runOnUiThread(() -> {
            Toast.makeText(this, "Review submitted", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }
}
