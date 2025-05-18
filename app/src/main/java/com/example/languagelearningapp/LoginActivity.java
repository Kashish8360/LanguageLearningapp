package com.example.languagelearningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "LoginPrefs";
    public static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_STUDENT_ID = "studentId";
    public static final String KEY_KEYPASS = "keypass";
    public static final String BASE_URL = "https://backendurl-on1w.onrender.com";

    private TextInputEditText usernameEditText, studentIdEditText;
    private MaterialButton loginButton;
    private CircularProgressIndicator progressBar;
    private SharedPreferences sharedPreferences;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupHttpClient();
        checkExistingSession();
        setupLoginButton();
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        studentIdEditText = findViewById(R.id.studentIdEditText);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupHttpClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    private void checkExistingSession() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToDashboard();
        }
    }

    private void setupLoginButton() {
        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String studentId = studentIdEditText.getText().toString().trim();

            if (validateInputs(username, studentId)) {
                attemptLogin(username, studentId);
            }
        });
    }

    private boolean validateInputs(String username, String studentId) {
        if (username.isEmpty()) {
            showToast("Please enter username");
            return false;
        }

        if (studentId.isEmpty()) {
            showToast("Please enter student ID");
            return false;
        }

        if (!studentId.matches("^s\\d{7,8}$")) {
            showToast("Student ID must be in format s1234567 or s12345678");
            return false;
        }

        return true;
    }

    private void attemptLogin(String username, String studentId) {
        loginButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", studentId);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/footscray/auth")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> {
                        resetLoginButton();
                        showToast("Network error. Please try again.");
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String responseData = response.body() != null ? response.body().string() : "";
                        runOnUiThread(() -> {
                            resetLoginButton();
                            handleLoginResponse(response, responseData, username, studentId);
                        });
                    } catch (IOException e) {
                        runOnUiThread(() -> {
                            resetLoginButton();
                            showToast("Error reading response");
                        });
                    }
                }
            });
        } catch (JSONException e) {
            resetLoginButton();
            showToast("Error creating login request");
        }
    }

    private void handleLoginResponse(Response response, String responseData, String username, String studentId) {
        try {
            if (response.isSuccessful() && responseData != null && !responseData.isEmpty()) {
                JSONObject jsonResponse = new JSONObject(responseData);
                if (jsonResponse.has("keypass")) {
                    String keypass = jsonResponse.getString("keypass");
                    saveLoginState(username, studentId, keypass);
                    navigateToDashboard();
                } else {
                    showToast("Invalid server response: Missing keypass");
                }
            } else {
                handleErrorResponse(response.code());
            }
        } catch (JSONException e) {
            showToast("Error processing server response");
        }
    }

    private void handleErrorResponse(int statusCode) {
        switch (statusCode) {
            case 401:
                showToast("Invalid username or student ID");
                break;
            case 404:
                showToast("Endpoint not found");
                break;
            case 500:
                showToast("Server error");
                break;
            default:
                showToast("Login failed. Error code: " + statusCode);
        }
    }

    private void resetLoginButton() {
        progressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
    }

    private void saveLoginState(String username, String studentId, String keypass) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_STUDENT_ID, studentId);
        editor.putString(KEY_KEYPASS, keypass);
        editor.apply();
    }

    private void navigateToDashboard() {
        runOnUiThread(() -> {
            try {
                Intent intent = new Intent(this, DashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (Exception e) {
                Log.e("LoginActivity", "Failed to navigate to dashboard", e);
                // Fallback to restart the app
                Intent restartIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(restartIntent);
                finish();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}