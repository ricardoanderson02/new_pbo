package com.example.pbo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private Context context;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.textViewTitle.setText(task.getTitle());
        holder.textViewCategory.setText(task.getCategory());
        holder.textViewCreationDate.setText("Created: " + task.getStartDate());
        holder.textViewDueDate.setText("Due: " + task.getEndDate());
        holder.textViewStatus.setText("Status: " + task.getStatus());

        holder.buttonEdit.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).editTask(task);
            }
        });

        holder.buttonDelete.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).deleteTask(task);
            }
        });

        holder.buttonComplete.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).markTaskComplete(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle, textViewCategory, textViewCreationDate, textViewDueDate, textViewStatus;
        public Button buttonEdit, buttonDelete;
        public ImageButton buttonComplete;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewCreationDate = itemView.findViewById(R.id.textViewCreationDate);
            textViewDueDate = itemView.findViewById(R.id.textViewDueDate);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonComplete = itemView.findViewById(R.id.buttonComplete);
        }
    }
}
