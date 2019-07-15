package com.example.notes;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Fragment containing GUI to obtain Voice notes.
 */
public class VoiceFragment extends Fragment {

    // Folder to save Voice Notes in.
    final private static String DATA_SAVE_FOLDER = "data/notes/voice";

    // nomedia file. Give it a name because for some reason it does not get created without a name.
    final private static String DATA_NOMEDIA_FILE = "data/notes/data.nomedia";

    private EditText nameInput;

    private boolean attached = false;

    private AnimatedButton micStartButton;

    private AnimatedButton micStopButton;

    // MediaRecorder to record Audio.
    private MediaRecorder recorder;

    private File outputFile;

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
                onStartClicked();
            }
        });

        this.micStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                micStopButton.hide();
                micStartButton.show();
                onStopClicked();
            }
        });
    }

    /**
     * Start recording button has been clicked.
     */
    private void onStartClicked() {
        // Delete previously recorded file, so that we only keep files that user accepts.
//        this.clearOldFile();
//        this.startRecording();
    }

    /**
     * Stop recording button has been clicked.
     */
    private void onStopClicked() {
//        this.stopRecording();
    }

    /**
     * Clears any previously recorded file from storage.
     */
    private void clearOldFile() {
        if (this.outputFile == null)
            return;
        if (this.outputFile.exists()) {

        }
    }

    private void startRecording() {

        // Create folder.
        File mainDirectory = new File(Environment.getExternalStorageDirectory(), DATA_SAVE_FOLDER);
        File noMedia = new File(Environment.getExternalStorageDirectory(), DATA_NOMEDIA_FILE);
        if (!mainDirectory.exists()) {
            mainDirectory.mkdirs();
        }
        // A .nomedia file ensures that gallery would not include media in this folder.
        if (!noMedia.exists()) {
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Calendar calendar = Calendar.getInstance();
        this.outputFile = new File(Environment.getExternalStorageDirectory(), DATA_SAVE_FOLDER + calendar.getTimeInMillis());

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setOutputFile(this.outputFile);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
        }

        recorder.start();
    }

    private void stopRecording() {

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
