package com.example.pbo;

import android.app.DatePickerDialog;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class UploadTaskActivity extends AppCompatActivity {

    private EditText editTextTaskTitle, editTextTaskCategory;
    private Button buttonTaskStartDate, buttonTaskEndDate, buttonUploadTask;
    private Spinner spinnerTaskStatus;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_task);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextTaskTitle = findViewById(R.id.editTextTaskTitle);
        editTextTaskCategory = findViewById(R.id.editTextTaskCategory);
        buttonTaskStartDate = findViewById(R.id.buttonTaskStartDate);
        buttonTaskEndDate = findViewById(R.id.buttonTaskEndDate);
        spinnerTaskStatus = findViewById(R.id.spinnerTaskStatus);
        buttonUploadTask = findViewById(R.id.buttonUploadTask);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.task_status_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTaskStatus.setAdapter(adapter);

        buttonTaskStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((date) -> {
                    startDate = date;
                    buttonTaskStartDate.setText(date);
                });
            }
        });

        buttonTaskEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog((date) -> {
                    endDate = date;
                    buttonTaskEndDate.setText(date);
                });
            }
        });

        buttonUploadTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTextTaskTitle.getText().toString();
                String category = editTextTaskCategory.getText().toString();
                String status = spinnerTaskStatus.getSelectedItem().toString();

                if (!title.isEmpty() && !category.isEmpty() && startDate != null && endDate != null) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        Task newTask = new Task(title, category, startDate, endDate, status);

                        // Simpan tugas baru ke Firestore
                        Map<String, Object> taskMap = new HashMap<>();
                        taskMap.put("title", title);
                        taskMap.put("category", category);
                        taskMap.put("startDate", startDate);
                        taskMap.put("endDate", endDate);
                        taskMap.put("status", status);
                        taskMap.put("userId", userId);

                        db.collection("tasks").add(taskMap).addOnSuccessListener(documentReference -> {
                            Toast.makeText(UploadTaskActivity.this, "Task Uploaded", Toast.LENGTH_SHORT).show();
                            finish(); // Kembali ke MainActivity
                        }).addOnFailureListener(e -> {
                            Toast.makeText(UploadTaskActivity.this, "Failed to upload task", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(UploadTaskActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UploadTaskActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog(DatePickerCallback callback) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            callback.onDateSet(date);
        }, year, month, day).show();
    }

    interface DatePickerCallback {
        void onDateSet(String date);
    }
}
