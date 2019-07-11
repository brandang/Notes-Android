package com.example.notes;

/**
 * A wrapper for SaveData class that stores whether or not the SaveData is valid, it is empty,
 * or there were errors loading the SaveData.
 */
public class DownloadedData {

    // The result codes.
    final public static int RESULTS_OK = 0;

    final public static int RESULTS_EMPTY = 1;

    final public static int RESULTS_ERROR = 2;

    private int resultCode;

    private SaveData saveData;

    /**
     * Create a new Wrapper for SaveData.
     * @param saveData The SaveData.
     * @param resultCode The result code for the data.
     */
    public DownloadedData(SaveData saveData, int resultCode) {
        this.saveData = saveData;
        this.resultCode = resultCode;
    }

    /**
     * Returns the SaveData.
     * @return The SaveData.
     */
    public SaveData getSaveData() {
        return this.saveData;
    }

    /**
     * Returns the result code.
     * @return The result code.
     */
    public int getResultCode() {
        return this.resultCode;
    }

}
