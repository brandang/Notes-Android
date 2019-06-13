package com.example.notes;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single recycler_imageview.
 */
public class RecyclerImageViewHolder extends RecyclerView.ViewHolder {

    private RecyclerImageView imageView;

    /**
     * Creates a new RecyclerImageViewHolder containing just the specified textarea.
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
