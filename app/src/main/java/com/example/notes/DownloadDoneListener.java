package com.example.notes;

/**
 * Interface that is responsible for letting user know that a download has just been
 * completed.
 */
public interface DownloadDoneListener {

    /**
     * Handle the data that was downloaded.
     * @param downloadedData The data. Stores the result code for status.
     */
    void onDownloadComplete(DownloadedData downloadedData);
}
