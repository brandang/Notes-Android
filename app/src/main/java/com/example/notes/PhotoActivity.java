package com.example.notes;

import android.app.Activity;
import android.content.Intent;
import android.content.UriPermission;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.transition.TransitionManager;

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
public class PhotoActivity extends AppCompatActivity implements SnackbarDisplayer {

    // Folder to save photos in.
    final private static String DATA_SAVE_FOLDER = "data/notes/photos";

    // nomedia file. Give it a name because for some reason it does not get created without a name.
    final private static String DATA_NOMEDIA_FILE = "data/notes/data.nomedia";

    // Request code used to obtain photo from camera.
    final private static int REQUEST_CODE_CAPTURE = 0;

    // Request code to obtain image from internal storage.
    final private static int REQUEST_CODE_SEARCH = 1;

    // States.
    final private static int STATE_PROMPT = 0;
    final private static int STATE_PHOTO = 1;
    final private static int STATE_REORDER = 2;

    private int state = STATE_PROMPT;

    // Uri of the photo that the user choose. Null if unspecified.
    private Uri uriFilePath;

    private Toolbar toolbar;

    // Custom action buttons at the bottom.
    private AnimatedFloatingButton capture, search, accept, decline;

    private LinearLayout acceptContainer;

    // Layout containing app bar and everything else.
    private CoordinatorLayout background;

    private PhotoContentFragment photoContentFragment;

    private ReorderFragment reorderFragment;

    // SaveData obtained from parent Activity.
    private SaveData saveData;

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
        this.acceptContainer = findViewById(R.id.photo_buttons);

        // Obtain SaveData.
        this.saveData = (SaveData) getIntent().getSerializableExtra("saveData");

        // Create fragments.
        this.reorderFragment = new ReorderFragment();
        this.photoContentFragment = new PhotoContentFragment();
        this.attachPhotoContentFragment();

        // Enable the back button.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelResults();
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
        this.decline.enableClickAnimation(false);

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

