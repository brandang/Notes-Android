package com.example.notes;

import android.view.View;

/**
 * An interface that allows other classes to display a Snackbar. Useful for when a class needs to
 * display s Snackbar but they are not able to.
 */
public interface SnackbarDisplayer {

    /**
     * Shows a new Snackbar given the information.
     * @param msg The message to display.
     * @param actionMsg The action message.
     * @param length The amount of time to display for.
     * @param listener Listener to notify whenever the action is clicked.
     */
    void showSnackbar(String msg, String actionMsg, int length, View.OnClickListener listener);
}
