package com.example.justdoit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String EXTRA_ID =
            "com.example.justdoit.EXTRA_TID";
    public static final String EXTRA_TITLE =
            "com.example.justdoit.EXTRA_TITLE";
    public static final String EXTRA_CATEGORY =
            "com.example.justdoit.EXTRA_CATEGORY";
    public static final String EXTRA_PRIORITY =
            "com.example.justdoit.EXTRA_PRIORITY";
    public static final String EXTRA_DUEAT =
            "com.example.justdoit.EXTRA_DUEAT";
    public static final String EXTRA_COMPLETED =
            "com.example.justdoit.EXTRA_COMPLETED";

    private EditText editTextTitle;
    private EditText editTextCategory;
    private NumberPicker numberPickerPriority;
    private TextView textViewDueDate;
    private ImageButton datePickerButton;
    private boolean completed;

    private static DateFormat df = new SimpleDateFormat("EEE d MMM yy", Locale.ENGLISH);
    private static DateFormat tf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        df.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        tf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextCategory = findViewById(R.id.edit_text_category);
        numberPickerPriority = findViewById(R.id.number_picker_priority);
        textViewDueDate = findViewById(R.id.text_view_duedate);
        datePickerButton = findViewById(R.id.date_picker_button);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(5);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextCategory.setText(intent.getStringExtra(EXTRA_CATEGORY));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
            textViewDueDate.setText(intent.getStringExtra(EXTRA_DUEAT));
            completed = intent.getBooleanExtra(EXTRA_COMPLETED, false);
        } else {
            setTitle("Add Note");
        }

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(intent.getStringExtra(EXTRA_DUEAT));
            }
        });

    }

    private void datePicker(String date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.setTimeInMillis(0);
                calendar.set(year, month, dayOfMonth, 0, 0, 0);
                Date chosenDate = calendar.getTime();
                String formatDate = df.format(chosenDate);
                textViewDueDate.setText(formatDate);
                timePicker(formatDate);
            }
        }, year, month, dayOfMonth);
        calendar.add(Calendar.MONTH, 3);
        long now = System.currentTimeMillis() - 1000;
        long maxDate = calendar.getTimeInMillis();
        datePickerDialog.getDatePicker().setMinDate(now);
        datePickerDialog.getDatePicker().setMaxDate(maxDate); //After one month from now
        datePickerDialog.show();
    }

    private void timePicker(final String date){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.setTimeInMillis(0);
                calendar.set(2020, 1, 1, hourOfDay, minute, 0);
                Date chosenTime = calendar.getTime();
                String formatTime = tf.format(chosenTime);
                String dateTime = date + "   " + formatTime;
                textViewDueDate.setText(dateTime);
            }
        }, hour + 1, minute, false);
//        timePickerDialog.setMin(hour + 1, minute);
        timePickerDialog.show();
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String category = editTextCategory.getText().toString();
        int priority = numberPickerPriority.getValue();
        String date = textViewDueDate.getText().toString();

        if (title.trim().isEmpty() || category.trim().isEmpty() || date.trim().isEmpty()) {
            Toast.makeText(this, "Please insert a title and date", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_CATEGORY, category);
        data.putExtra(EXTRA_PRIORITY, priority);
        data.putExtra(EXTRA_DUEAT, date);
        data.putExtra(EXTRA_COMPLETED, completed);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}