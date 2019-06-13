package com.example.notes;

/**
 * A class for purposes of storing data for a single ItemView in a RecyclerView.
 */
public class ItemViewData {

    // Constants for use in identifying which view type this ItemView is for.
    final public static int TYPE_PHOTO = 0;
    final public static int TYPE_TEXT = 1;

    private int viewType;

    private String data;

    /**
     * Create a new wrapper for a ItemView.
     * @param data The data.
     * @param viewType The type. Will set type to TYPE_TEXT if invalid input.
     */
    public ItemViewData(String data, int viewType) {
        this.data = data;
        this.viewType = viewType;
        if (this.viewType != TYPE_PHOTO && this.viewType != TYPE_TEXT) {
            this.viewType = TYPE_TEXT;
        }
    }

    /**
     * Returns what kind type this ItemView is.
     * @return The type.
     */
    public int getViewType() {
        return viewType;
    }

    /**
     * Returns the data associated with this ItemView instance. If it is text data, it will return
     * the text, whereas if it is photo, it will have the uri.
     * @return The data.
     */
    public String getData() {
        return data;
    }
}
