package com.example.notes;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Fragment that is responsible for asking and obtaining Preferences from user.
 */
public class SettingsFragment extends PreferenceFragment {

    private ListPreference textSizePreference;
    private Settings settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.settings = new Settings(getString(R.string.text_size_small));
        this.setupPreferences();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.setSettings(this.settings);
    }

    /**
     * Handles whatever the user changed in preferences.
     */
    private void setupPreferences() {
        this.textSizePreference = (ListPreference)
                findPreference(getString(R.string.text_size_pref));

        Preference.OnPreferenceChangeListener textSizeListener = new
                Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object size) {
                String textSize = (String) size;
                SettingsFragment.this.settings.setTextSize(textSize);
                return true;
            }
        };

        this.textSizePreference.setOnPreferenceChangeListener(textSizeListener);
    }

    /**
     * Sets the initial Settings if the Fragment has been attached. If it has not been attached, do
     * nothing.
     * @param settings The Settings.
     */
    public void setSettings(Settings settings) {
        if (settings == null) {
            this.textSizePreference.setValueIndex(0);
            return;
        }
        if (!isAdded()) {
            this.settings = settings;
            return;
        }
        this.settings = settings;
        int index = 0;
        if (settings.getTextSize().equals(getString(R.string.text_size_small))) {
            index = 1;
        } else if (settings.getTextSize().equals(getString(R.string.text_size_medium))) {
            index = 2;
        } else if (settings.getTextSize().equals(getString(R.string.text_size_large))) {
            index = 3;
        }
        this.textSizePreference.setValueIndex(index);
    }

    /**
     * Return settings that the user modified.
     * @return The Settings.
     */
    public Settings getSettings() {
        return this.settings;
    }
}
