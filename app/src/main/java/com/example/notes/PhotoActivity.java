package com.example.notes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.Calendar;

/**
 * Activity that is responsible for obtaining photo from user. If the user returned without choosing
 * a photo, the returned result will be null.
 */
public class PhotoActivity extends AppCompatActivity {

    // Request code used to obtain photo from camera.
    final private static int REQUEST_CODE_PHOTO = 0;

    // Uri of the photo that the user choose. Null if unspecified.
    private Uri uriFilePath;

    private Toolbar toolbar;

    private PhotoView photoView;
    private TextView prompt;

    // Floating action buttons at the bottom.
    private FloatingActionButton capture, search, accept, decline;

    // Views that contain the sets of buttons at the bottom.
    private LinearLayout photoButtons, promptButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initially assume user did not choose any image.
        this.uriFilePath = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_taker_screen);
        this.toolbar = (Toolbar) findViewById(R.id.photos_toolbar);
        this.toolbar.setTitle(getString(R.string.choose_photo_title));
        setSupportActionBar(toolbar);

        this.promptButtons = findViewById(R.id.prompt_buttons);
        this.photoButtons = findViewById(R.id.photo_buttons);

        this.photoView = findViewById(R.id.photo);
        this.prompt = findViewById(R.id.choose_photo_prompt);

        // Enable the back button.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("photo", PhotoActivity.this.uriFilePath);
                setResult(Activity.RESULT_OK, resultIntent);
                onBackPressed();
                finish();
            }
        });

        this.setupButtons();
        this.showPrompt();
    }

    /**
     * Binds the floating action buttons and sets their behaviours.
     */
    private void setupButtons() {

        this.capture = findViewById(R.id.capture_button);
        this.search = findViewById(R.id.search_button);
        this.accept = findViewById(R.id.accept_button);
        this.decline = findViewById(R.id.decline_button);

        final Animation captureAnim = PhotoActivity.this.getFabClickAnim(this.capture);
        final Animation searchAnim = PhotoActivity.this.getFabClickAnim(this.search);

        this.capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.this.capture.startAnimation(captureAnim);
                PhotoActivity.this.startImageCapture();
            }
        });
        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.this.search.startAnimation(searchAnim);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
    }

    /**
     * Start capturing an image using the camera.
     */
    private void startImageCapture() {
        PackageManager packageManager = getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            File mainDirectory = new File(Environment.getExternalStorageDirectory(), "MyFolder/tmp");
            if (!mainDirectory.exists())
                mainDirectory.mkdirs();

            Calendar calendar = Calendar.getInstance();

            uriFilePath = Uri.fromFile(new File(mainDirectory, "IMG_" + calendar.getTimeInMillis()));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFilePath);
            startActivityForResult(intent, REQUEST_CODE_PHOTO);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_PHOTO) {
//                String filePath = uriFilePath.getPath();
                this.photoView.setImageURI(uriFilePath);
                this.showPhoto();
                /*Intent resultIntent = new Intent();
                resultIntent.putExtra("photo", filePath);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();*/
            }
        }
    }

    /**
     * Shows the photo and hides the prompt. Does the same for the buttons.
     */
    private void showPhoto() {
        if (this.photoView == null || this.prompt == null)
            return;
        this.photoView.setVisibility(View.VISIBLE);
        this.prompt.setVisibility(View.GONE);
        this.photoButtons.setVisibility(View.VISIBLE);
        this.promptButtons.setVisibility(View.GONE);
        Animation popIn = AnimationUtils.loadAnimation(this, R.anim.pop_in_anim);
        this.photoView.startAnimation(popIn);
        Animation popUp = AnimationUtils.loadAnimation(this, R.anim.pop_up_anim);
        this.photoButtons.startAnimation(popUp);
    }

    /**
     * Shows the prompt and hides the photo. Does the same for the buttons.
     */
    private void showPrompt() {
        if (this.photoView == null || this.prompt == null)
            return;
        this.photoView.setVisibility(View.GONE);
        this.prompt.setVisibility(View.VISIBLE);
        this.photoButtons.setVisibility(View.GONE);
        this.promptButtons.setVisibility(View.VISIBLE);
    }

    /**
     * Returns an animation to use for whenever a FAB is clicked.
     * @param button The button to apply the animation to.
     * @return The Animation.
     */
    private Animation getFabClickAnim(final FloatingActionButton button) {
        Animation jumpUp = AnimationUtils.loadAnimation(PhotoActivity.this,
                R.anim.jump_up_anim);
        final Animation jumpDown = AnimationUtils.loadAnimation(PhotoActivity.this,
                R.anim.jump_down_anim);
        jumpUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                button.startAnimation(jumpDown);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return jumpUp;
    }
}
