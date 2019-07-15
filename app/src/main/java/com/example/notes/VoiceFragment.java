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

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Fragment containing GUI to obtain Voice notes.
 */
public class VoiceFragment extends Fragment {

    // Folder to save Voice Notes in.
    final private static String DATA_SAVE_FOLDER = "data/notes/voice/";

    // nomedia file. Give it a name because for some reason it does not get created without a name.
    final private static String DATA_NOMEDIA_FILE = "data/notes/data.nomedia";

    // File type format.
    final private static String FILE_TYPE = ".m4a";

    // File name prefix.
    final private static String FILE_PREFIX = "voice_";

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
                onStartClicked();
            }
        });

        this.micStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStopClicked();
            }
        });
    }

    /**
     * Start recording button has been clicked.
     */
    private void onStartClicked() {
        this.micStartButton.setClickable(false);
        this.micStopButton.setClickable(true);
        this.micStartButton.hide();
        this.micStopButton.show();

        // Stop playing file, otherwise we won't be able to delete it.
        this.stopReplay();
        // Delete previously recorded file, so that we only keep files that user accepts.
        this.clearOldFile();
        this.startRecording();
    }

    /**
     * Stop recording button has been clicked.
     */
    private void onStopClicked() {
        // Show message when stop was unsuccessful.
        if (!this.stopRecording()) {
            Snackbar.make(this.getActivity().findViewById(R.id.voice_screen),
                    this.getResources().getString(R.string.recording_failed_msg),
                    Snackbar.LENGTH_LONG).show();
        }
        this.micStartButton.setClickable(true);
        this.micStopButton.setClickable(false);
        this.micStopButton.hide();
        this.micStartButton.show();
    }

    /**
     * Clears any previously recorded file from storage.
     */
    private void clearOldFile() {
        if (this.outputFile == null)
            return;
        if (this.outputFile.exists()) {
            this.outputFile.delete();
        }
    }

    private void stopReplay() {

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
        this.outputFile = new File(Environment.getExternalStorageDirectory(), DATA_SAVE_FOLDER
                + FILE_PREFIX + calendar.getTimeInMillis() + FILE_TYPE);

        this.recorder = new MediaRecorder();
        this.recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        this.recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // Must be called after setOutputFormat.
        this.recorder.setOutputFile(this.outputFile.getAbsolutePath());
        this.recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC_ELD);

        try {
            this.recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.recorder.start();
    }

    /**
     * Attempt to stop recording.
     * @return Returns true if successful, false if not.
     */
    private boolean stopRecording() {
        // When stop is immediately called right after start, exception will be thrown.
        try {
            this.recorder.stop();
        } catch (RuntimeException e) {
            this.outputFile.delete();
            return false;
        }
        this.recorder.release();
        this.recorder = null;
        return true;
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
