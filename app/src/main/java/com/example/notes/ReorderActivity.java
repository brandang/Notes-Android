package com.example.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

/**
 * Activity that is responsible for obtaining photo from user. If the user returned without choosing
 * a photo, the returned result will be null.
 */
public class ReorderActivity extends AppCompatActivity {

    private Toolbar toolbar;

    // Custom action buttons at the bottom.
    private AnimatedActionButton accept, decline;

    // RecyclerView and adapter.
    private RecyclerView recyclerView;

    private ReorderAdapter adapter;

    // Background/root view.
    private CoordinatorLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.reorder_screen);
        this.toolbar = findViewById(R.id.reorder_toolbar);
        this.toolbar.setTitle(getString(R.string.reorder_title));
        setSupportActionBar(toolbar);

        this.background = findViewById(R.id.reorder_screen);

        // Set up the recyclerView and its adapter.
        this.recyclerView = findViewById(R.id.reorder_recycler);
        this.adapter = new ReorderAdapter(this, new ArrayList<ItemData>(0),
                this.recyclerView);
        SaveData saveData = (SaveData) getIntent().getSerializableExtra("saveData");
        if (saveData != null)
            this.adapter.setDisplayData(saveData);
        // Assign callback so that we can handle dragging and dropping.
        ItemTouchHelper.Callback callback = new ItemMoveCallback(this.adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(this.recyclerView);
        this.recyclerView.setAdapter(this.adapter);

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
        bundle.putSerializable("saveData", this.adapter.getSaveData());
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
}
