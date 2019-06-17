package com.example.notes;

/**
 * Listener that gets notified whenever the user pressed enter.
 * Notifies what item position in the RecyclerView this occured in as well as the text which should
 * be displaced onto a new line.
 */
public interface EnterKeyPressedListener {

    /**
     * The user pressed enter.
     * @param position Position within RecyclerView of the item where user pressed Enter.
     * @param newLine The text that should get displaced onto new line.
     */
    void onEnterPressed(int position, String newLine);
}
