package com.example.notes;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single TextArea.
 */
public class TextAreaHolder extends RecyclerView.ViewHolder {

    private TextArea textArea;

    private TextChangeListener textChangeListener;

    private EnterKeyPressedListener enterKeyPressedListener;

    /**
     * Creates a new TextAreaHolder containing just the specified textarea.
     * @param textArea The View.
     */
    public TextAreaHolder(@NonNull TextArea textArea) {
        super(textArea);
        this.textArea = textArea;

        // Add textChangeListener so that everyone gets notified.
        this.textArea.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // User just clicked enter.
                if (before == 0 && count == 1 && charSequence.charAt(start) == '\n') {
                    TextAreaHolder.this.onEnterPressed();
                }
                TextChangeListener listener = TextAreaHolder.this.textChangeListener;
                if (listener != null) {
                    listener.onTextChanged(
                            TextAreaHolder.this.textArea.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                return;
            }
        });
    }

    /**
     * Sets the background of the view.
     * @param background The new background.
     */
    public void setBackground(Drawable background) {
        this.textArea.setBackground(background);
    }

    /**
     * Sets the data to display. Also sets textChangeListener that listens in and gets notified whenever the
     * text in this item gets changed.
     * @param data The data.
     * @param size The size of the text.
     * @param textListener The textChangeListener that listens in.
     */
    public void setData(String data, float size, TextChangeListener textListener, EnterKeyPressedListener enterListener) {
        this.addTextChangeListener(textListener);
        this.addEnterKeyPressedListener(enterListener);
        this.textArea.setText(data);
        this.textArea.setTextSize(size);
    }

    /**
     * Notify listeners that the user just pressed enter.
     */
    private void onEnterPressed() {
        this.enterKeyPressedListener.onEnterPressed(this.getAdapterPosition(),
                this.cutTextAfterCursor());
    }

    /**
     * Deletes and returns the text after the current position of the cursor.
     * @return The text.
     */
    private String cutTextAfterCursor() {
        int start = this.textArea.getSelectionStart();
        String endText = this.textArea.getText().toString().substring(start);
        String startText = this.textArea.getText().toString().substring(0, start-1);
        this.textArea.setText(startText);
        return endText;
    }

    /**
     * Set textChangeListener that listens in and gets notified whenever the text in this item gets changed.
     * @param listener The textChangeListener that listens in.
     */
    public void addTextChangeListener(TextChangeListener listener) {
        this.textChangeListener = listener;
    }

    /**
     * Add a new EnterKeyPressedListener.
     * @param listener The listener to add.
     */
    public void addEnterKeyPressedListener(EnterKeyPressedListener listener) {
        this.enterKeyPressedListener = listener;
    }

    /**
     * Request focus for this item.
     */
    public void requestFocus() {
        this.textArea.requestFocus();
    }
}