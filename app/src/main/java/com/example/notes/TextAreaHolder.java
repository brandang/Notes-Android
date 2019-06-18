package com.example.notes;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single TextArea.
 */
public class TextAreaHolder extends RecyclerView.ViewHolder implements EnterKeyPressedListener,
        BackKeyPressedListener {

    private TextArea textArea;

    // Listeners.
    private AddLineListener addLineListener;

    private RemoveLineListener removeLineListener;

    /**
     * Creates a new TextAreaHolder containing just the specified textarea.
     * @param textArea The View.
     */
    public TextAreaHolder(@NonNull TextArea textArea) {
        super(textArea);
        this.textArea = textArea;
        this.textArea.setEnterListener(this);
        this.textArea.setBackListener(this);
    }

    /**
     * Sets the background of the view.
     * @param background The new background.
     */
    public void setBackground(Drawable background) {
        this.textArea.setBackground(background);
    }

    /**
     * Sets the data to display. Also sets textChangeListener that listens in and gets notified
     * whenever the text in this item gets changed.
     * @param data The data.
     * @param size The size of the text.
     * @param textListener The textChangeListener that listens in.
     */
    public void setData(String data, float size, TextChangeListener textListener,
                         AddLineListener addLineListener, RemoveLineListener removeLineListener) {
        // Update which listeners to notify.
        this.setTextChangeListener(textListener);
        this.setEnterKeyPressedListener(addLineListener);
        this.setBackKeyPressedListesner(removeLineListener);
        this.textArea.setText(data);
        this.textArea.setTextSize(size);
    }

    /**
     * Set textChangeListener that listens in and gets notified whenever the text in this item gets changed.
     * @param listener The textChangeListener that listens in.
     */
    public void setTextChangeListener(TextChangeListener listener) {
        // Pass on to text area.
        this.textArea.setTextChangeListener(listener);
    }

    /**
     * Add a new EnterKeyPressedListener.
     * @param listener The listener to add.
     */
    public void setEnterKeyPressedListener(AddLineListener listener) {
        this.addLineListener = listener;
    }

    /**
     * Add a new BackKeyPressedListener.
     * @param listener The listener to add.
     */
    public void setBackKeyPressedListesner(RemoveLineListener listener) {
        this.removeLineListener = listener;
    }

    /**
     * Request focus for this item at the given cursor position.
     * @param cursorPosition The position to focus the cursor to.
     */
    public void requestFocus(int cursorPosition) {
        this.textArea.requestFocus();
        this.textArea.setSelection(cursorPosition);
    }

    @Override
    public void onBackPressed(String previousLine) {
        if (this.getAdapterPosition() == 0) {
            return;
        }
        this.removeLineListener.removeLine(getAdapterPosition(), previousLine);
    }

    @Override
    public void onEnterPressed(int position, String newLine) {
        // Delete everything after and including the new line character.
        position --;
        if (position < 0)
            position = 0;

        this.textArea.setText(this.textArea.getText().toString().substring(0, position));
        this.addLineListener.addLine(this.getAdapterPosition(), newLine);
    }
}