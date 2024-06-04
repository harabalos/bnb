package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ViewAccommodationActivity extends AppCompatActivity {

    private String managerId;
    private ConsoleClient consoleClient;
    private LinearLayout accommodationsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_accommodation);

        Intent intent = getIntent();
        managerId = intent.getStringExtra("id");

        accommodationsLayout = findViewById(R.id.accommodationsLayout);
        consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        consoleClient.viewAccommodationsAsync(managerId, this::displayAccommodations);

        Button goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(v -> finish());
    }

    private void displayAccommodations(String response) {
        runOnUiThread(() -> {
            try {
                JSONArray accommodationsArray = new JSONArray(response);
                for (int i = 0; i < accommodationsArray.length(); i++) {
                    JSONObject accommodationObject = accommodationsArray.getJSONObject(i);
                    Accommodation accommodation = Accommodation.fromJson(accommodationObject);

                    Button accommodationButton = new Button(this);
                    accommodationButton.setText(accommodation.getName());
                    accommodationButton.setOnClickListener(v -> {
                        Intent intent = new Intent(ViewAccommodationActivity.this, AccommodationDetailActivity.class);
                        intent.putExtra("accommodation", accommodation);
                        startActivity(intent);
                    });
                    accommodationsLayout.addView(accommodationButton);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
}
