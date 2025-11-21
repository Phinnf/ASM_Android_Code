package com.example.Assignment_Demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LayerActivity extends AppCompatActivity {

    private MaterialCardView cardNote;
    private MaterialCardView cardTrack;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAddExpense;
    private int currentUserId;
    private RecyclerView tasksRecyclerView;

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

        cardNote = findViewById(R.id.cardNote);
        cardTrack = findViewById(R.id.cardTrack);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabAddExpense = findViewById(R.id.fabAddExpense);

        cardNote.setOnClickListener(v -> {
            // Start the new NoteActivity and pass the USER_ID to it
            Intent intent = new Intent(LayerActivity.this, NoteActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        cardTrack.setOnClickListener(v -> {
            // Navigate to the BudgetActivity
            Intent intent = new Intent(LayerActivity.this, BudgetActivity.class);
            // Pass the current user ID (Good practice, even if BudgetActivity uses Prefs)
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });
        cardTrack.setOnClickListener(v -> {
            Intent intent = new Intent(LayerActivity.this, CategoryAnalyticsActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(LayerActivity.this, AddExpenseActivity.class);
            intent.putExtra("USER_ID", currentUserId); // Also pass the ID here
            startActivity(intent);
        });

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
