package com.example.notes;

/**
 * Interface that is responsible for letting user know that a download has just been
 * completed.
 */
public interface DownloadDoneListener {

    /**
     * Handle the data that was downloaded.
     * @param saveData The data. If unable to load data, it is null.
     */
    void onDownloadComplete(SaveData saveData);
}
