package com.example.bnb;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class AuthenticationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);
    }

    public void launchLogIn(View v) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void launchSignup(View v) {
        Intent i = new Intent(this, SignupActivity.class);
        startActivity(i);
    }
}
