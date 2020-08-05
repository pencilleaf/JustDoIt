package com.example.justdoit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddEditNoteActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
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
    public static final String EXTRA_DESCRIPTION =
            "com.example.justdoit.EXTRA_DESCRIPTION";
    public static final String EXTRA_REMINDER =
            "com.example.justdoit.EXTRA_REMINDER";
    public static final String EXTRA_ATTACHMENT =
            "com.example.justdoit.EXTRA_ATTACHMENT";

    private EditText editTextTitle;
    private EditText editTextDescription;
    private Spinner spinnerCategorySelect;
    private TextView textViewCategory;
    private Spinner spinnerPrioritySelect;
    private TextView textViewPriority;
    private TextView textViewDueDate;
    private ImageButton datePickerButton;
    private Switch switchReminder;
    private TextView textViewReminder;
    private boolean completed;
    private String category;
    private ImageButton buttonAddAttachment;
    private ImageButton buttonRemoveAttachment;
    private ImageView imageViewAttachment;
    private String selectedImageUri;

    private String[] priorityList = {"1","2","3","4","5"};
    private String[] categoriesList = {
            "Education",
            "Work",
            "Shopping",
            "Health",
            "Chores",
            "Entertainment"
    };

    private static DateFormat df = new SimpleDateFormat("EEE d MMM yy", Locale.ENGLISH);
    private static DateFormat tf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        df.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        tf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        spinnerCategorySelect = findViewById(R.id.spinner_category_select);
        textViewCategory = findViewById(R.id.text_view_category);
        spinnerPrioritySelect = findViewById(R.id.spinner_priority_select);
        textViewPriority = findViewById(R.id.text_view_priority);
        textViewDueDate = findViewById(R.id.text_view_duedate);
        datePickerButton = findViewById(R.id.date_picker_button);
        switchReminder = findViewById(R.id.switch_reminder);
        textViewReminder = findViewById(R.id.text_view_reminder);
        buttonAddAttachment =findViewById(R.id.button_add_attachment);
        buttonRemoveAttachment = findViewById(R.id.button_remove_attachment);
        imageViewAttachment = findViewById(R.id.image_view_attachment);

        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categoriesList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerCategorySelect.setAdapter(arrayAdapter);
        spinnerCategorySelect.setOnItemSelectedListener(this);
        spinnerCategorySelect.setSelection(0);

        ArrayAdapter<CharSequence> arrayAdapter_ = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorityList);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerPrioritySelect.setAdapter(arrayAdapter_);
        spinnerPrioritySelect.setOnItemSelectedListener(this);
        spinnerPrioritySelect.setSelection(0);

        switchReminder.setOnCheckedChangeListener(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Date currentDateTime = new Date();
        Date defaultDateTime = new Date(currentDateTime.getTime() + 3600*1000);
        textViewDueDate.setText(df.format(defaultDateTime) + " " + tf.format(defaultDateTime));
        selectedImageUri = "";

        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            category = intent.getStringExtra(EXTRA_CATEGORY);
            List<String> categories = Arrays.asList(categoriesList);
            int ind = categories.indexOf(category);
            spinnerCategorySelect.setSelection(ind);
            switchReminder.setChecked(intent.getBooleanExtra(EXTRA_REMINDER,false));

            spinnerPrioritySelect.setSelection(intent.getIntExtra(EXTRA_PRIORITY, 0) - 1);
            textViewDueDate.setText(intent.getStringExtra(EXTRA_DUEAT));
            completed = intent.getBooleanExtra(EXTRA_COMPLETED, false);
            selectedImageUri = intent.getStringExtra(EXTRA_ATTACHMENT);

            if (selectedImageUri != "") {
                Glide.with(this).load(Uri.parse(selectedImageUri)).into(imageViewAttachment);
            }

        } else {
            setTitle("Add Note");
        }

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker(intent.getStringExtra(EXTRA_DUEAT));
            }
        });

        buttonAddAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an image"), 0);
            }
        });
        buttonRemoveAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImageUri = "";
                imageViewAttachment.setImageDrawable(null);
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
        timePickerDialog.show();
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = Integer.parseInt(textViewPriority.getText().toString());
        String date = textViewDueDate.getText().toString();

        if (title.trim().isEmpty() || date.equals("None")) {
            Toast.makeText(this, "Please insert a title and due date", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_CATEGORY, category);
        data.putExtra(EXTRA_PRIORITY, priority);
        data.putExtra(EXTRA_DUEAT, date);
        data.putExtra(EXTRA_COMPLETED, completed);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_REMINDER, switchReminder.isChecked());
        data.putExtra(EXTRA_ATTACHMENT, selectedImageUri);

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
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()) {
            case R.id.spinner_category_select:
                category = adapterView.getItemAtPosition(i).toString();
                textViewCategory.setText(category);
                break;
            case R.id.spinner_priority_select:
                textViewPriority.setText(adapterView.getItemAtPosition(i).toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            textViewReminder.setText("On");
        } else {
            textViewReminder.setText("Off");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                selectedImageUri = data.getData().toString();
                Glide.with(this).load(data.getData()).into(imageViewAttachment);
            }
        }
    }
}