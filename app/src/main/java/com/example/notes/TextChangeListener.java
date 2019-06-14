package com.example.notes;

/**
 * Listener that notifies whenever a View`s text is changed.
 */
public interface TextChangeListener {

    /**
     * The text was just changed.
     * @param newText The new text.
     */
    void onTextChanged(String newText);
}
