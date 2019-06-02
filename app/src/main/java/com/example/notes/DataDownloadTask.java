package com.example.notes;

import android.os.AsyncTask;

import java.util.List;

/**
 * Task that is responsible for downloading the app data from Google Drive.
 */
public class DataDownloadTask extends AsyncTask<Void, Void, String> {

    private DriveService service;
    private List<DownloadDoneListener> listeners;

    /**
     * Initializes a new task to download data, given the parameters.
     * @param service The DriveService wrapper to use to download.
     * @param listeners The listeners to notify when download is complete.
     */
    public DataDownloadTask(DriveService service, List<DownloadDoneListener> listeners) {
        this.service = service;
        this.listeners = listeners;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return this.service.downloadData();
    }

    @Override
    protected void onPostExecute(String data) {
        super.onPostExecute(data);
        for (DownloadDoneListener listener : this.listeners) {
            if (data == null) {
                listener.onDownloadComplete(null);
            } else {
                listener.onDownloadComplete(new SaveData(data));
            }
        }
    }
}

