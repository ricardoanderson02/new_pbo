package com.example.pbo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.core.view.GravityCompat;

import com.example.pbo.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.snackbar.Snackbar;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Intent untuk membuka UploadTaskActivity
                Intent intent = new Intent(MainActivity.this, UploadTaskActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderUsername = headerView.findViewById(R.id.nav_header_username);
        TextView navHeaderEmail = headerView.findViewById(R.id.nav_header_email);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String username = document.getString("username");
                        String email = document.getString("email");
                        navHeaderUsername.setText(username);
                        navHeaderEmail.setText(email);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error getting user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Initialize RecyclerView and task list
        recyclerView = binding.contentMain.recyclerViewTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);

        // Load tasks from Firestore
        loadTasksFromFirestore();
    }

    private void loadTasksFromFirestore() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("tasks")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            taskList.clear();
                            QuerySnapshot querySnapshot = task.getResult();
                            for (DocumentSnapshot document : querySnapshot) {
                                Task taskItem = document.toObject(Task.class);
                                taskItem.setId(document.getId());
                                taskList.add(taskItem);
                            }
                            taskAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(MainActivity.this, "Error getting tasks.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(null, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void toggleDrawer(View view) {
        DrawerLayout drawerLayout = binding.drawerLayout;
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    // Methods for editing, deleting, and marking tasks as complete
    public void editTask(Task task) {
        Intent intent = new Intent(MainActivity.this, EditTaskActivity.class);
        intent.putExtra("task", task);
        startActivity(intent);
    }

    public void deleteTask(Task task) {
        db.collection("tasks").document(task.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    taskList.remove(task);
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error deleting task", Toast.LENGTH_SHORT).show());
    }

    public void markTaskComplete(Task task) {
        task.setStatus("Completed");
        db.collection("tasks").document(task.getId())
                .update("status", "Completed")
                .addOnSuccessListener(aVoid -> taskAdapter.notifyDataSetChanged())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Error updating task", Toast.LENGTH_SHORT).show());
    }
}
