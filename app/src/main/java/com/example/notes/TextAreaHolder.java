package com.example.notes;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single textarea.
 */
public class TextAreaHolder extends RecyclerView.ViewHolder {

    private TextArea textArea;

    /**
     * Creates a new TextAreaHolder containing just the specified textarea.
     * @param textArea The View.
     */
    public TextAreaHolder(@NonNull TextArea textArea) {
        super(textArea);
        this.textArea = textArea;
    }

    /**
     * Sets the background of the view.
     * @param background The new background.
     */
    public void setBackground(Drawable background) {
        this.textArea.setBackground(background);
    }

    /**
     * Sets the data to display.
     * @param data The data.
     * @param size The size of the text.
     */
    public void setData(String data, float size) {
        this.textArea.setText(data);
        this.textArea.setTextSize(size);
    }
}