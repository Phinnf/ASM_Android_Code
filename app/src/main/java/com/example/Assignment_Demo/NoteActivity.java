package com.example.Assignment_Demo;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {

    private RecyclerView notesRecyclerView;
    private NoteAdapter noteAdapter;
    private List<Note> noteList;
    private DatabaseHelper dbHelper;
    private int currentUserId;

    private EditText etNewNoteContent;
    private Button btnAddNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note); // We will create this XML next

        // Get the User ID passed from LayerActivity
        currentUserId = getIntent().getIntExtra("USER_ID", -1);
        if (currentUserId == -1) {
            Toast.makeText(this, "User not found", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        etNewNoteContent = findViewById(R.id.etNewNoteContent);
        btnAddNote = findViewById(R.id.btnAddNote);

        // Setup the "Create" functionality
        btnAddNote.setOnClickListener(v -> {
            String content = etNewNoteContent.getText().toString().trim();
            if (!content.isEmpty()) {
                addNoteToList(content);
                etNewNoteContent.setText(""); // Clear the input
            } else {
                Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        setupRecyclerView();
        loadNotesFromDatabase();
    }

    private void setupRecyclerView() {
        noteList = new ArrayList<>();
        // Pass the dbHelper to the adapter so it can perform delete/update
        noteAdapter = new NoteAdapter(this, noteList, dbHelper);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(noteAdapter);
    }

    private void loadNotesFromDatabase() {
        noteList.clear(); // Clear the existing list first
        Cursor cursor = dbHelper.getNotes(currentUserId);

        if (cursor.getCount() > 0) {
            // Get column indices
            int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_ID);
            int contentIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_CONTENT);
            int completeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_IS_COMPLETE);
            int dateIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_DATE);
            int userIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_USER_ID);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                String content = cursor.getString(contentIndex);
                boolean isComplete = cursor.getInt(completeIndex) == 1; // Convert 1/0 to true/false
                String date = cursor.getString(dateIndex);
                int userId = cursor.getInt(userIndex);

                noteList.add(new Note(id, content, isComplete, date, userId));
            }
        }
        cursor.close();
        noteAdapter.notifyDataSetChanged(); // Refresh the list
    }

    private void addNoteToList(String content) {
        // Get current date as string
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Add to database
        boolean success = dbHelper.addNote(currentUserId, content, currentDate);

        if (success) {
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
            // Reload all notes from DB to get the new one
            loadNotesFromDatabase();
        } else {
            Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show();
        }
    }
}