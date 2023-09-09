package com.example.todolistapp.adapters;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolistapp.data.db.DatabaseAdapter;
import com.example.todolistapp.data.models.Task;
import com.example.todolistapp.databinding.ItemTaskTitleBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context;
    public final AsyncListDiffer<Task> differ;
    private static DatabaseAdapter databaseAdapter;

    public TaskAdapter(Context context) {
        this.context = context;
        databaseAdapter = new DatabaseAdapter(context);

        // Define the DiffUtil callback
        DiffUtil.ItemCallback<Task> diffCallback = new DiffUtil.ItemCallback<Task>() {
            @Override
            public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
                return oldItem.equals(newItem);
            }
        };

        // Initialize the AsyncListDiffer
        differ = new AsyncListDiffer<>(this, diffCallback);
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
        private final Calendar calendar = Calendar.getInstance();

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

                // Close the database connection after updating
                databaseAdapter.closeDatabase();
            });

            // Set the date format
            if (task.getDate() != null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE - MMM - d", Locale.getDefault());
                String formattedDate = simpleDateFormat.format(new Date(task.getDate()));
                binding.todoDate.setText(formattedDate);
            } else {
                binding.todoDate.setText(""); // Clear the date TextView if there's no date
            }

            // Open the date picker dialog and update the date
            binding.todoDate.setOnClickListener(v -> showDatePickerDialog(task.getId()));
        }


        private void showDatePickerDialog(int id) {
            Context context = binding.getRoot().getContext();
            if (context != null) {
                DatePickerDialog dialog = new DatePickerDialog(context, (datePicker, year, month, day) -> {
                    calendar.clear();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);

                    // Store the selected date in the database
                    long selectedDate = calendar.getTimeInMillis();
                    databaseAdapter.openDatabase();
                    databaseAdapter.updateDate(id, selectedDate);

                    databaseAdapter.closeDatabase();

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
                    String formattedDate = simpleDateFormat.format(calendar.getTime());
                    binding.todoDate.setText(formattedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        }
    }

    private static boolean toBoolean(int n) {
        return n != 0;
    }
}