package com.example.bnb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ManagerHomepageActivity extends AppCompatActivity {
    private ConsoleClient consoleClient;
    private TextView responseTextView;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_homepage);

        consoleClient = new ConsoleClient("192.168.0.6", 4321, this);

        Button addAccommodationButton = findViewById(R.id.addAccommodationButton);
        Button viewAccommodationsButton = findViewById(R.id.viewAccommodationsButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        addAccommodationButton.setOnClickListener(view -> {
            if (currentUser != null && currentUser.isManager()) {
                // Δημιουργία και προσθήκη καταλύματος
                // Μπορείτε να ανοίξετε μια νέα δραστηριότητα για να συλλέξετε τα στοιχεία του καταλύματος από τον χρήστη
            }
        });

        viewAccommodationsButton.setOnClickListener(view -> {
            if (currentUser != null && currentUser.isManager()) {
                consoleClient.viewAccommodationsAsync(currentUser.getId(), response -> {
                    runOnUiThread(() -> responseTextView.setText("Your accommodations: " + response));
                });
            }
        });

        logoutButton.setOnClickListener(view -> {
            currentUser = null;
            finish();
        });
    }
}
