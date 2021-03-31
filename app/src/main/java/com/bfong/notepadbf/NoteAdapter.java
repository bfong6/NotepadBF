package com.bfong.notepadbf;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private static final String TAG = "NoteAdapter";
    private List<Note> noteList;
    private MainActivity mainAct;

    NoteAdapter(List<Note> list, MainActivity ma) {
        this.noteList = list;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        Note note = noteList.get(position);

        holder.title.setText(note.getTitle());
        holder.date.setText(DateFormat.format("E MMM dd, hh:mm aa", note.getDate()));
        String n = note.getText();
        if (n.length() <= 80)
            holder.text.setText(n);
        else
            holder.text.setText(n.substring(0, 80) + "...");
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
