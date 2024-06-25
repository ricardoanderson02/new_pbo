package com.example.pbo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);

        FloatingActionButton fab = findViewById(R.id.fabAddTask);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TaskListActivity.this, UploadTaskActivity.class));
            }
        });
    }

    public void editTask(Task task) {
        // Implementasikan logika untuk mengedit tugas
        Toast.makeText(this, "Edit task: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    public void deleteTask(Task task) {
        // Implementasikan logika untuk menghapus tugas
        taskList.remove(task);
        taskAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Task deleted: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    public void markTaskComplete(Task task) {
        // Implementasikan logika untuk menandai tugas sebagai selesai
        task.setStatus("Completed");
        taskAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Task marked as complete: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }

    public void unmarkTaskComplete(Task task) {
        // Implementasikan logika untuk menandai tugas sebagai belum selesai
        task.setStatus("Not Started");
        taskAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Task marked as not complete: " + task.getTitle(), Toast.LENGTH_SHORT).show();
    }
}