package com.bfong.notepadbf;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteViewHolder extends RecyclerView.ViewHolder {

    public TextView title;
    TextView date;
    TextView text;

    NoteViewHolder(@NonNull View view) {
        super(view);
        title = view.findViewById(R.id.titleText);
        date = view.findViewById(R.id.date);
        text = view.findViewById(R.id.noteText);
    }
}
