package com.example.pbo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextCategory;
    private Button buttonStartDate, buttonEndDate, buttonSave;
    private Spinner spinnerStatus;
    private FirebaseFirestore db;
    private Task task;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextCategory = findViewById(R.id.editTextCategory);
        buttonStartDate = findViewById(R.id.buttonStartDate);
        buttonEndDate = findViewById(R.id.buttonEndDate);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        buttonSave = findViewById(R.id.buttonSave);

        // Setting up the adapter for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("task")) {
            task = (Task) intent.getSerializableExtra("task");
            if (task != null) {
                editTextTitle.setText(task.getTitle());
                editTextCategory.setText(task.getCategory());
                buttonStartDate.setText(task.getStartDate());
                buttonEndDate.setText(task.getEndDate());
                // Set spinner status
                int spinnerPosition = adapter.getPosition(task.getStatus());
                spinnerStatus.setSelection(spinnerPosition);
            }
        }

        buttonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((Button) v);
            }
        });

        buttonEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((Button) v);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }

    private void showDatePickerDialog(final Button button) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        button.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void saveTask() {
        String title = editTextTitle.getText().toString();
        String category = editTextCategory.getText().toString();
        String startDate = buttonStartDate.getText().toString();
        String endDate = buttonEndDate.getText().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (title.isEmpty() || category.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || status.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure userId is set
        if (task != null) {
            String userId = mAuth.getCurrentUser().getUid();
            task.setUserId(userId);
            task.setTitle(title);
            task.setCategory(category);
            task.setStartDate(startDate);
            task.setEndDate(endDate);
            task.setStatus(status);

            db.collection("tasks").document(task.getId())
                    .set(task)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditTaskActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(EditTaskActivity.this, "Error updating task", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Error: Task data is missing", Toast.LENGTH_SHORT).show();
        }
    }
}
