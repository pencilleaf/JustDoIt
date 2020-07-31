package com.example.justdoit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    @Query("SELECT * FROM note_table ORDER BY due_at ASC, priority DESC")
    LiveData<List<Note>> getAllNotesDate();

    @Query("SELECT * FROM note_table ORDER BY priority DESC, due_at ASC")
    LiveData<List<Note>> getAllNotesPriority();

}
