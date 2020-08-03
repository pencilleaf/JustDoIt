package com.example.justdoit;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    public static final int REMINDER_BROADCAST_REQUEST = 3;
    private NoteViewModel noteViewModel;
    private AppBarConfiguration mAppBarConfiguration;
    final NoteAdapter adapter = new NoteAdapter();
    private RadioButton radioDate;
    private Spinner spinner;
    private String[] categoriesList = {
            "All",
            "Education",
            "Work",
            "Shopping",
            "Health",
            "Chores",
            "Entertainment"
    };

    private static DateFormat df = new SimpleDateFormat("EEE d MMM yy HH:mm", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        FloatingActionButton buttonAddNote = findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        radioDate = findViewById(R.id.radio_date);
        radioDate.setChecked(true);

        spinner = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.sortCol.setValue("DATE");
        noteViewModel.allNotes.observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // update RecyclerView
                adapter.submitList(notes);
            }
        });


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        df.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        adapter.setOnCheckClickListener(new NoteAdapter.OnItemCheckClickListener() {
            @Override
            public void onCheckClick(Note note) {
                Note newNote = new Note(note.getTitle(), note.getCategory(), note.getPriority(), !note.isCompleted(), note.getDueAt(), note.getDescription(), note.isReminder());
                newNote.setId(note.getId());
                noteViewModel.update(newNote);
                Toast.makeText(MainActivity.this, "Toggle complete", Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_CATEGORY, note.getCategory());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.getPriority());
                intent.putExtra(AddEditNoteActivity.EXTRA_DUEAT, df.format(note.getDueAt()));
                intent.putExtra(AddEditNoteActivity.EXTRA_COMPLETED, note.isCompleted());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_REMINDER, note.isReminder());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
        String category = data.getStringExtra(AddEditNoteActivity.EXTRA_CATEGORY);
        int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
        String date = data.getStringExtra(AddEditNoteActivity.EXTRA_DUEAT);
        boolean completed = data.getBooleanExtra(AddEditNoteActivity.EXTRA_COMPLETED, false);
        String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
        boolean reminder = data.getBooleanExtra(AddEditNoteActivity.EXTRA_REMINDER, false);

        try {
            Date dateInsert = df.parse(date);
            Note note = new Note(title, category, priority, completed, dateInsert, description, reminder);
            if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
                noteViewModel.insert(note);
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
            } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
                int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
                if (id == -1) {
                    Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                    return;
                }
                note.setId(id);
                noteViewModel.update(note);
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (reminder) {
            Intent intent = new Intent(MainActivity.this, ReminderBroadcast.class);
            intent.putExtra("title", title);
            intent.putExtra("category", category);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, REMINDER_BROADCAST_REQUEST, intent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            try {
                Date dateInsert = df.parse(date);
                alarmManager.set(AlarmManager.RTC_WAKEUP, dateInsert.getTime() ,pendingIntent);
                Toast.makeText(this, "Reminder set", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                noteViewModel.searchString.setValue(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radio_date:
                if (checked)
                    noteViewModel.sortCol.setValue("DATE");
                    break;
            case R.id.radio_priority:
                if (checked)
                    noteViewModel.sortCol.setValue("PRIORITY");
                    break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String string = adapterView.getItemAtPosition(i).toString();
        noteViewModel.category.setValue(string);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "JustDoItReminderChannel";
            String description = "Channel for JustDoIt Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyJustDoIt", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}