package com.example.todolistapp.fragments;

import static androidx.navigation.Navigation.findNavController;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.todolistapp.R;
import com.example.todolistapp.data.db.DatabaseAdapter;
import com.example.todolistapp.data.models.Task;
import com.example.todolistapp.databinding.FragmentAddTaskTitleBinding;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddTaskTitleFragment extends Fragment {
    private FragmentAddTaskTitleBinding binding;
    private DatabaseAdapter databaseAdapter;


    // Flags to track whether the activity is in update mode and task ID to update
    private boolean isUpdate = false;

    private int taskIdToUpdate = -1;

    private Long selectedDate = null; // Store the selected date

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
                long date = bundle.getLong("date");
                binding.titleEt.setText(title);
                binding.etTask.setText(task);


                // Store the selected date
                selectedDate = date;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE - MMM - d", Locale.getDefault());
                String formattedDate = simpleDateFormat.format(new Date(date));
                binding.dateBtn.setText(formattedDate);

            }

        }

        binding.addBtn.setOnClickListener(v -> {
            String title = binding.titleEt.getText().toString().trim();
            String task = binding.etTask.getText().toString().trim();

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(requireContext(), R.string.please_enter_a_title, Toast.LENGTH_SHORT).show();
            } else {
                if (isUpdate) {
                    if (taskIdToUpdate != -1) {
                        databaseAdapter.updateTask(taskIdToUpdate, title, task, selectedDate);
                        Toast.makeText(requireContext(), R.string.task_updated, Toast.LENGTH_SHORT).show();
                        findNavController(v).navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
                        // update the bottom navigation item to home
                        MeowBottomNavigation bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
                        bottomNavigation.show(2, true);
                    }
                } else {
                    if (selectedDate != null) {
                        // Create a new Task object with the selected date
                        Task newTask = new Task(title, task, 0, selectedDate);
                        databaseAdapter.insertTask(newTask);
                        Toast.makeText(requireContext(), R.string.task_added, Toast.LENGTH_SHORT).show();
                        findNavController(v).navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
                        // update the bottom navigation item to home
                        MeowBottomNavigation bottomNavigation = requireActivity().findViewById(R.id.bottom_navigation);
                        bottomNavigation.show(2, true);
                    } else {
                        Toast.makeText(requireContext(), R.string.please_select_the_date, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.dateBtn.setOnClickListener(v -> showDatePickerDialog());

    }


    private final Calendar calendar = Calendar.getInstance();

    private void showDatePickerDialog() {
        Context context = getContext();
        if (context != null) {
            DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, MMM d", Locale.getDefault());
                    String formattedDate = simpleDateFormat.format(new Date(year - 1900, month, day));
                    binding.dateBtn.setText(formattedDate);

                    calendar.set(year, month, day);

                    // To ignore time
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    selectedDate = calendar.getTimeInMillis(); // Store the selected date
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }
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
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.update_task);
            binding.addBtn.setText(R.string.update_task);
        } else {
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.addtask);
            binding.addBtn.setText(R.string.add_task);
        }
    }

}
