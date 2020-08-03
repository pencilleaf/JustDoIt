package com.example.justdoit;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.Date;

@Database(entities = {Note.class}, version = 4)
@TypeConverters({TimestampConverter.class})
public abstract class NoteDatabase extends RoomDatabase {

    private static NoteDatabase instance;

    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context){
        // Singleton
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,"note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase db){
            noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Date today = new Date();
            Date yesterday = new Date(today.getTime() - 24*3600*1000);
            Date tomorrow = new Date(today.getTime() + 24*3600*1000);
            noteDao.insert(new Note("Title 1", "Education", 1, false, yesterday, "Description 1", false));
            noteDao.insert(new Note("Title 2", "Work", 2,false, today, "Description 2", false));
            noteDao.insert(new Note("Title 3", "Shopping", 3,false, tomorrow, "Description 3", false));
            return null;
        }
    }
}
