package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.todolistapp.databinding.ActivityMainBinding;
import com.example.todolistapp.fragments.AddTaskTitleFragment;
import com.example.todolistapp.fragments.HomeFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;

    // Declare the currentNavItem variable
    private int currentNavItem = 1;

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

        int currentDestinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();

        if (currentDestinationId == R.id.homeFragment) {
            currentNavItem = 1; // Update to Home
        } else if (currentDestinationId == R.id.addTaskTitleFragment) {
            currentNavItem = 2; // Update to Add
        }

    }

    // change the status bar color
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color, getTheme()));
    }


    // Update the bottom navigation item
    private void updateBottomNavigation(int navItem) {
        bottomNavigation.show(navItem, true);
    }
    @Override
    public void onBackPressed() {
        int currentDestinationId = navController.getCurrentDestination().getId();

        if (currentDestinationId != R.id.homeFragment) {
            // If not in the HomeFragment, navigate back to it and update the bottom navigation
            navController.navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
            updateBottomNavigation(1); // Update the bottom navigation item to Home
        } else {
            super.onBackPressed();
        }
    }


}

