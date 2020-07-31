package com.example.justdoit;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    public LiveData<List<Note>> allNotes;
    public MutableLiveData<String> sortCol = new MutableLiveData<String>();
    public MutableLiveData<String> searchString = new MutableLiveData<String>("%");
    public CustomLiveData params = new CustomLiveData(sortCol, searchString);

    public NoteViewModel(@NonNull final Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = Transformations.switchMap(params, val -> {
            switch (val.first) {
                case "DATE":
                    return repository.getAllNotesDate("%" + val.second + "%");
                case "PRIORITY":
                    return repository.getAllNotesPriority("%" + val.second + "%");
            }
            return null;
        });
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
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

class CustomLiveData extends MediatorLiveData<Pair<String, String>> {
    public CustomLiveData(LiveData<String> sortCol, LiveData<String> searchString) {
        addSource(sortCol, new Observer<String>() {
            public void onChanged(@Nullable String first) {
                setValue(Pair.create(first, searchString.getValue()));
            }
        });
        addSource(searchString, new Observer<String>() {
            public void onChanged(@Nullable String second) {
                setValue(Pair.create(sortCol.getValue(), second));
            }
        });
    }
}
