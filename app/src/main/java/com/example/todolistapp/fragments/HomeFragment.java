package com.example.todolistapp.fragments;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolistapp.R;
import com.example.todolistapp.data.db.DatabaseAdapter;
import com.example.todolistapp.data.models.Subtask;
import com.example.todolistapp.databinding.FragmentHomeBinding;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.adapters.TaskAdapter;
import com.example.todolistapp.data.models.Task;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private TaskAdapter taskAdapter;

    private DatabaseAdapter databaseAdapter;

    private List<Task> taskList = new ArrayList<>();

    private NavController navController;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        databaseAdapter = new DatabaseAdapter(requireContext()); // Initialize your database adapter
        // Initialize the database handler
        databaseAdapter.openDatabase();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setUpRecyclerView();

        // Set up item touch helper for swipe actions
        setUpItemTouchHelper();

        // initialize the NavController
        navController = Navigation.findNavController(view);
    }

    private void setUpItemTouchHelper() {
        // Create an ItemTouchHelper instance
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            // Override onMove() method to handle drag and drop events
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Return false to indicate that no items were moved
            }

            // Override onSwiped() method to handle swipe events
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    // Display a confirmation dialog for task deletion
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle(R.string.delete_task_dialog_title);
                    builder.setMessage(R.string.delete_task_dialog_message);
                    builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
                        // Get the position of the item swiped
                        Task task = taskList.get(position);
                        // Delete the task
                        databaseAdapter.deleteTask(task.getId());
                        taskList.remove(task);

                        // Notify the adapter of the change in the data set
                        taskAdapter.notifyItemRemoved(position);
                    });
                    builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                        // Notify the adapter of the change in the data set
                        taskAdapter.notifyItemChanged(position);
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else if (direction == ItemTouchHelper.RIGHT) {
                    // Edit the task
                    Task task = taskList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", task.getId());
                    bundle.putString("title", task.getTitle());

                    navController.navigate(R.id.action_homeFragment_to_addTaskTitleFragment, bundle);
                }
            }

            // Override onChildDraw() method to customize swipe animation
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                Drawable icon;
                ColorDrawable background;
                View itemView = viewHolder.itemView;
                int backgroundCornerOffset = 20;

                // Set the icon and background based on the swipe direction
                if (dX > 0) { // Swiping to the right
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_edit);
                    background = new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.status_bar_color));
                } else {
                    icon = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_delete);
                    background = new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.g_red ));
                }

                assert icon != null;
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                // Set the icon and background bounds based on the swipe direction
                if (dX > 0) { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                            itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                // If the view is not actively being swiped, reset its position
                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
                    background.setBounds(0, 0, 0, 0);
                    icon.setBounds(0, 0, 0, 0);
                }

                // Draw the background and icon
                background.draw(c);
                icon.draw(c);
            }
        };

        // Attach the custom ItemTouchHelper to the RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);
    }


    private void setUpRecyclerView() {
        taskAdapter = new TaskAdapter(requireContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(taskAdapter);

        // Get all tasks from the database
        taskList = databaseAdapter.getAllTasks();

        // Set the list of tasks on the adapter
        taskAdapter.setTasks(taskList);

        //get the subtasks for each task
        for (Task task : taskList) {
            List<Subtask> subtasks = databaseAdapter.getAllSubtasks();
            task.setSubtasks(subtasks);
        }
    }


}
