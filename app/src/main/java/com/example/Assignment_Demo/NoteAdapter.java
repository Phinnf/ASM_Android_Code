package com.example.Assignment_Demo;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> noteList;
    private Context context;
    private DatabaseHelper dbHelper;

    public NoteAdapter(Context context, List<Note> noteList, DatabaseHelper dbHelper) {
        this.context = context;
        this.noteList = noteList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        holder.tvNoteContent.setText(note.getContent());
        holder.cbNoteComplete.setChecked(note.isComplete());

        // Call the helper function to set strikethrough
        setStrikethrough(holder.tvNoteContent, note.isComplete());

        // UPDATE: Handle CheckBox click
        holder.cbNoteComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the database
            dbHelper.updateNoteCompletion(note.getId(), isChecked);

            // Update the local model
            note.setComplete(isChecked);

            // Re-apply the strikethrough style
            setStrikethrough(holder.tvNoteContent, isChecked);
        });

        // DELETE: Handle Delete button click
        holder.btnDeleteNote.setOnClickListener(v -> {
            // Remove from database
            dbHelper.deleteNote(note.getId());

            // Remove from list
            noteList.remove(position);

            // Notify adapter
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, noteList.size());
        });
    }

    /**
     * Helper method to apply or remove strikethrough
     * This is the equivalent of the <del> tag
     */
    private void setStrikethrough(TextView textView, boolean isComplete) {
        if (isComplete) {
            // Add strikethrough
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(Color.GRAY);
        } else {
            // Remove strikethrough
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // ViewHolder class
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbNoteComplete;
        TextView tvNoteContent;
        ImageButton btnDeleteNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            cbNoteComplete = itemView.findViewById(R.id.cbNoteComplete);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);
            btnDeleteNote = itemView.findViewById(R.id.btnDeleteNote);
        }
    }
}
