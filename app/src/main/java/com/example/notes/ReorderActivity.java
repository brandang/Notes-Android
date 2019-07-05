package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

/**
 * Activity that is responsible for obtaining photo from user. If the user returned without choosing
 * a photo, the returned result will be null.
 */
public class ReorderActivity extends AppCompatActivity implements SnackbarDisplayer {

    private Toolbar toolbar;

    // Custom action buttons at the bottom.
    private AnimatedActionButton accept, decline;

    // RecyclerView and adapter.
    private RecyclerView recyclerView;

    private ReorderAdapter adapter;

    // Background/root view.
    private CoordinatorLayout background;

    private ReorderFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reorder_screen);
        this.toolbar = findViewById(R.id.reorder_toolbar);
        this.toolbar.setTitle(getString(R.string.reorder_title));
        setSupportActionBar(toolbar);

        this.background = findViewById(R.id.reorder_screen);

        // Set up fragment.
        this.fragment = new ReorderFragment();
        SaveData saveData = (SaveData) getIntent().getSerializableExtra("saveData");
        getSupportFragmentManager().beginTransaction().replace(R.id.reorder_fragment_container,
                this.fragment).commit();
        this.fragment.setData(saveData, this);

        // Enable the back button in the toolbar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReorderActivity.this.cancelChanges();
            }
        });

        this.setupButtons();
        this.showPrompt();

        // Prevent keyboard from popping up in this Activity.
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Binds the floating action buttons and sets their behaviours.
     */
    private void setupButtons() {

        this.accept = findViewById(R.id.accept_button);
        this.decline = findViewById(R.id.decline_button);

        this.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReorderActivity.this.onAcceptClick();
            }
        });
        this.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReorderActivity.this.onDeclineClick();
            }
        });

        this.accept.show();
        this.decline.show();
    }

    /**
     * Displays the message prompt.
     */
    private void showPrompt() {
        Snackbar message = Snackbar.make(ReorderActivity.this.background,
                getString(R.string.reorder_prompt),
                Snackbar.LENGTH_LONG);
        message.show();
    }

    /**
     * Decline Floating action button was just clicked. Handles it.
     */
    private void onDeclineClick() {
        this.cancelChanges();
    }

    /**
     * Accept floating action button was just clicked. Handles it.
     */
    private void onAcceptClick() {
        this.acceptChanges();
    }

    /**
     * Return the rearranged notes and return to parent activity.
     */
    private void acceptChanges() {
        Intent results = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("saveData", this.fragment.getData());
        results.putExtras(bundle);
        setResult(RESULT_OK, results);
        finish();
    }

    /**
     * Return to parent activity without making any changes.
     */
    private void cancelChanges() {
        Intent results = new Intent();
        setResult(RESULT_CANCELED, results);
        finish();
    }

    @Override
    public void showSnackbar(String msg, String actionMsg, int length, View.OnClickListener listener) {
        Snackbar.make(this.background, msg, length).setAction(actionMsg, listener).show();
    }
}
