package com.bfong.notepadbf;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable, Comparable<Note> {
    private String title;
    private Date date;
    private String text;

    Note(String t, Date d, String txt) {
        this.title = t;
        this.date = d;
        this.text = txt;
    }

    Note(JsonReader reader) throws IOException {
        String t = "";
        Date d;
        String txt = "";

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("title"))
                t = reader.nextString();
            else if (name.equals("date"))
                d = new Date(reader.nextLong());
            else if (name.equals("text"))
                txt = reader.nextString();
        }
        reader.endObject();
    }

    String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        title = s;
    }

    Date getDate() {
        return date;
    }

    public void setDate(Date d) {
        date = d;
    }

    String getText() {
        return text;
    }

    public void setText(String s) {
        text = s;
    }

    public void save(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("title").value(this.title);
        writer.name("date").value(this.date.getTime());
        writer.name("text").value(this.text);
        writer.endObject();
    }

    @Override
    public int compareTo(Note n) {
        Date c = n.getDate();
        return c.compareTo(this.getDate());
    }

    public boolean equals(Note n) {
        if (!n.getTitle().equals(this.title))
            return false;
        if (!n.getDate().equals(this.date))
            return false;
        if (!n.getText().equals(this.text))
            return false;
        return true;
    }

    public String toString() {
        return this.title;
    }
}
