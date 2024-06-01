package com.example.bnb;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ConsoleClient consoleClient;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView responseTextView;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consoleClient = new ConsoleClient("192.168.0.6", 4321, this);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        responseTextView = findViewById(R.id.responseTextView);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);
        Button addAccommodationButton = findViewById(R.id.addAccommodationButton);
        Button viewAccommodationsButton = findViewById(R.id.viewAccommodationsButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        loginButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            consoleClient.authenticateAsync(username, password, user -> {
                if (user != null) {
                    currentUser = user;
                    runOnUiThread(() -> responseTextView.setText("Login successful!"));
                    updateUIForLoggedInUser();
                } else {
                    runOnUiThread(() -> responseTextView.setText("Login failed: Invalid username or password."));
                }
            });
        });

        signupButton.setOnClickListener(view -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            boolean isManager = true; // Για απλοποίηση, ορίζουμε ότι όλοι οι χρήστες είναι διαχειριστές
            User user = new User(username, password, isManager);
            if (consoleClient.addUser(user)) {
                runOnUiThread(() -> responseTextView.setText("Signup successful!"));
            } else {
                runOnUiThread(() -> responseTextView.setText("Signup failed: User already exists."));
            }
        });

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
            updateUIForLoggedOutUser();
            runOnUiThread(() -> responseTextView.setText("Logged out"));
        });

        updateUIForLoggedOutUser();
    }

    private void updateUIForLoggedInUser() {
        findViewById(R.id.loginButton).setVisibility(View.GONE);
        findViewById(R.id.signupButton).setVisibility(View.GONE);
        findViewById(R.id.usernameEditText).setVisibility(View.GONE);
        findViewById(R.id.passwordEditText).setVisibility(View.GONE);
        findViewById(R.id.addAccommodationButton).setVisibility(View.VISIBLE);
        findViewById(R.id.viewAccommodationsButton).setVisibility(View.VISIBLE);
        findViewById(R.id.logoutButton).setVisibility(View.VISIBLE);
    }

    private void updateUIForLoggedOutUser() {
        findViewById(R.id.loginButton).setVisibility(View.VISIBLE);
        findViewById(R.id.signupButton).setVisibility(View.VISIBLE);
        findViewById(R.id.usernameEditText).setVisibility(View.VISIBLE);
        findViewById(R.id.passwordEditText).setVisibility(View.VISIBLE);
        findViewById(R.id.addAccommodationButton).setVisibility(View.GONE);
        findViewById(R.id.viewAccommodationsButton).setVisibility(View.GONE);
        findViewById(R.id.logoutButton).setVisibility(View.GONE);
    }
}
