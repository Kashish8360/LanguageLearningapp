package com.example.languagelearningapp;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Entity entity = (Entity) getIntent().getSerializableExtra("entity");

        if (entity == null) {
            finish(); // Close activity if no entity
            return;
        }
        displayEntityDetails(entity);
    }

    private void displayEntityDetails(Entity entity) {
        TextView property1TextView = findViewById(R.id.detailProperty1);
        TextView property2TextView = findViewById(R.id.detailProperty2);
        TextView descriptionTextView = findViewById(R.id.detailDescription);

        property1TextView.setText(entity.getProperty1());
        property2TextView.setText(entity.getProperty2());
        descriptionTextView.setText(entity.getDescription());
    }
}