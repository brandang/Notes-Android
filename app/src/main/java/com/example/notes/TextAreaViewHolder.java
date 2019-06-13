package com.example.notes;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single TextArea.
 */
public class TextAreaViewHolder extends RecyclerView.ViewHolder {

    private TextArea textArea;

    /**
     * Creates a new TextAreaViewHolder containing just the specified TextArea.
     * @param textArea The View.
     */
    public TextAreaViewHolder(@NonNull TextArea textArea) {
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