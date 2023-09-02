package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import java.util.Objects;

public class StartedScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_started_screen);
        new Handler().postDelayed(() -> {
            startActivity(new Intent(StartedScreenActivity.this, OnBoardingActivity.class));
            finish();
        }, 3000);
        changeStatusColor();

        //hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    void changeStatusColor() {
        // Change status bar color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color, null)); // Change to your desired color resource
    }


}