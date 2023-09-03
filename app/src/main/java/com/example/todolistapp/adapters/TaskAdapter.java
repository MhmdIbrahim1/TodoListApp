package com.example.todolistapp.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.data.db.DatabaseAdapter;
import com.example.todolistapp.data.models.Task;
import com.example.todolistapp.databinding.ItemTaskTitleBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    public  AsyncListDiffer<Task> differ = null;

    private static DatabaseAdapter databaseAdapter;

    public TaskAdapter(Context context) {
        this.context = context;
        databaseAdapter = new DatabaseAdapter(context);
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
//    public void setTasks(List<Task> tasks) {
//        differ.submitList(tasks);
//    }

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
            binding.todoTitle.setText(task.getTitle());
            binding.todoCb.setText(task.getTask());
            binding.todoCb.setChecked(toBoolean(task.getStatus()));

            // Set the task ID as a tag to the CheckBox
            binding.todoCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Open the database connection before updating
                databaseAdapter.openDatabase();

                // Update the task status in the database based on CheckBox state
                if (isChecked) {
                    databaseAdapter.updateStatus(task.getId(), 1);
                } else {
                    databaseAdapter.updateStatus(task.getId(), 0);
                }
            });

            //set the date format
            if (task.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String formattedDate = sdf.format(new Date(task.getDate()));
                binding.todoDate.setText(formattedDate);
            } else {
                binding.todoDate.setText(""); // Clear the date TextView if there's no date
            }


            // Close the database connection after updating
            databaseAdapter.closeDatabase();
        }

    }


    private static boolean toBoolean(int n) {

        return n != 0;
    }
}
