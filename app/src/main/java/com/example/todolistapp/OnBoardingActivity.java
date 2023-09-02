package com.example.todolistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnBoardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.backbtn.setOnClickListener(v -> {
            if (getitem(0) > 0) {
                mSLideViewPager.setCurrentItem(getitem(-1), true);
            }
        });

        binding.nextbtn.setOnClickListener(v -> {
            if (getitem(0) < 3)
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
        dots = new TextView[4];
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

}