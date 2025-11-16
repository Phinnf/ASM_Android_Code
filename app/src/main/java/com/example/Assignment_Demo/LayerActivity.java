package com.example.Assignment_Demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LayerActivity extends AppCompatActivity {

    private Button btnAddNote;
    private Button btnTrackByCategory;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddExpense;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layer);

        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User ID not found in Intent", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnAddNote = findViewById(R.id.btnAddNote);
        btnTrackByCategory = findViewById(R.id.btnTrackByCategory);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAddExpense = findViewById(R.id.fabAddExpense);

        btnAddNote.setOnClickListener(v -> {
            Toast.makeText(LayerActivity.this, "Add Note clicked", Toast.LENGTH_SHORT).show();
            // TODO: Implement navigation or functionality for adding a note
        });

        btnTrackByCategory.setOnClickListener(v -> {
            Toast.makeText(LayerActivity.this, "Layer clicked", Toast.LENGTH_SHORT).show();
            // TODO: Implement navigation or functionality for tracking by category
        });
        fabAddExpense.setOnClickListener(v ->
                startActivity(new Intent(LayerActivity.this, AddExpenseActivity.class))
        );

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(LayerActivity.this, DashboardActivity.class);
                intent.putExtra("USER_ID", currentUserId);
                startActivity(intent);
                finish(); // Finish current activity to prevent back stack issues
                return true;
            } else if (itemId == R.id.nav_chart) {
                Toast.makeText(LayerActivity.this, "Chart clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_layers) {
                Toast.makeText(LayerActivity.this, "Layers clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_user) {
                Toast.makeText(LayerActivity.this, "User clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

        // Set the current item in the bottom navigation to 'layers' when this activity is created
        bottomNavigationView.setSelectedItemId(R.id.nav_layers);
    }
}