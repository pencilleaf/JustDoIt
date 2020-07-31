package com.example.justdoit;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    public LiveData<List<Note>> allNotes;
    public MutableLiveData<String> sortCol = new MutableLiveData<String>();

    public NoteViewModel(@NonNull final Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = Transformations.switchMap(sortCol, sort -> {
            switch (sort) {
                case "DATE":
                    return repository.getAllNotesDate();
                case "PRIORITY":
                    return repository.getAllNotesPriority();
            }
            return null;
        });
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note){
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
