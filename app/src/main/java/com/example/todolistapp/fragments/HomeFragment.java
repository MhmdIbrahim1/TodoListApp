package com.example.todolistapp.fragments;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView; // Import the androidx SearchView class

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.todolistapp.R;
import com.example.todolistapp.data.db.DatabaseAdapter;
import com.example.todolistapp.databinding.FragmentHomeBinding;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.adapters.TaskAdapter;
import com.example.todolistapp.data.models.Task;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        setHasOptionsMenu(true);

        // Set up item touch helper for swipe actions
        setUpItemTouchHelperForTaskRv();

        // initialize the NavController
        navController = Navigation.findNavController(view);

    }

    private void setUpRecyclerView() {
        taskAdapter = new TaskAdapter(requireContext());
        // Set the task adapter on the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(taskAdapter);

        // Get all tasks from the database
        taskList = databaseAdapter.getAllTasks();

        // Set the list of tasks on the adapter
        taskAdapter.differ.submitList(taskList);

        // Add layout animation to the RecyclerView
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.recyclerrview_animation);
        binding.recyclerView.setLayoutAnimation(new LayoutAnimationController(animation));

        //set the list sort by date by default
        sortByDate();
    }

    //inflate the menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        //get the menu item search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //get the search view
        SearchView searchView = (SearchView) searchItem.getActionView();
        //set the query hint
        searchView.setQueryHint(getResources().getString(R.string.search));
        //set the search icon

        // Set a query listener for the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Called when the user submits the query
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search the database for tasks that match the query
                taskList = databaseAdapter.searchTasks(query);
                // Set the list of tasks on the adapter
                taskAdapter.differ.submitList(taskList);
                return true;
            }

            // Called when the query text is changed by the user
            @Override
            public boolean onQueryTextChange(String newText) {
                // Search the database for tasks that match the query
                taskList = databaseAdapter.searchTasks(newText);
                // Set the list of tasks on the adapter
                taskAdapter.differ.submitList(taskList);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.delete) {
            // Handle delete action
            if (taskList.isEmpty()) {
                Toast.makeText(requireContext(), getResources().getString(R.string.nothing_to_delete), Toast.LENGTH_SHORT).show();
            } else {
                showDeleteConfirmationDialog();
            }
            return true;
        } else if (id == R.id.sort_by_date) {
            // Handle sorting by date
            sortByDate();
            //notify the adapter
            return true;
        } else if (id == R.id.sort_by_title) {
            // Handle sorting by title
            sortByTitle();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void sortByTitle() {
        // Sort taskList by title
        taskList.sort(Comparator.comparing(Task::getTitle));
        taskAdapter.differ.submitList(taskList);
        //notify the adapter
        taskAdapter.notifyDataSetChanged();

        //animate the recyclerview
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.recyclerrview_animation);
        binding.recyclerView.setLayoutAnimation(new LayoutAnimationController(animation));
    }


    private void sortByDate() {
        // Sort taskList by date
        taskList.sort(Comparator.comparingLong(Task::getDate));
        taskAdapter.differ.submitList(taskList);
        //notify the adapter
        taskAdapter.notifyDataSetChanged();

        //animate the recyclerview
        Animation animation = AnimationUtils.loadAnimation(requireContext(), R.anim.recyclerrview_animation);
        binding.recyclerView.setLayoutAnimation(new LayoutAnimationController(animation));
    }


    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getResources().getString(R.string.deleteall));
        builder.setMessage(getResources().getString(R.string.deleteMessage));
        builder.setPositiveButton(getResources().getString(R.string.confirm), (dialog, which) -> {
            // Delete all tasks
            databaseAdapter.deleteAllTasks();
            taskList.clear();
            taskAdapter.differ.submitList(taskList);
            taskAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();

    }


    private void setUpItemTouchHelperForTaskRv() {
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
                } else if (direction == ItemTouchHelper.RIGHT) {
                    // Edit the task
                    Task task = taskList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", task.getId());
                    bundle.putString("title", task.getTitle());
                    bundle.putString("task", task.getTask());
                    bundle.putLong("date", task.getDate());
                    navController.navigate(R.id.action_homeFragment_to_addTaskTitleFragment, bundle);
                    // update the bottom navigation item to home
                    MeowBottomNavigation bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
                    bottomNavigation.show(1,true);
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
                    background = new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.g_red));
                }

                assert icon != null;

                // Calculate the size of the icon
                int iconSize = (int) (itemView.getHeight() * 0.3);

                int iconMargin = (itemView.getHeight() - iconSize) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - iconSize) / 2;
                int iconBottom = iconTop + iconSize;

                // Set the icon and background bounds based on the swipe direction
                if (dX > 0) { // Swiping to the right
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getLeft() + iconMargin + iconSize;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

                    background.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
                } else if (dX < 0) { // Swiping to the left
                    int iconLeft = itemView.getRight() - iconMargin - iconSize;
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


    @Override
    public void onResume() {
        super.onResume();
        // change the action bar text and color
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.todo_list);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.status_bar_color, null))); // Use getResources() to get the color
    }
}
