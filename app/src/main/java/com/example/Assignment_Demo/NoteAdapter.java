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
    private OnNoteListener onNoteListener;

    public NoteAdapter(Context context, List<Note> noteList, DatabaseHelper dbHelper, OnNoteListener onNoteListener) {
        this.context = context;
        this.noteList = noteList;
        this.dbHelper = dbHelper;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_note, parent, false);
        return new NoteViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = noteList.get(position);

        holder.tvNoteContent.setText(note.getContent());
        holder.cbNoteComplete.setChecked(note.isComplete());

        setStrikethrough(holder.tvNoteContent, note.isComplete());

        holder.cbNoteComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.updateNoteCompletion(note.getId(), isChecked);
            note.setComplete(isChecked);
            setStrikethrough(holder.tvNoteContent, isChecked);
        });

        holder.btnDeleteNote.setOnClickListener(v -> {
            dbHelper.deleteNote(note.getId());
            noteList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, noteList.size());
        });

        holder.btnEditNote.setOnClickListener(v -> {
            onNoteListener.onNoteEditClick(holder.getAdapterPosition());
        });
    }

    private void setStrikethrough(TextView textView, boolean isComplete) {
        if (isComplete) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(Color.GRAY);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            textView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckBox cbNoteComplete;
        TextView tvNoteContent;
        ImageButton btnDeleteNote;
        ImageButton btnEditNote;
        OnNoteListener onNoteListener;

        public NoteViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            cbNoteComplete = itemView.findViewById(R.id.cbNoteComplete);
            tvNoteContent = itemView.findViewById(R.id.tvNoteContent);
            btnDeleteNote = itemView.findViewById(R.id.btnDeleteNote);
            btnEditNote = itemView.findViewById(R.id.btnEditNote);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
        void onNoteEditClick(int position);
    }
}
