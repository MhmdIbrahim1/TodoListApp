package com.example.todolistapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolistapp.data.models.Subtask;
import com.example.todolistapp.databinding.ItemTasksBinding;

import java.util.List;

public class SubtaskAdapter extends RecyclerView.Adapter<SubtaskAdapter.SubtaskViewHolder> {

    private final Context context;
    private final AsyncListDiffer<Subtask> differ;

    public SubtaskAdapter(Context context) {
        this.context = context;

        // Define the DiffUtil callback
        DiffUtil.ItemCallback<Subtask> diffCallback = new DiffUtil.ItemCallback<Subtask>() {
            @Override
            public boolean areItemsTheSame(@NonNull Subtask oldItem, @NonNull Subtask newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Subtask oldItem, @NonNull Subtask newItem) {
                return oldItem.isDone() == newItem.isDone();
            }
        };

        // Initialize the AsyncListDiffer
        differ = new AsyncListDiffer<>(this, diffCallback);
    }

    // Setter for the list of subtasks
    public void setSubtasks(List<Subtask> subtasks) {
        differ.submitList(subtasks);
    }

    @NonNull
    @Override
    public SubtaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTasksBinding binding = ItemTasksBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SubtaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubtaskViewHolder holder, int position) {
        Subtask subtask = differ.getCurrentList().get(position);
        holder.bind(subtask);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public static class SubtaskViewHolder extends RecyclerView.ViewHolder {
        private final ItemTasksBinding binding;

        public SubtaskViewHolder(ItemTasksBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Subtask subtask) {
            binding.todoCb.setText(subtask.getTitle());
            binding.todoCb.setChecked(subtask.isDone());
            // Handle checkbox changes here
        }
    }
}
