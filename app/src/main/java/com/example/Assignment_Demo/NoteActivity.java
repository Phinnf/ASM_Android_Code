package com.example.Assignment_Demo;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity implements NoteAdapter.OnNoteListener {

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
        setContentView(R.layout.activity_note);

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

        btnAddNote.setOnClickListener(v -> {
            String content = etNewNoteContent.getText().toString().trim();
            if (!content.isEmpty()) {
                addNoteToList(content);
                etNewNoteContent.setText("");
            } else {
                Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        setupRecyclerView();
        loadNotesFromDatabase();
    }

    private void setupRecyclerView() {
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(this, noteList, dbHelper, this);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        notesRecyclerView.setAdapter(noteAdapter);
    }

    private void loadNotesFromDatabase() {
        noteList.clear();
        Cursor cursor = dbHelper.getNotes(currentUserId);

        if (cursor.getCount() > 0) {
            int idIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_ID);
            int contentIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_CONTENT);
            int completeIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_IS_COMPLETE);
            int dateIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_DATE);
            int userIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NOTE_USER_ID);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                String content = cursor.getString(contentIndex);
                boolean isComplete = cursor.getInt(completeIndex) == 1;
                String date = cursor.getString(dateIndex);
                int userId = cursor.getInt(userIndex);

                noteList.add(new Note(id, content, isComplete, date, userId));
            }
        }
        cursor.close();
        noteAdapter.notifyDataSetChanged();
    }

    private void addNoteToList(String content) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean success = dbHelper.addNote(currentUserId, content, currentDate);

        if (success) {
            Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
            loadNotesFromDatabase();
        } else {
            Toast.makeText(this, "Failed to add note", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNoteClick(int position) {
        // Handle note click
    }

    @Override
    public void onNoteEditClick(int position) {
        Note note = noteList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        final EditText input = new EditText(this);
        input.setText(note.getContent());
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newContent = input.getText().toString();
                dbHelper.updateNoteContent(note.getId(), newContent);
                loadNotesFromDatabase();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
