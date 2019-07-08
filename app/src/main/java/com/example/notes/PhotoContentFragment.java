package com.example.notes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.github.chrisbanes.photoview.PhotoView;

/**
 * Fragment that is responsible for prompting user to choose an image, and then displaying that
 * image when chosen.
 */
public class PhotoContentFragment extends Fragment {

    private PhotoView photoView;
    private TextView prompt;

    private boolean attached = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment.
        View layout = inflater.inflate(R.layout.photo_content, container,false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        this.photoView = view.findViewById(R.id.photo);
        this.prompt = view.findViewById(R.id.choose_photo_prompt);
        this.showPrompt();
    }

    /**
     * Shows prompt to choose an image.
     */
    public void showPrompt() {
        if (this.prompt == null || this.photoView == null)
            return;
        this.prompt.setVisibility(View.VISIBLE);
        this.photoView.setVisibility(View.GONE);
    }

    /**
     * Shows the image.
     * @param uri The URI of the image.
     */
    public void showImage(Uri uri) {
        if (this.prompt == null || this.photoView == null)
            return;
        this.prompt.setVisibility(View.GONE);
        this.photoView.setVisibility(View.VISIBLE);
        this.photoView.setImageURI(uri);
        Animation popIn = AnimationUtils.loadAnimation(this.getContext(), R.anim.pop_in_anim);
        this.photoView.startAnimation(popIn);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.attached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.attached = false;
    }
}
