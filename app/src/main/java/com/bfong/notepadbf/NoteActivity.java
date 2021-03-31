package com.bfong.notepadbf;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;

public class NoteActivity extends AppCompatActivity {

    private static final String TAG = "NoteActivity";
    private static String oTitle;
    private static String oText;
    private EditText title;
    private EditText text;
    private Note n;
    private int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        title = findViewById(R.id.titleText);
        text = findViewById(R.id.noteText);
        text.setMovementMethod(new ScrollingMovementMethod());
        getSupportActionBar().setTitle("Create Note");

        Intent intent = getIntent();
        if (intent.hasExtra("NOTE")) {
            getSupportActionBar().setTitle("Edit Note");
            n = (Note) intent.getSerializableExtra("NOTE");
            title.setText(n.getTitle());
            oTitle = n.getTitle();
            text.setText(n.getText());
            oText = n.getText();
        } else {
            n = null;
        }
        if (intent.hasExtra("POS")) {
            pos = intent.getIntExtra("POS", -1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Log.d(TAG, "onOptionsItemSelected: Save");
                if (!(title.getText().toString().isEmpty())) {
                    if (!(title.getText().toString().equals(oTitle) && text.getText().toString().equals(oText))) {
                        doReturn(null);
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(this, "Note without a title was not saved!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void doReturn(View v) {
        Log.d(TAG, "doReturn: started return");
        String t = title.getText().toString().trim();
        Date d = new Date();
        String txt = text.getText().toString();
        if (t.length() > 0) {
            if (n == null) {
                n = new Note(t, d, txt);
            } else {
                n.setTitle(t);
                n.setDate(d);
                n.setText(txt);
            }
        }
        Intent intent = new Intent();
        intent.putExtra("NOTE", n);
        if (pos != -1) {
            intent.putExtra("POS", pos);
        }
        setResult(RESULT_OK, intent);
        Log.d(TAG, "doReturn: packed up note");
        finish();
    }

    @Override
    public void onBackPressed() {

        final AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setIcon(R.drawable.baseline_note_add_black_48);

        b.setTitle("Your note is not saved!");
        b.setMessage("Save note \"" + title.getText().toString() + "\"?");
        b.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!(title.getText().length() == 0)) {
                    if (!(title.getText().toString().equals(oTitle) && text.getText().toString().equals(oText))) {
                        doReturn(null);
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(b.getContext(), "Note without a title was not saved!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
        b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = b.create();
        dialog.show();
    }
}