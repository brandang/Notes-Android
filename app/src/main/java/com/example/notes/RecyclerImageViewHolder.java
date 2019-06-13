package com.example.notes;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single RecyclerImageView.
 */
public class RecyclerImageViewHolder extends RecyclerView.ViewHolder {

    private RecyclerImageView imageView;

    /**
     * Creates a new RecyclerImageViewHolder containing just the specified TextArea.
     * @param imageView The View.
     */
    public RecyclerImageViewHolder(@NonNull RecyclerImageView imageView) {
        super(imageView);
        this.imageView = imageView;
    }

    /**
     * Set the image to display.
     * @param uri The uri of the image as a String.
     */
    public void setImage(String uri) {
        this.imageView.setImageURI(Uri.parse(uri));
    }

}
