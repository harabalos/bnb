package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputLayout usernameInputLayout = findViewById(R.id.editTextName);
        usernameEditText = (TextInputEditText) usernameInputLayout.getEditText();
        TextInputLayout passwordInputLayout = findViewById(R.id.editTextPassword);
        passwordEditText = (TextInputEditText) passwordInputLayout.getEditText();
    }

    public void goBack(View view) {
        finish();
    }

    public void attemptLogin(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        ConsoleClient consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        consoleClient.authenticateAsync(username, password, user -> {
            if (user != null) {
                runOnUiThread(() -> Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show());
                // Navigate to the appropriate homepage
                if (user.isManager()) {
                    Intent intent = new Intent(this, ManagerHomepageActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, ClientHomepageActivity.class);
                    startActivity(intent);
                }
                finish();
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Login failed: Invalid username or password.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
