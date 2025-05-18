package com.example.languagelearningapp;

import android.util.Log;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements EntityAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private EntityAdapter adapter;
    private final List<Entity> entities = new ArrayList<>();
    private OkHttpClient client;
    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView keypassTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initializeViews();
        setupHttpClient();
        checkSessionValidity();
        setupBottomNavigation();
        setupSwipeRefresh();
        displayKeypass();
        fetchEntities();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        keypassTextView = findViewById(R.id.keypassTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EntityAdapter(entities, this); // Pass 'this' as the click listener
        recyclerView.setAdapter(adapter);
    }

    private void setupHttpClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    private void checkSessionValidity() {
        sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(LoginActivity.KEY_IS_LOGGED_IN, false) ||
                sharedPreferences.getString(LoginActivity.KEY_KEYPASS, null) == null) {
            navigateToLogin();
        }
    }

    private void displayKeypass() {
        String keypass = sharedPreferences.getString(LoginActivity.KEY_KEYPASS, "");
        keypassTextView.setText("Session Key: " + keypass);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchEntities();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void fetchEntities() {
        runOnUiThread(() -> {
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (emptyView != null) emptyView.setVisibility(View.GONE);
        });

        String keypass = sharedPreferences.getString(LoginActivity.KEY_KEYPASS, "");
        if (keypass.isEmpty()) {
            navigateToLogin();
            return;
        }
        String url = LoginActivity.BASE_URL + "/dashboard/" + keypass;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("Network error. Pull to refresh.");
                    showToast("Failed to connect: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseData = response.body() != null ? response.body().string() : "";
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        handleDashboardResponse(response, responseData);
                    });
                } catch (IOException e) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        showToast("Error reading response");
                    });
                }
            }
        });
    }

    private void handleDashboardResponse(Response response, String responseData) {
        try {
            Log.d("Dashboard", "Response received: " + responseData);

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response.code() + ": " + responseData);
            }

            JSONObject jsonResponse = new JSONObject(responseData);

            // Check if "entities" exists in the response
            if (!jsonResponse.has("entities")) {
                throw new JSONException("Missing 'entities' in response");
            }

            JSONArray entitiesArray = jsonResponse.getJSONArray("entities");
            Log.d("Dashboard", "Found " + entitiesArray.length() + " entities");

            List<Entity> newEntities = new ArrayList<>();
            for (int i = 0; i < entitiesArray.length(); i++) {
                try {
                    JSONObject entityObj = entitiesArray.getJSONObject(i);
                    Entity entity = new Entity(
                            entityObj.optString("property1", ""),
                            entityObj.optString("property2", ""),
                            entityObj.optString("description", "")
                    );
                    newEntities.add(entity);
                } catch (JSONException e) {
                    Log.e("Dashboard", "Error parsing entity at position " + i, e);
                }
            }

            runOnUiThread(() -> {
                entities.clear();
                entities.addAll(newEntities);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (entities.isEmpty()) {
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setText("No data available");
                } else {
                    emptyView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        } catch (Exception e) {
            Log.e("Dashboard", "Error processing response", e);
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("Error loading data: " + e.getMessage());
                showToast("Error: " + e.getMessage());
            });
        }
    }
    @Override
    public void onItemClick(int position) {
        Entity selectedEntity = entities.get(position);
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("entity", selectedEntity);
        startActivity(intent);
    }
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}