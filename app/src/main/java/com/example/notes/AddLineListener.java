package com.example.notes;

/**
 * Listener that should be notified so that it can display the text onto the next line.
 */
public interface AddLineListener {

    /**
     * Displace the text onto the .
     * @param position Position within RecyclerView of the item that should be displaced.
     * @param newLine The text that should get displaced onto new line.
     */
    void addLine(int position, String newLine);
}
