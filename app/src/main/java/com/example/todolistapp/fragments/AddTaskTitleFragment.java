package com.example.todolistapp.fragments;


import static android.app.Activity.RESULT_OK;

import static androidx.navigation.Navigation.findNavController;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.todolistapp.R;
import com.example.todolistapp.adapters.TaskAdapter;
import com.example.todolistapp.data.db.DatabaseAdapter;
import com.example.todolistapp.data.models.Task;
import com.example.todolistapp.databinding.FragmentAddTaskTitleBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class AddTaskTitleFragment extends Fragment {
    private FragmentAddTaskTitleBinding binding;
    private DatabaseAdapter databaseAdapter;


    // Flags to track whether the activity is in update mode and task ID to update
    private boolean isUpdate = false;

    private int taskIdToUpdate = -1;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTaskTitleBinding.inflate(inflater, container, false);
        databaseAdapter = new DatabaseAdapter(requireContext()); // Initialize your database adapter
        databaseAdapter.openDatabase(); // Open the database for writing
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            taskIdToUpdate = bundle.getInt("id", -1);
            isUpdate = (taskIdToUpdate != -1);

            if (isUpdate) {
                // If in update mode, set the task text and update button state
                String title = bundle.getString("title");
                String task = bundle.getString("task");
                binding.titleEt.setText(title);
                binding.etTask.setText(task);
                binding.addBtn.setText("Update");

            }

        }


        binding.addBtn.setOnClickListener(v -> {
            String title = binding.titleEt.getText().toString().trim();
            String task = binding.etTask.getText().toString().trim();

            if(TextUtils.isEmpty(title)) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            }else {
                if(isUpdate){
                    if (taskIdToUpdate != -1) {
                        databaseAdapter.updateTask(taskIdToUpdate, title,task);
                        Toast.makeText(requireContext(), "Task updated", Toast.LENGTH_SHORT).show();
                        findNavController(v).navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
                        // update the bottom navigation item to home
                        MeowBottomNavigation bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
                        bottomNavigation.show(1,true);
                    }
                }else {
                    databaseAdapter.insertTask(new Task(title,task,0));
                    Toast.makeText(requireContext(), "Task added", Toast.LENGTH_SHORT).show();
                    findNavController(v).navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
                    // update the bottom navigation item to home
                    MeowBottomNavigation bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
                    bottomNavigation.show(1,true);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        databaseAdapter.closeDatabase(); // Close the database
    }

    @Override
    public void onResume() {
        super.onResume();
        // if in update mode, set the title of the action bar to "Update Task" else "Add Task"
        if (isUpdate) {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Update Task");
        } else {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Add Task");
        }
    }
}
