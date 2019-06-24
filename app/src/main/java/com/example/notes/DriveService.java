package com.example.notes;

import android.content.Context;
import android.util.Log;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Class that is responsible for storing and loading data from Google Drive using REST API.
 */
public class DriveService {

    final private static String DRIVE_SERVICE_TAG = "Drive Service:";

    // AppDataFolder Scope.
    final private static String APPDATAFOLDER = "appDataFolder";

    // Name for save data file.
    final private static String SAVE_FILE_NAME = "save_data";

    // Type for save data file.
    final private static String SAVE_FILE_TYPE = "text/txt";

    // Drive to use to perform operations.
    private Drive service;

    private boolean isBusy;


    /**
     * Initializes a new Drive Service wrapper given a Drive object obtained from credentials.
     * @param service The Drive service.
     */
    public DriveService(Drive service) {
        this.service = service;
        this.setBusy(false);
    }

    /**
     * Returns the save file. Returns null if the file does not exist. Throws exception if there
     * was an error accessing the file.
     * @return The file.
     */
    private File getSaveFile() {
        // File searcher.
        Drive.Files.List fileSearch;
        try {
            fileSearch = this.service.files().list();
            // Where to search.
            fileSearch.setSpaces(APPDATAFOLDER);
            // Start search.
            FileList files = fileSearch.execute();
            // Look for the right file and return it.
            for (File file : files.getFiles()) {
                if (file.getName().equals(SAVE_FILE_NAME)) {
                    return file;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Returns the id of the save data file. Returns null if the file does not exist/inaccessible.
     * @return The id.
     */
    private String getSaveFileID() {
        File saveFile = this.getSaveFile();
        if (saveFile == null) {
            return null;
        } else {
            return saveFile.getId();
        }
    }

    /**
     * Determines whether or not the save file exists on the user`s Drive.
     * @return True for yes, false for no.
     */
    private boolean saveFileExists() {
        return this.getSaveFile() != null;
    }

    /**
     * Downloads and returns the app data.
     * @return The app data. Returns empty String if it does not exist.
     */
    public SaveData downloadData() {

        this.setBusy(true);

        // Default value to return.
        String downloadUrl = this.getSaveFileID();

        // Make sure downloadURL is valid.
        if (downloadUrl == null) {
            this.setBusy(false);
            return null;
        }

        try {

            // Stream the file contents to a String.
            InputStream input = this.service.files().get(downloadUrl).executeMediaAsInputStream();
            ObjectInputStream object = new ObjectInputStream(input);
            SaveData data = (SaveData) object.readObject();
            object.close();
            setBusy(false);
            input.close();
            setBusy(false);
            return data;
        } catch (Exception e) {
            setBusy(false);
            return null;
        }
    }

    /**
     * Uploads data to Google.
     * @param data The data to upload.
     * @return Whether or not the upload was successful.
     */
    public boolean uploadData(SaveData data) {
        setBusy(true);
        try {
            if (this.saveFileExists()) {
                this.updateData(data);
            } else {
                this.createAndUpload(data);
            }
            setBusy(false);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            setBusy(false);
            return false;
        }
    }

    /**
     * Uploads the app data. Overwrites the current data file, or creates a new one if it does not exist. Throws
     * exception if credentials are invalid.
     * @param data The data to upload.
     * @return The Results: task was met with success, canceled, or failed.
     */
   /* public Results uploadData(String data) {
        if (this.busy) {
            return Results.CANCELED;
        } else {
            this.busy = true;
        }
        try {
            if (this.saveFileExists()) {
                this.updateData(data);
            } else {
                this.createAndUpload(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Results.FAILED;
        }
        this.busy = false;
        return Results.SUCCESS;
    }*/

    /**
     * Update the data file. Throws IO exception whenever
     * @param data The data.
     */
    private void updateData(SaveData data) throws IOException {

        // Create a new File.
        File metadata = new File().setName(SAVE_FILE_NAME);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        out = new ObjectOutputStream(bos);
        out.writeObject(data);
        out.flush();
        byte[] yourBytes = bos.toByteArray();

        // Convert content to an AbstractInputStreamContent instance.
        // This means we don't actually have to create a new file.
        ByteArrayContent contentStream = new ByteArrayContent(SAVE_FILE_TYPE, yourBytes);

        // Update the metadata and contents.
        String fileID = this.getSaveFileID();

        // Update the metadata and contents.
        this.service.files().update(fileID, metadata, contentStream).execute();
    }

    /**
     * Creates a new data file and uploads it.
     * @param data The data.
     * @return True if creation was successful, False if it wasn't.
     */
    private boolean createAndUpload(SaveData data) throws IOException {

        File fileMetadata = new File()
                .setSpaces(Collections.singletonList(APPDATAFOLDER))
                .setMimeType(SAVE_FILE_TYPE)
                .setName(SAVE_FILE_NAME);

        File file = this.service.files().create(fileMetadata).execute();
        // Let user know of failure.
        if (file == null) {
            return false;
        }
        this.updateData(data);
        return true;
    }

    /**
     * Prints all of names of the files in the appDataFolder. Throws Exception if Credentials are invalid.
     */
    public void printFileNames() {

        setBusy(true);
        Log.d(DRIVE_SERVICE_TAG, "Files: ");

        // File searcher.
        Drive.Files.List fileSearch;
        try {
            fileSearch = this.service.files().list();
            // Where to search.
            fileSearch.setSpaces(APPDATAFOLDER);
            // Start search.
            FileList files = fileSearch.execute();

            // Look for the right file and return it.
            for (File file : files.getFiles()) {
                Log.d(DRIVE_SERVICE_TAG, file.getName());
            }
            setBusy(false);
        } catch (IOException e) {
            setBusy(false);
            e.printStackTrace();
        }
    }

    /**
     * Checks whether or not Service is busy.
     * @return True for yes, False for no.
     */
    public boolean isBusy() {
        return isBusy;
    }

    /**
     * Sets whether or not Service is busy.
     * @param busy True for yes, False for no.
     */
    private void setBusy(boolean busy) {
        isBusy = busy;
    }
}
