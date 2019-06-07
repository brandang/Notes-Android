package com.example.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

/**
 * Activity for the purposes of obtaining Settings from the user.
 */
public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SettingsFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_screen);
        this.toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        this.toolbar.setTitle(getString(R.string.settings_title));
        setSupportActionBar(toolbar);

        // Add in Preferences.
        this.fragment = new SettingsFragment();
        getFragmentManager().beginTransaction().replace(R.id.settings_fragment_container,
                this.fragment).commit();

        Settings currentSettings = (Settings) getIntent().getSerializableExtra("settings");
        this.fragment.setSettings(currentSettings);

        // Go back to previous screen.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings settings = SettingsActivity.this.fragment.getSettings();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("settings", settings);
                setResult(Activity.RESULT_OK, resultIntent);
                onBackPressed();
                finish();
            }
        });
    }
}
