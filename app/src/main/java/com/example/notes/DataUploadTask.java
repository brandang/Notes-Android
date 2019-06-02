package com.example.notes;

import android.os.AsyncTask;

import java.util.List;

public class DataUploadTask extends AsyncTask<Void, Void, String> {

    // Strings used to communicate whether or not the task was successful.
    final private static String SUCCESS = "success";
    final private static String FAILURE = "failed";

    private DriveService service;
    private SaveData data;
    private List<UploadDoneListener> listeners;

    /**
     * Creates a new task that uploads data into the app data folder.
     * @param service The DriveService wrapper to use to upload.
     * @param saveData The data to upload.
     */
    public DataUploadTask(DriveService service, SaveData saveData, List<UploadDoneListener>
            listeners) {
        this.service = service;
        this.data = saveData;
        this.listeners = listeners;
    }

    @Override
    protected String doInBackground(Void... voids) {
        boolean success = this.service.uploadData(this.data.getSaveData());
        if (success) {
            return this.SUCCESS;
        } else {
            return this.FAILURE;
        }
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        for (UploadDoneListener listener : this.listeners) {
            if (data.equals(this.SUCCESS)) {
                listener.onUploadComplete(true);
            } else {
                listener.onUploadComplete(false);
            }
        }
    }
}
