package com.example.notes;

/**
 * Listener that gets notified whenever the user pressed backspace, and the cursor is currently
 * in the first position.
 * Notifies what the text is which should be displaced onto the previous line.
 */
public interface BackKeyPressedListener {

    /**
     * The user pressed backspace.
     * @param previousLine The text that should get displaced onto the previous line.
     */
    void onBackPressed(String previousLine);

}
