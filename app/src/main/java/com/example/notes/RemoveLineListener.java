package com.example.notes;

/**
 * Listener that should be notified so that it can displace the text onto the previous line.
 */
public interface RemoveLineListener {

    /**
     * Displace the text onto the previous line.
     * @param position Position within RecyclerView of the item which should be displaced.
     * @param line The text that should get displaced onto the previous line.
     */
    void removeLine(int position, String line);
}
