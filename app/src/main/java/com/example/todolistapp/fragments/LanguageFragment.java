package com.example.todolistapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.todolistapp.activities.MainActivity;
import com.example.todolistapp.R;
import com.example.todolistapp.databinding.FragmentLanguageBinding;
import java.util.Locale;
import java.util.Objects;

public class LanguageFragment extends Fragment {
    private FragmentLanguageBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLanguageBinding.inflate(inflater, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("Mode", Context.MODE_PRIVATE);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String currentLanguage = Locale.getDefault().getLanguage();

        binding.appSwitchTheme.setChecked(sharedPreferences.getBoolean("isDarkModeOn", false));

        switch (currentLanguage) {
            case "en":
                changeToEnglish();
                break;
            case "ar":
                changeToArabic();
                break;

        }

        binding.linearEnglish.setOnClickListener(v -> changeLanguage("en"));

        binding.linearArabic.setOnClickListener(v -> changeLanguage("ar"));

        binding.appSwitchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isDarkModeOn", isChecked);
            editor.apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });


    }

    private void changeToArabic() {
        binding.imgArabic.setVisibility(View.VISIBLE);
        binding.imgEnglish.setVisibility(View.INVISIBLE);
    }

    private void changeToEnglish() {
        binding.imgArabic.setVisibility(View.INVISIBLE);
        binding.imgEnglish.setVisibility(View.VISIBLE);
    }



    private void changeLanguage(String code) {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        SharedPreferences sharedPref = requireActivity().getSharedPreferences("Language", Context.MODE_PRIVATE);
        sharedPref.edit().putString("language", "en").apply();

        if (code.equals("en")) {
            setLocale(requireActivity(), "en");
            changeToEnglish();
            sharedPref.edit().putString("language", "en").apply();
            startActivity(intent);
        } else if (code.equals("ar")) {
            setLocale(requireActivity(), "ar");
            changeToArabic();
            sharedPref.edit().putString("language", "ar").apply();
            startActivity(intent);
        }
    }

    private void setLocale(Activity activity, String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Resources resources = activity.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }


    @Override
    public void onResume() {
        super.onResume();
        // Set the title of the toolbar
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle(R.string.language);
    }
}