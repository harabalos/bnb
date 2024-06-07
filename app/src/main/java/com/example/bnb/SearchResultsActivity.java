package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.Log;

public class SearchResultsActivity extends AppCompatActivity {

    private LinearLayout resultsContainer;
    private String filters;
    private boolean noFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        resultsContainer = findViewById(R.id.resultsContainer);

        Intent intent = getIntent();
        filters = intent.getStringExtra("filters");
        noFilters = intent.getBooleanExtra("noFilters", false);

        Button goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setOnClickListener(v -> finish());

        searchAccommodations();
    }

    private void searchAccommodations() { //search function as described in the description
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try (Socket socket = new Socket("192.168.0.6", 4321);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("search");

                if (noFilters) {
                    out.writeObject("{}");  // Sending empty JSON string for no filters
                    Log.d("SearchAccommodations", "Empty JSON string sent");
                } else {
                    out.writeObject(filters);  // Sending filters as String
                    Log.d("SearchAccommodations", "Filters JSON string sent: " + filters);
                }

                String response = (String) in.readObject();
                JSONArray accommodationsArray = new JSONArray(response);

                runOnUiThread(() -> displayAccommodations(accommodationsArray));

            } catch (Exception e) {
                Log.e("SearchAccommodations", "Error in searchAccommodations", e);
            }
        });
    }


    private void displayAccommodations(JSONArray accommodations) {
        try {
            String userId = getIntent().getStringExtra("id");
            for (int i = 0; i < accommodations.length(); i++) {
                JSONObject accommodationJson = accommodations.getJSONObject(i);
                Accommodation accommodation = Accommodation.fromJson(accommodationJson);

                Button accommodationButton = new Button(this);
                accommodationButton.setText(accommodation.getName());
                accommodationButton.setOnClickListener(v -> {
                    Intent intent = new Intent(SearchResultsActivity.this, AccommodationDetailActivity.class);
                    intent.putExtra("accommodation", accommodation);
                    intent.putExtra("id", userId);
                    startActivity(intent);
                });

                resultsContainer.addView(accommodationButton);
            }
        } catch (JSONException e) {
            Log.e("SearchResultsActivity", "Error displaying accommodations", e);
        }
    }

}
