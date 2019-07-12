package com.example.notes;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

/**
 * Fragment containing GUI to obtain Voice notes.
 */
public class VoiceFragment extends Fragment {

    private EditText nameInput;

    private boolean attached = false;

    private AnimatedButton micStartButton;

    private AnimatedButton micStopButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment.
        View layout = inflater.inflate(R.layout.voice_content, container,false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Load up child views.
        this.nameInput = view.findViewById(R.id.name);
        this.micStartButton = view.findViewById(R.id.mic_on_button);
        this.micStopButton = view.findViewById(R.id.mic_off_button);
        this.setupButtons();
    }

    /**
     * Sets up and Binds the buttons.
     */
    private void setupButtons() {
        // Initially show this button.
        this.micStartButton.show();

        // Set up listeners.
        this.micStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                micStartButton.hide();
                micStopButton.show();
            }
        });

        this.micStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                micStopButton.hide();
                micStartButton.show();
            }
        });
    }

    /**
     * 
     */
    private void onStartClicked() {

    }

    private void onStopClicked() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.attached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.attached = false;
    }
}
