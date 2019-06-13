package com.example.notes;

import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single recycler_imageview.
 */
public class RecyclerImageViewHolder extends RecyclerView.ViewHolder {

    private RecyclerImageView imageView;
    private LinearLayout background;

    /**
     * Creates a new RecyclerImageViewHolder containing a RecyclerImageView and a LinearLayout.
     * @param view The View.
     */
    public RecyclerImageViewHolder(@NonNull View view) {
        super(view);
        this.imageView = view.findViewById(R.id.item_imageview);
        this.background = view.findViewById(R.id.item_background);
    }

    /**
     * Set the image to display.
     * @param uri The uri of the image as a String.
     */
    public void setImage(String uri) {
        this.imageView.setImageURI(Uri.parse(uri));
    }

    /**
     * Sets the color of the background.
     * @param color The color of the background.
     */
    public void setBackgroundColor(int color) {
        this.background.setBackgroundColor(color);
    }
}
