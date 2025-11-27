package com.example.Assignment_Demo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LayerActivity extends AppCompatActivity {

    private MaterialCardView cardNote;
    private MaterialCardView cardTrack;
    private MaterialCardView cardSpendingHealth;
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
        cardSpendingHealth = findViewById(R.id.cardSpendingHealth);
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
        cardSpendingHealth.setOnClickListener(v -> {
            Intent intent = new Intent(LayerActivity.this, SpendingAnalysisActivity.class);
            intent.putExtra("USER_ID", currentUserId);
            startActivity(intent);
        });

        fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(LayerActivity.this, AddExpenseActivity.class);
            intent.putExtra("USER_ID", currentUserId); // Also pass the ID here
            startActivity(intent);
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        MenuNavigation.setupBottomNavigation(bottomNavigationView, this, currentUserId, R.id.nav_layers);
    }
}
