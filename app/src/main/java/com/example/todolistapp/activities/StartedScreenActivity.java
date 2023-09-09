package com.example.todolistapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.todolistapp.R;
import com.example.todolistapp.helper.LocaleHelper;

import java.util.Objects;

public class StartedScreenActivity extends AppCompatActivity {

    boolean isDarkModeOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the language setting from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("Language", Context.MODE_PRIVATE);
        String language = sharedPref.getString("language", "en"); // default English if no setting is stored
        // Set the language based on the stored setting
        LocaleHelper.setLocale(this, language);

        sharedPref = getSharedPreferences("Mode", Context.MODE_PRIVATE);
        if (sharedPref.contains("isDarkModeOn")) {
            isDarkModeOn = sharedPref.getBoolean("isDarkModeOn", false); // Update if the key exists
        }

        if (isDarkModeOn) {
            // Set the dark theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Set the light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_started_screen);
        changeStatusColor();

        // Hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

        // Always show the splash screen for 3 seconds
        new Handler().postDelayed(() -> {
            startActivity(new Intent(StartedScreenActivity.this, OnBoardingActivity.class));
            finish();
        }, 3000);
    }

    void changeStatusColor() {
        // Change status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color, null)); // Set status bar color
    }
}
