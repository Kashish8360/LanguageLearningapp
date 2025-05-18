package com.example.languagelearningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Looper;

import android.widget.Toast;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    GreetingService greetingService;

    private static final String TAG = "MainActivity";
    private static final int SPLASH_DELAY = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, greetingService.getGreeting(), Toast.LENGTH_SHORT).show();
        // Initialize a handler to delay the next activity
        new Handler().postDelayed(this::checkLoginAndRedirect, SPLASH_DELAY);
    }

    private void checkLoginAndRedirect() {
        try {
            SharedPreferences prefs = getSharedPreferences(
                    LoginActivity.PREFS_NAME,
                    MODE_PRIVATE
            );

            // Add delay to ensure all resources are loaded
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    boolean isLoggedIn = prefs.getBoolean(
                            LoginActivity.KEY_IS_LOGGED_IN,
                            false
                    );

                    Intent intent = isLoggedIn
                            ? new Intent(this, DashboardActivity.class)
                            : new Intent(this, LoginActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Redirect error", e);
                    // Fallback to login
                    Intent fallback = new Intent(this, LoginActivity.class);
                    fallback.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(fallback);
                    finish();
                }
            }, 500); // Reduced delay to 500ms
        } catch (Exception e) {
            Log.e(TAG, "Initialization error", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up any handlers to prevent memory leaks
        new Handler().removeCallbacksAndMessages(null);
    }
}