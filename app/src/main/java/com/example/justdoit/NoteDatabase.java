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

@Database(entities = {Note.class}, version = 1)
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
            noteDao.insert(new Note("Watch lectures", "Education", 1, false, yesterday, "Lectures 11, 12", false, ""));
            noteDao.insert(new Note("Work on report", "Education", 2, false, today, "", false, ""));
            noteDao.insert(new Note("Submit app", "Work", 5,false, today, "", false, ""));
            noteDao.insert(new Note("Buy apples", "Shopping", 4,false, tomorrow, "", false, ""));
            noteDao.insert(new Note("Example tasks", "Work", 3,false, tomorrow, "", false, ""));
            return null;
        }
    }
}
