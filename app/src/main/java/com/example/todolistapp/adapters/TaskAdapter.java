package com.example.todolistapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.data.db.DatabaseAdapter;
import com.example.todolistapp.data.models.Task;
import com.example.todolistapp.databinding.ItemTaskTitleBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    private final AsyncListDiffer<Task> differ;

    private DatabaseAdapter databaseAdapter;

    public TaskAdapter(Context context) {
        this.context = context;

        // Define the DiffUtil callback
        DiffUtil.ItemCallback<Task> diffCallback = new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.equals(newItem);
            }
        };

        // Initialize the AsyncListDiffer
        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    // Setter for the list of tasks
    public void setTasks(List<Task> tasks) {
        differ.submitList(tasks);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskTitleBinding binding = ItemTaskTitleBinding.inflate(LayoutInflater.from(context), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = differ.getCurrentList().get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }


    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaskTitleBinding binding;

        public TaskViewHolder(ItemTaskTitleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Task task) {
            binding.tvTaskTitle.setText(task.getTitle());
            // Populate the subtasks RecyclerView with data
            SubtaskAdapter subtaskAdapter = new SubtaskAdapter(itemView.getContext());
            subtaskAdapter.setSubtasks(task.getSubtasks());
            binding.rvTasks.setAdapter(subtaskAdapter);
        }
    }

}
