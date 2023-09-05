package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.todolistapp.databinding.ActivityMainBinding;
import com.example.todolistapp.helper.LocaleHelper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;

    private MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // Retrieve the language setting from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("Language", Context.MODE_PRIVATE);
        String language = sharedPref.getString("language", "ar"); // default English if no setting is stored
        // Set the language based on the stored setting
        LocaleHelper.setLocale(this, language);

        setContentView(binding.getRoot());

        // Initialize NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // assign the bottom navigation
        bottomNavigation = binding.bottomNavigation;

        // add the bottom navigation
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_add));
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_language));

        // set the bottom nav listener
        bottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.addTaskTitleFragment) {
                        navController.navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
                    } else if (navController.getCurrentDestination().getId() == R.id.languageFragment) {
                        navController.navigate(R.id.action_languageFragment_to_homeFragment);
                    }
                    break;
                case 2:
                    if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.homeFragment) {
                        navController.navigate(R.id.action_homeFragment_to_addTaskTitleFragment);
                    } else if (navController.getCurrentDestination().getId() == R.id.languageFragment) {
                        navController.navigate(R.id.action_languageFragment_to_addTaskTitleFragment);
                    }
                    break;
                case 3:
                    if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.homeFragment) {
                        navController.navigate(R.id.action_homeFragment_to_languageFragment);
                    } else if (navController.getCurrentDestination().getId() == R.id.addTaskTitleFragment) {
                        navController.navigate(R.id.action_addTaskTitleFragment_to_languageFragment);
                    }
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


    // Update the bottom navigation item
    private void updateBottomNavigation() {
    }

    @Override
    public void onBackPressed() {
        int currentDestinationId = Objects.requireNonNull(navController.getCurrentDestination()).getId();

        if (currentDestinationId == R.id.homeFragment) {
            super.onBackPressed();
        } else if (currentDestinationId == R.id.addTaskTitleFragment) {
            navController.navigate(R.id.action_addTaskTitleFragment_to_homeFragment);
            bottomNavigation.show(2, true);
        } else if (currentDestinationId == R.id.languageFragment) {
            navController.navigate(R.id.action_languageFragment_to_homeFragment);
            bottomNavigation.show(2, true);
        }
    }


}

