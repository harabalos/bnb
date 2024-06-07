package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewBookingActivity extends AppCompatActivity {

    private LinearLayout bookingsLayout;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking);

        bookingsLayout = findViewById(R.id.bookingsLayout);
        userId = getIntent().getStringExtra("id");

        Button goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(v -> finish());

        loadBookings();
    }

    private void loadBookings() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Socket socket = new Socket("192.168.0.6", 4321);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("viewBookings");
                out.writeObject(userId);

                String response = (String) in.readObject();
                JSONArray bookingsArray = new JSONArray(response);

                runOnUiThread(() -> displayBookings(bookingsArray));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void displayBookings(JSONArray bookingsArray) {
        try {
            for (int i = 0; i < bookingsArray.length(); i++) {
                JSONObject bookingJson = bookingsArray.getJSONObject(i);
                Accommodation accommodation = Accommodation.fromJson(bookingJson.getJSONObject("accommodation"));

                Button bookingButton = new Button(this);
                bookingButton.setText(accommodation.getName());
                bookingButton.setOnClickListener(v -> {
                    Intent intent = new Intent(ViewBookingActivity.this, AccommodationDetailActivity.class);
                    intent.putExtra("accommodation", accommodation);
                    intent.putExtra("id", userId);
                    startActivity(intent);
                });

                bookingsLayout.addView(bookingButton);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
