package com.example.languagelearningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setupBottomNavigation();
        setupLogoutButton();
        displayUserInfo();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                startActivity(new Intent(ProfileActivity.this, DashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void setupLogoutButton() {
        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);

        // Get user data
        String username = sharedPreferences.getString(LoginActivity.KEY_USERNAME, "User");
        String studentId = sharedPreferences.getString(LoginActivity.KEY_STUDENT_ID, "s0000000");
        String email = studentId.toLowerCase() + "@university.edu";

        // Generate join date (current date for demo)
        String joinDate = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());

        // Set user information
        TextView profileNameText = findViewById(R.id.profileNameText);
        TextView studentIdText = findViewById(R.id.studentIdText);
        TextView profileEmailText = findViewById(R.id.profileEmailText);
        TextView memberSinceText = findViewById(R.id.memberSinceText);

        profileNameText.setText(username);
        studentIdText.setText(studentId);
        profileEmailText.setText(email);
        memberSinceText.setText(joinDate);

        // Set statistics (demo values)
        TextView coursesCompletedText = findViewById(R.id.coursesCompletedText);
        TextView lessonsCompletedText = findViewById(R.id.lessonsCompletedText);
        TextView streakDaysText = findViewById(R.id.streakDaysText);

        coursesCompletedText.setText("5");
        lessonsCompletedText.setText("24");
        streakDaysText.setText("7");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}