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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    // Floating action buttons at the bottom.
    private FloatingActionButton capture, search, accept, decline;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initially assume user did not choose any image.
        this.uriFilePath = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_taker_screen);
        this.toolbar = (Toolbar) findViewById(R.id.photos_toolbar);
        this.toolbar.setTitle(getString(R.string.choose_photo_title));
        setSupportActionBar(toolbar);

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

        this.bindButtons();

//        this.startImageCapture();
    }

    /**
     * Binds the floating action buttons and sets their behaviours.
     */
    private void bindButtons() {

        this.capture = findViewById(R.id.left_action_button);
        this.search = findViewById(R.id.right_action_button);

        final Animation captureAnim = PhotoActivity.this.getFabClickAnim(this.capture);
        final Animation searchAnim = PhotoActivity.this.getFabClickAnim(this.search);

        this.capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.this.capture.startAnimation(captureAnim);
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
                String filePath = uriFilePath.getPath(); // Here is path of your captured image, so you can create bitmap from it, etc.
                Intent resultIntent = new Intent();
                resultIntent.putExtra("photo", filePath);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
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
