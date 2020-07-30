package com.example.justdoit;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String description;

    private int priority;

    private boolean completed;

    @ColumnInfo(name = "due_at")
    @TypeConverters({TimestampConverter.class})
    private Date dueAt;

    public Note(String title, String description, int priority, boolean completed, Date dueAt) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.completed = completed;
        this.dueAt = dueAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Date getDueAt() {
        return dueAt;
    }
}
