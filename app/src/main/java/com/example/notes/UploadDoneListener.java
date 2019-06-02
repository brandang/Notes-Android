package com.example.notes;

/**
* Interface that is responsible for letting user know that a download has just been
* completed.
*/
public interface UploadDoneListener {

    /**
     * Handle the data that was downloaded.
     * @param successful Whether or not the upload was a success.
     */
    void onUploadComplete(boolean successful);
}
