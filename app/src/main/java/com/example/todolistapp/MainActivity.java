package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.todolistapp.databinding.ActivityMainBinding;
import com.example.todolistapp.fragments.AddTaskTitleFragment;
import com.example.todolistapp.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private int currentNavItem = 1; // Default to the first item

    // Declare the bottom navigation
    private MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // assign the bottom navigation
        bottomNavigation = binding.bottomNavigation;

        // add the bottom navigation
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_add));

        // set the bottom nav listener
        bottomNavigation.setOnClickMenuListener(model -> {
            currentNavItem = model.getId(); // Update the currentNavItem
            switch (model.getId()) {
                case 1:
                    // Use custom animations when navigating to HomeFragment
                    navController.navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
                    break;
                case 2:
                    // Use custom animations when navigating to AddTaskTitleFragment
                    navController.navigate(R.id.action_homeFragment_to_addTaskTitleFragment);
                    break;
            }
            return null;
        });

        // set the home fragment as default
        bottomNavigation.show(1, true);

        // set menu item on reselected listener
        bottomNavigation.setOnReselectListener(model -> {
            // Handle reselection if needed
            return null;
        });

    }

    // change the status bar color
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color, getTheme()));
    }

    @Override
    public void onBackPressed() {
        if (currentNavItem != 1) {
            // If not in the HomeFragment, navigate back to it and update the bottom navigation
            navController.navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
            bottomNavigation.show(1, true); // Update the bottom navigation item to Home
            currentNavItem = 1;
        } else {
            super.onBackPressed(); // Default behavior for back button
        }
    }

}

