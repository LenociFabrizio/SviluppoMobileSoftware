package it.uniba.di.e_cultureexperience;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class TimeOutActivity extends AppCompatActivity {
    Button retryBtn, exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_out);

        retryBtn = findViewById(R.id.retryButton);
        retryBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TimeOutActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });

        exitBtn = findViewById(R.id.exitButton);
        exitBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TimeOutActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();
        });

    }
}