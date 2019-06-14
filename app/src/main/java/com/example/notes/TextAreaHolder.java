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

    private TextChangeListener listener;

    /**
     * Creates a new TextAreaHolder containing just the specified textarea.
     * @param textArea The View.
     */
    public TextAreaHolder(@NonNull TextArea textArea) {
        super(textArea);
        this.textArea = textArea;

        // Add listener so that everyone gets notified.
        this.textArea.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                return;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextChangeListener listener = TextAreaHolder.this.listener;
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
     * Sets the data to display. Note that if the user wants to add a listener, it should be done
     * BEFORE calling this method, lest there by bugs.
     * @param data The data.
     * @param size The size of the text.
     */
    public void setData(String data, float size) {
        this.textArea.setText(data);
        this.textArea.setTextSize(size);
    }

    /**
     * Set listener that listens in and gets notified whenever the text in this item gets changed.
     * Note that one should add the listener before setting any data, otherwise there will be bugs.
     * @param listener The listener that listens in.
     */
    public void addTextChangeListener(TextChangeListener listener) {
        this.listener = listener;
    }
}