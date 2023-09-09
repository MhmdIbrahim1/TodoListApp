package com.example.todolistapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.text.HtmlCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todolistapp.R;
import com.example.todolistapp.adapters.ViewPagerAdapter;
import com.example.todolistapp.databinding.ActivityOnBoardingBinding;
import com.example.todolistapp.helper.LocaleHelper;

import java.util.Objects;

public class OnBoardingActivity extends AppCompatActivity {
    private ActivityOnBoardingBinding binding;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager mSLideViewPager;
    LinearLayout mDotLayout;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String ONBOARDING_COMPLETE_KEY = "onboardingComplete";

    private boolean isDarkModeOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());

        // Retrieve the language setting from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("Language", Context.MODE_PRIVATE);
        String language = sharedPref.getString("language", "en"); // default English if no setting is stored


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

        // Set the language based on the stored setting
        LocaleHelper.setLocale(this, language);

        setContentView(binding.getRoot());

        // Check if onboarding has been completed before
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.getBoolean(ONBOARDING_COMPLETE_KEY, false)) {
            // Onboarding has been completed before, navigate to the main activity
            navigateToMainActivity();
        }

        binding.backbtn.setOnClickListener(v -> {
            if (getItem(0) > 0) {
                mSLideViewPager.setCurrentItem(getItem(-1), true);
            }
        });

        binding.nextbtn.setOnClickListener(v -> {
            if (getItem(0) < 2)
                mSLideViewPager.setCurrentItem(getItem(1), true);
            else {
                // User has finished onboarding, navigate to the main activity
                navigateToMainActivity();
            }
        });

        binding.skipButton.setOnClickListener(v -> {
            // User chose to skip onboarding, navigate to the main activity
            navigateToMainActivity();
        });

        mSLideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapter(this);
        mSLideViewPager.setAdapter(viewPagerAdapter);

        setUpIndicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);

        // Hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void setUpIndicator(int position) {
        dots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(HtmlCompat.fromHtml("&#8226", HtmlCompat.FROM_HTML_MODE_LEGACY));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.inactive, getApplicationContext().getTheme()));
            mDotLayout.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.active, getApplicationContext().getTheme()));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);

            if (position > 0) {
                binding.backbtn.setVisibility(View.VISIBLE);
            } else {
                binding.backbtn.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return mSLideViewPager.getCurrentItem() + i;
    }

    // Change status bar color
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color, getApplicationContext().getTheme()));
    }

    private void navigateToMainActivity() {
        // Mark onboarding as complete and navigate to the main activity
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(ONBOARDING_COMPLETE_KEY, true);
        editor.apply();

        Intent i = new Intent(OnBoardingActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
