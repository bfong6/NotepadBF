package com.bfong.notepadbf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = "MainActivity";
    private static final int N_CODE = 161;
    private static final int E_CODE = 777;
    private final List<Note> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NoteAdapter nAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFile();

        recyclerView = findViewById(R.id.recycler);

        nAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportActionBar().setTitle("Notepad LXH (" + noteList.size() + ")");
    }

    private void loadFile() {
        try {
            InputStream i = getApplicationContext().openFileInput("Notes.json");

            BufferedReader r = new BufferedReader(new InputStreamReader(i, StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }

            JSONArray jsonArray = new JSONArray(sb.toString());
            for (int x = 0; x < jsonArray.length(); x++) {
                JSONObject jsonObject = jsonArray.getJSONObject(x);
                String t = jsonObject.getString("title");
                long d = jsonObject.getLong("date");
                String txt = jsonObject.getString("text");
                Note n = new Note(t, new Date(d), txt);
                Log.d(TAG, "loadFile: adding item " + t);
                noteList.add(0, n);
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "No saved notes found", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(noteList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.newNote:
                Log.d(TAG, "onOptionsItemSelected: Selected New Note");
                intent = new Intent(this, NoteActivity.class);
                startActivityForResult(intent, N_CODE);
                return true;
            case R.id.about:
                Log.d(TAG, "onOptionsItemSelected: Selected About");
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        Note e = noteList.get(pos);

        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("NOTE", e);
        intent.putExtra("POS", pos);

        startActivityForResult(intent, E_CODE);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        final Note del = noteList.get(pos);

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setIcon(R.drawable.baseline_delete_forever_black_48);
        b.setTitle("Delete Note");
        b.setMessage("Delete note \"" + noteList.get(pos).getTitle() + "\"?");
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                noteList.remove(del);
                nAdapter.notifyDataSetChanged();
                getSupportActionBar().setTitle("Notepad LXH (" + noteList.size() + ")");
            }
        });
        b.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = b.create();
        dialog.show();

        return true;
    }

    @Override
    protected void onPause() {
        saveNotes();
        super.onPause();
    }

    private void saveNotes() {
        Log.d(TAG, "saveNotes: Saving notes");
        Collections.sort(noteList);
        try {
            FileOutputStream f = getApplicationContext().openFileOutput("Notes.json", Context.MODE_PRIVATE);

            StringBuilder sb = new StringBuilder();
            for (Note n : noteList) {
                sb.append(n.toString() + ", ");
            }
            Log.d(TAG, "saveNotes: notes:\n" + sb.toString());

            JsonWriter w = new JsonWriter(new OutputStreamWriter(f, "UTF-8"));
            w.setIndent("  ");
            w.beginArray();
            for (int i = 0; i < noteList.size(); i++) {
                noteList.get(i).save(w);
            }
            w.endArray();
            w.close();

        } catch (Exception e) {
            Log.d(TAG, "saveNotes: Error");
            e.getStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: returning to MainActivity");
        if (requestCode == N_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Note n = (Note) data.getSerializableExtra("NOTE");
                    if (n != null) {
                        noteList.add(0, n);
                        Collections.sort(noteList);
                        nAdapter.notifyDataSetChanged();
                        getSupportActionBar().setTitle("Notepad LXH (" + noteList.size() + ")");
                    }
                }
            } else {
                Toast.makeText(this, "OTHER result not OK!", Toast.LENGTH_SHORT);
            }
        } else if (requestCode == E_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Note n = (Note) data.getSerializableExtra("NOTE");
                    if (n != null) {
                        int p = data.getIntExtra("POS", -1);
                        if (p != -1) {
                            noteList.remove(p);
                            noteList.add(0, n);
                        }
                        Collections.sort(noteList);
                        nAdapter.notifyDataSetChanged();
                        getSupportActionBar().setTitle("Notepad LXH (" + noteList.size() + ")");
                    }
                }
            } else {
                Toast.makeText(this, "OTHER result not OK!", Toast.LENGTH_SHORT);
            }
        } else {
            Toast.makeText(this, "Unexpected code received: " + requestCode, Toast.LENGTH_SHORT).show();
        }
    }
}