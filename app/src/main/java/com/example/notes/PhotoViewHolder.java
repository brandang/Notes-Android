package com.example.notes;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.chrisbanes.photoview.PhotoView;

/**
 * View holder that contains a single PhotoView.
 */
public class PhotoViewHolder extends RecyclerView.ViewHolder {

    private CustomPhotoView photoView;

    /**
     * Creates a new PhotoViewHolder containing just the specified TextArea.
     * @param photoView The View.
     */
    public PhotoViewHolder(@NonNull CustomPhotoView photoView) {
        super(photoView);
        this.photoView = photoView;
    }

    /**
     * Set the image to display.
     * @param uri The uri of the image as a String.
     */
    public void setImage(String uri) {
        this.photoView.setImageURI(Uri.parse(uri));
    }

}
