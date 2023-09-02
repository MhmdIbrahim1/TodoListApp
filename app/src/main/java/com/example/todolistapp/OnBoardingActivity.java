package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.todolistapp.adapters.ViewPagerAdapter;
import com.example.todolistapp.databinding.ActivityOnBoardingBinding;

import java.util.Objects;

public class OnBoardingActivity extends AppCompatActivity {
    private ActivityOnBoardingBinding binding;

    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager mSLideViewPager;
    LinearLayout mDotLayout;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String ONBOARDING_COMPLETE_KEY = "onboardingComplete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Check if onboarding has been completed before
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (settings.getBoolean(ONBOARDING_COMPLETE_KEY, false)) {
            // Onboarding has been completed before, skip it
            Intent i = new Intent(OnBoardingActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        } else {
            // Onboarding has not been completed before, show it
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(ONBOARDING_COMPLETE_KEY, true);
            editor.apply();
        }

        binding.backbtn.setOnClickListener(v -> {
            if (getitem(0) > 0) {
                mSLideViewPager.setCurrentItem(getitem(-1), true);
            }
        });

        binding.nextbtn.setOnClickListener(v -> {
            if (getitem(0) < 2)
                mSLideViewPager.setCurrentItem(getitem(1),true);
            else {

                Intent i = new Intent(OnBoardingActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        binding.skipButton.setOnClickListener(v -> {
            Intent i = new Intent(OnBoardingActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        });

        mSLideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);


        viewPagerAdapter = new ViewPagerAdapter(this);

        mSLideViewPager.setAdapter(viewPagerAdapter);

        setUpIndicator(0);
        mSLideViewPager.addOnPageChangeListener(viewListener);


        //hide action bar
        Objects.requireNonNull(getSupportActionBar()).hide();

    }

    private void setUpIndicator(int position) {
        dots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {

            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
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

    private int getitem(int i) {

        return mSLideViewPager.getCurrentItem() + i;
    }

    // change status bar color

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setStatusBarColor(getResources().getColor(R.color.status_bar_color,getApplicationContext().getTheme()));
    }
}