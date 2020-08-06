package com.example.justdoit;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "note_table")
public class Note {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String category;

    private int priority;

    private boolean completed;

    private String description;

    private boolean reminder;

    private String attachment;

    @ColumnInfo(name = "due_at")
    @TypeConverters({TimestampConverter.class})
    private Date dueAt;

    public Note(String title, String category, int priority, boolean completed, Date dueAt, String description, boolean reminder, String attachment) {
        this.title = title;
        this.category = category;
        this.priority = priority;
        this.completed = completed;
        this.dueAt = dueAt;
        this.description = description;
        this.reminder = reminder;
        this.attachment = attachment;
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

    public String getCategory() {
        return category;
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

    public String getDescription() {
        return description;
    }

    public boolean isReminder() {
        return reminder;
    }

    public String getAttachment() {
        return attachment;
    }
}