    /**
     * Attach the PhotoContentFragment to the container in this Activity.
     */
    private void attachPhotoContentFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                this.photoContentFragment).commit();
    }

    /**
     * Attach the ReorderFragment to the container in this Activity. Also adds specified photo to
     * top of the list in the Fragment.
     * @param saveData The SaveData to display in the Fragment.
     * @param uri The uri of the new photo to add.
     */
    private void attachReorderFragment(SaveData saveData, Uri uri) {
        this.state = STATE_REORDER;
        saveData.getData().add(0, new ItemData(uri.toString(), ItemData.TYPE_PHOTO));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                this.reorderFragment).commit();
        this.reorderFragment.setData(saveData, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (uriFilePath != null)
            outState.putString("uri_file_path", uriFilePath.toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        this.cancelResults();
    }

    /**
     * Start capturing an image using the camera.
     */
    private void startImageCapture() {
        this.uriFilePath = null;
        PackageManager packageManager = getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            File mainDirectory = new File(Environment.getExternalStorageDirectory(), DATA_SAVE_FOLDER);
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
                this.showPhoto(this.uriFilePath);
            }
            else if (requestCode == REQUEST_CODE_SEARCH) {

                this.uriFilePath = data.getData();

                // Show message if there was an error.
                if (this.uriFilePath == null) {
                    Snackbar message = Snackbar.make(this.background,
                            getString(R.string.choose_photo_failed),
                            Snackbar.LENGTH_LONG);
                    message.show();
                }
                // Else show the photo and ask user to accept.
                else {
                    this.showPhoto(this.uriFilePath);
                }
            }
        }
    }

    /**
     * Copies the photo from the given URI into the folder that the app uses to store data.
     * Returns null if save was not successful. If the photo already exists in the folder, returns
     * the URI to that photo instead.
     * @param photo To URI for the photo to save.
     * @return The URI of the new photo.
     */
    private Uri copyPhoto(Uri photo) {

        // Create folder.
        File mainDirectory = new File(Environment.getExternalStorageDirectory(), DATA_SAVE_FOLDER);
        File noMedia = new File(Environment.getExternalStorageDirectory(), DATA_NOMEDIA_FILE);
        if (!mainDirectory.exists()) {
            mainDirectory.mkdirs();
        }
        // A .nomedia file ensures that gallery would not include media in this folder.
        if (!noMedia.exists()) {
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // If the file already exists in this data folder, don't copy again.
        File outputFile = new File(mainDirectory, this.getFileName(photo));
        if (outputFile.exists())
            return Uri.fromFile(outputFile);

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
    private void showPhoto(Uri uri) {
        this.state = STATE_PHOTO;

        // Hide buttons.
        this.search.hide();
        this.capture.hide();

        // Animations for buttons.
        this.accept.show();
        this.decline.show();

        this.photoContentFragment.showImage(uri);
    }

    /**
     * Shows the prompt and hides the photo. Does the same for the buttons.
     */
    private void showPrompt() {
        this.state = STATE_PROMPT;

        // Hide buttons.
        this.accept.hide();
        this.decline.hide();

        // Show the buttons.
        this.capture.show();
        this.search.show();

        this.photoContentFragment.showPrompt();
    }

    /**
     * Decline Floating action button was just clicked. Handles it.
     */
    private void onDeclineClick() {
        this.uriFilePath = null;
        this.showPrompt();
    }

    /**
     * Accept floating action button was just clicked. Handles it.
     */
    private void onAcceptClick() {
        // Take action based on what state it currently is.
        if (this.state != STATE_REORDER) {
            this.decline.hide();
            // Copy this image into the data folder.
            this.uriFilePath = this.copyPhoto(this.uriFilePath);
            this.attachReorderFragment(this.saveData, this.uriFilePath);
            this.obtainPersistedPermission(this.uriFilePath);
            // Show prompt.
            Snackbar message = Snackbar.make(this.background,
                    getString(R.string.reorder_prompt),
                    Snackbar.LENGTH_LONG);
            message.show();
            this.toolbar.setTitle(getString(R.string.reorder_title));
            this.moveAcceptButtonRight();
        } else {
            this.returnResults();
        }
    }

    /**
     * Transitions the Accept button to the right.
     */
    private void moveAcceptButtonRight() {
        // Change layout gravity and enable Animation for it.
        TransitionManager.beginDelayedTransition((ViewGroup) this.acceptContainer.getParent());
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) this.acceptContainer.getLayoutParams();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        this.acceptContainer.setLayoutParams(layoutParams);
    }

    /**
     * Return the image URI to the activity that called this one. Ends this activity.
     */
    private void returnResults() {
        Intent results = new Intent();

        Log.d("Main", "" + this.uriFilePath);

        results.putExtra("photo", this.uriFilePath);
        // Grant permissions to calling activity to read file.
        results.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Bundle bundle = new Bundle();
        bundle.putSerializable("saveData", this.reorderFragment.getData());
        results.putExtras(bundle);

        // Attempt to get long term permission for image.
        /*if (!this.obtainPersistedPermission(this.uriFilePath)) {
            setResult(Activity.RESULT_CANCELED, results);
            Log.d("Main", "cancelled");
        } else*/
        setResult(Activity.RESULT_OK, results);
        finish();
    }

    /**
     * Cancel choosing an image and return to calling Activity.
     */
    private void cancelResults() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("photo", (Parcelable[]) null);
        setResult(Activity.RESULT_CANCELED, resultIntent);
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

    /**
     * Obtain read permissions for URI so that long term access to this file is allowed.
     * @param uri The URI of the file.
     * @return True if successful, False if not.
     */
    private boolean obtainPersistedPermission(Uri uri) {
        boolean obtained = false;
        try {
            getContentResolver()
                    .takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            for (UriPermission perm : getContentResolver().getPersistedUriPermissions()) {
                if (perm.getUri().equals(uri)) {
                    obtained = true;
                    break;
                }
            }
        }
        catch (SecurityException e) {
            obtained = false;
        }
        return obtained;
    }

    @Override
    public void showSnackbar(String msg, String actionMsg, int length, View.OnClickListener listener) {
        Snackbar.make(this.background, msg, length).setAction(actionMsg, listener).show();
    }
}
