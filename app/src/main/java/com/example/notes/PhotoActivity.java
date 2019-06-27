package com.example.notes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * Activity that is responsible for obtaining photo from user. If the user returned without choosing
 * a photo, the returned result will be null.
 */
public class PhotoActivity extends AppCompatActivity {

    // Folder to save photos in.
    final private static String DATA_SAVE_FOLDER = "data/notes";

    // Request code used to obtain photo from camera.
    final private static int REQUEST_CODE_CAPTURE = 0;

    // Request code to obtain image from internal storage.
    final private static int REQUEST_CODE_SEARCH = 1;

    // Uri of the photo that the user choose. Null if unspecified.
    private Uri uriFilePath;

    private Toolbar toolbar;

    private PhotoView photoView;
    private TextView prompt;

    // Custom action buttons at the bottom.
    private AnimatedActionButton capture, search, accept, decline;

    // Views that contain the sets of buttons at the bottom.
    private LinearLayout photoButtons, promptButtons;

    // Layout containing app bar and everything else.
    private CoordinatorLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initially assume user did not choose any image.
        this.uriFilePath = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_taker_screen);
        this.toolbar = (Toolbar) findViewById(R.id.photos_toolbar);
        this.toolbar.setTitle(getString(R.string.choose_photo_title));
        setSupportActionBar(toolbar);

        this.background = findViewById(R.id.photos_screen);

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

        this.capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.this.startImageCapture();
            }
        });
        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.this.startImageSearch();
            }
        });
        this.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.this.onAcceptClick();
            }
        });
        this.decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoActivity.this.onDeclineClick();
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
        this.uriFilePath = null;
        PackageManager packageManager = getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            File mainDirectory = new File(Environment.getExternalStorageDirectory(), "data/notes");
            if (!mainDirectory.exists())
                mainDirectory.mkdirs();

            Calendar calendar = Calendar.getInstance();

            this.uriFilePath = Uri.fromFile(new File(mainDirectory, "IMG_" + calendar.getTimeInMillis()));
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, this.uriFilePath);
            startActivityForResult(intent, REQUEST_CODE_CAPTURE);
        }
    }

    /**
     * Starts an activity responsible for searching for a photo stored on this device.
     */
    private void startImageSearch() {
        this.uriFilePath = null;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SEARCH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Main", "Request Code: " + requestCode + " Result Code: " + resultCode
                + " Data: " + data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CODE_CAPTURE) {
                this.photoView.setImageURI(this.uriFilePath);
                this.showPhoto();
            }
            else if (requestCode == REQUEST_CODE_SEARCH) {
                // Copy this image into the data folder. Have to do this because URI may not be
                // valid when starting other activities.
                this.uriFilePath = this.copyPhoto(data.getData());
                // Show message if there was an error.
                if (this.uriFilePath == null) {
                    Snackbar message = Snackbar.make(this.background,
                            getString(R.string.choose_photo_failed),
                            Snackbar.LENGTH_LONG);
                    message.show();
                }
                // Else show the photo and ask user to accept.
                else {
                    this.photoView.setImageURI(this.uriFilePath);
                    this.showPhoto();
                }
            }
        }
    }

    /**
     * Copies the photo from the given URI into the folder that the app uses to store data.
     * Returns null if save was not successful.
     * @param photo To URI for the photo to save.
     * @return The URI of the new photo.
     */
    private Uri copyPhoto(Uri photo) {
        // Create folder.
        File mainDirectory = new File(Environment.getExternalStorageDirectory(), DATA_SAVE_FOLDER);
        if (!mainDirectory.exists())
            mainDirectory.mkdirs();

        File outputFile = new File(mainDirectory, this.getFileName(photo));

        // Save the data.
        InputStream in;
        OutputStream out;
        try {
            in = this.getContentResolver().openInputStream(photo);
            out = new FileOutputStream(outputFile);
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }

            Uri save = Uri.fromFile(outputFile);
            out.close();
            in.close();
            return save;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        // Animation for photo coming in.
        Animation popIn = AnimationUtils.loadAnimation(this, R.anim.pop_in_anim);
        this.photoView.startAnimation(popIn);

        // Animations for buttons.
        this.accept.show();
        this.decline.show();
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

        // Show the buttons.
        this.capture.show();
        this.search.show();
    }

    /**
     * Decline Floating action button was just clicked. Handles it.
     */
    private void onDeclineClick() {
        this.uriFilePath = null;
        this.photoView.setImageURI(null);
        this.showPrompt();
    }

    /**
     * Accept floating action button was just clicked. Handles it.
     */
    private void onAcceptClick() {
        this.returnResults();
    }

    /**
     * Return the image URI to the activity that called this one. Ends this activity.
     */
    private void returnResults() {
        Intent results = new Intent();
        results.putExtra("photo", this.uriFilePath);
        setResult(Activity.RESULT_OK, results);
        finish();
    }

    /**
     * Returns the name of the file given the URI.
     * @param uri The URI.
     * @return The name of the file.
     */
    private String getFileName(Uri uri) {
        Cursor returnCursor =
                this.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
}
