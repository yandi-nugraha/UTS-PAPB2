package com.example.uts_papb2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // Button untuk mengganti bahasa
        rootView.findViewById(R.id.change_language).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(languageIntent);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }
}