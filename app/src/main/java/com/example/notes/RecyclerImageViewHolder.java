package com.example.notes;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * View holder that contains a single recycler_imageview.
 */
public class RecyclerImageViewHolder extends RecyclerView.ViewHolder {

    // Transparency values.
    final private static float SELECTED_TRANSPARENCY = 0.5f;

    final private static float UNSELECTED_TRANSPARENCY = 1f;

    // Child views.
    private RecyclerImageView imageView;

    private LinearLayout background;

    private Context context;

    /**
     * Creates a new RecyclerImageViewHolder containing a RecyclerImageView and a LinearLayout.
     * @param view The View.
     */
    public RecyclerImageViewHolder(@NonNull View view, Context context) {
        super(view);
        this.imageView = view.findViewById(R.id.item_imageview);
        this.background = view.findViewById(R.id.item_background);
        this.context = context;
    }

    /**
     * Set the image to display.
     * @param uri The uri of the image as a String.
     */
    public void setImage(String uri) {
        if (uri == null)
            this.imageView.setImageURI(null);
        else
            this.imageView.setImageURI(Uri.parse(uri));
        this.imageView.invalidate();
        this.background.invalidate();
        this.startAnimation(context);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    /**
     * Sets the color of the background.
     * @param color The color of the background.
     */
    public void setBackgroundColor(int color) {
        this.background.setBackgroundColor(color);
    }

    /**
     * Handles being selected. Changes transparency.
     */
    public void onSelected() {
        this.imageView.setAlpha(SELECTED_TRANSPARENCY);
        this.background.setAlpha(SELECTED_TRANSPARENCY);
    }

    /**
     * Handles being cleared. Reverts transparency back to normal.
     */
    public void onClear() {
        this.imageView.setAlpha(UNSELECTED_TRANSPARENCY);
        this.background.setAlpha(UNSELECTED_TRANSPARENCY);
    }

    /**
     * Start animation for the photo appearing.
     * @param context The context.
     */
    private void startAnimation(Context context) {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.pop_in_anim);
        this.imageView.startAnimation(animation);
    }
}
