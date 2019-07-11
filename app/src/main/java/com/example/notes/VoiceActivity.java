package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Activity that is responsible for obtaining Voice Notes from the User.
 */
public class VoiceActivity extends AppCompatActivity {

    // Fragments.
    private VoiceFragment voiceFragment;

    private ReorderFragment reorderFragment;

    private SaveData saveData;

    // Components.
    private Toolbar toolbar;

    private CoordinatorLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initially assume user did not choose any image.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_screen);
        this.toolbar = (Toolbar) findViewById(R.id.voice_toolbar);
        this.toolbar.setTitle(getString(R.string.voice_title));
        setSupportActionBar(toolbar);

        // Obtain views.
        this.background = findViewById(R.id.voice_screen);

        // Obtain SaveData.
        this.saveData = (SaveData) getIntent().getSerializableExtra("saveData");

        // Create fragments.
        this.voiceFragment = new VoiceFragment();
        this.reorderFragment = new ReorderFragment();
        this.attachVoiceFragment();

        // Enable the back button.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelResults();
            }
        });
    }

    /**
     * Attach VoiceFragment to the container.
     */
    private void attachVoiceFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                this.voiceFragment).commit();
    }

    /**
     * Attach ReorderFragment to the container.
     */
    private void attachReorderFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                this.reorderFragment).commit();
    }

    private void onRecordClicked() {

    }

    private void startRecording() {

    }

    /**
     * Cancel and return to calling Activity.
     */
    private void cancelResults() {
        Intent results = new Intent();
        setResult(RESULT_CANCELED, results);
        finish();
    }

}
