package com.example.notes;

import android.os.AsyncTask;

public class PrintFilesTask extends AsyncTask<Void, Void, String> {

    private DriveService service;

    /**
     * Creates a new task that prints out the names of all the files in the app data folder.
     * @param service The DriveService to use to search.
     */
    public PrintFilesTask(DriveService service) {
        this.service = service;
    }


    @Override
    protected String doInBackground(Void... voids) {
        this.service.printFileNames();
        return null;
    }
}
