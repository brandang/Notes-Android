package com.example.notes;

/**
 * Listener that gets notified whenever the user pressed enter.
 * Notifies what should be displaced onto a new line.
 */
public interface EnterKeyPressedListener {

    /**
     * The user pressed enter.
     * @param position The position of the cursor after the recently added new line character.
     * @param newLine The text that should get displaced onto new line.
     */
    void onEnterPressed(int position, String newLine);
}
