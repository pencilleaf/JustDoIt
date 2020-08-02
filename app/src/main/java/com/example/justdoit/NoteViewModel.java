package com.example.justdoit;


import android.app.Application;
import android.util.Log;

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
    public MutableLiveData<String> searchString = new MutableLiveData<String>("");
    public MutableLiveData<String> category = new MutableLiveData<String>("All");
    public CustomLiveData params = new CustomLiveData(sortCol, searchString, category);

    public NoteViewModel(@NonNull final Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = Transformations.switchMap(params, val -> {
            Log.d("LIVEDATA", val.first + val.second + val.third);
            if  (!val.second.isEmpty() || val.third.equals("All")){
                Log.d("LIVEDATA", "Get all notes");
                switch (val.first) {
                    case "DATE":
                        return repository.getAllNotesDate("%" + val.second + "%");
                    case "PRIORITY":
                        return repository.getAllNotesPriority("%" + val.second + "%");
                }
            }
            if (!val.third.equals("All")){
                Log.d("LIVEDATA", "Get filtered notes");
                switch (val.first) {
                    case "DATE":
                        return repository.getFilteredNotesDate(val.third);
                    case "PRIORITY":
                        return repository.getFilteredNotesPriority(val.third);
                }
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

class Triple<X, Y, Z> {
    public final X first;
    public final Y second;
    public final Z third;

    public Triple(X first, Y second, Z third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

}

class CustomLiveData extends MediatorLiveData<Triple<String, String, String>> {
    public CustomLiveData(LiveData<String> sortCol, LiveData<String> searchString, LiveData<String> category) {
        addSource(sortCol, new Observer<String>() {
            public void onChanged(@Nullable String first) {
                String _searchString = searchString.getValue();
                String _category = category.getValue();
                setValue(new Triple<>(first, _searchString, _category));
            }
        });
        addSource(searchString, new Observer<String>() {
            public void onChanged(@Nullable String second) {
                String _sortCol = sortCol.getValue();
                String _category = category.getValue();
                setValue(new Triple<>(_sortCol, second, _category));
            }
        });
        addSource(category, new Observer<String>() {
            public void onChanged(@Nullable String third) {
                String _sortCol = sortCol.getValue();
                String _searchString = searchString.getValue();
                setValue(new Triple<>(_sortCol, _searchString, third));
            }
        });
    }
}

//class CustomLiveData extends MediatorLiveData<Pair<String, String>> {
//    public CustomLiveData(LiveData<String> sortCol, LiveData<String> searchString) {
//        addSource(sortCol, new Observer<String>() {
//            public void onChanged(@Nullable String first) {
//                setValue(Pair.create(first, searchString.getValue()));
//            }
//        });
//        addSource(searchString, new Observer<String>() {
//            public void onChanged(@Nullable String second) {
//                setValue(Pair.create(sortCol.getValue(), second));
//            }
//        });
//    }
//}
