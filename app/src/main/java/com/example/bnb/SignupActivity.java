package com.example.bnb;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private RadioGroup roleRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextInputLayout usernameInputLayout = findViewById(R.id.usernameInput);
        usernameEditText = (TextInputEditText) usernameInputLayout.getEditText();
        TextInputLayout passwordInputLayout = findViewById(R.id.passwordInput);
        passwordEditText = (TextInputEditText) passwordInputLayout.getEditText();
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
    }

    public void goBack(View view) {
        finish();
    }

    public void submitData(View view) {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        boolean isManager = roleRadioGroup.getCheckedRadioButtonId() == R.id.radioManager;

        User user = new User(username, password, isManager);
        ConsoleClient consoleClient = new ConsoleClient("192.168.0.6", 4321, this);
        if (consoleClient.addUser(user)) {
            Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Signup failed: User already exists.", Toast.LENGTH_SHORT).show();
        }
    }
}
