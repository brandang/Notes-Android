package com.example.notes;

import java.io.Serializable;

/**
 * A class that stores a users preferences.
 */
public class Settings implements Serializable {

    // Size of the text.
    private String textSize;

    /**
     * Create a new Settings configuration.
     * @param textSize The text size.
     */
    public Settings(String textSize) {
        this.textSize = textSize;
    }

    /**
     * Sets the size of the text.
     * @param textSize The new text size.
     */
    public void setTextSize(String textSize) {
        this.textSize = textSize;
    }

    /**
     * Returns the size of the text that the user requested as a String.
     * @return The size of the text.
     */
    public String getTextSize() {
        return textSize;
    }
}
