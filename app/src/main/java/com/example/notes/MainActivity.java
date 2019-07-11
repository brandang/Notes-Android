package com.example.notes;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DownloadDoneListener,
        UploadDoneListener {

    private boolean debug = false;

    // Tag used for debugging.
    final private static String MAIN_ACTIVITY_TAG = "Main Activity:";

    // Request code to identify signing in to Google.
    final private static int REQUEST_CODE_SIGN_IN = 0;

    // Request code to identify loading data from Google.
    final private static int REQUEST_CODE_LOAD_DATA = 1;

    // Request code to identify user modifying settings.
    final private static int REQUEST_CODE_SETTINGS = 2;

    // Request code to identify user rearranged the notes.
    final private static int REQUEST_CODE_REARRANGED = 3;

    // Request code to capture photo.
    final private static int REQUEST_CODE_PHOTO = 4;

    // Request code to capture voice.
    final private static int REQUEST_CODE_VOICE = 5;

    // The layout that contains the loading screen.
    private LinearLayout loadingScreen;

    // View that contains the text area and photos.
    private LinearLayout noteScreen;

    // TextView that displays message for loading screen.
    private TextView loadingMessage;

    // View holding items for the note screen.
    private RecyclerView noteRecyclerView;

    // Adapter for the above.
    private NoteAdapter noteAdapter;

    // Layout containing app bar and everything else.
    private CoordinatorLayout background;

    // Helper to manage files.
    private DriveService service;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setupComponents();
        this.signIn();
    }

    /**
     * Sets up the GUI components.
     */
    @SuppressLint("WrongViewCast")
    private void setupComponents() {
        setContentView(R.layout.activity_main);
        // Views.
        this.background = findViewById(R.id.background);
        this.loadingScreen = findViewById(R.id.loading_screen);
        this.loadingMessage = findViewById(R.id.loading_message);
        this.noteScreen = findViewById(R.id.note_screen);
        this.noteRecyclerView = findViewById(R.id.note_recycler);

        // Setup toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Setup noteAdapter and RecyclerView.
        this.noteAdapter = new NoteAdapter(this,
                null, this.noteRecyclerView);
        // Don't attach a callback because we don't need any gestures here.
        this.noteRecyclerView.setAdapter(this.noteAdapter);
    }

    /**
     * Start activity to sign in to Google.
     */
    private void signIn() {
        this.startLoadScreen(getString(R.string.sign_in_message));
        GoogleSignInClient client = buildGoogleSignInClient();
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Build a Google SignIn client.
     * @return The client.
     */
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(DriveScopes.DRIVE_APPDATA))
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    /**
     * Starts the loading screen with the specified message.
     * @param message The message to display.
     */
    private void startLoadScreen(String message) {
        this.loadingScreen.setVisibility(View.VISIBLE);
        this.noteScreen.setVisibility(View.GONE);
        this.loadingMessage.setText(message);
    }

    /**
     * Starts the note screen and disable the other screens.
     */
    private void startNoteScreen() {
        this.loadingScreen.setVisibility(View.GONE);
        this.noteScreen.setVisibility(View.VISIBLE);

        /*
        this.textarea.requestFocus();
        // Move cursor to beginning.
        this.textarea.setSelection(0);

        // For some reason, if we set the text programmatically, we also have to set these
        // programmatically.
        this.textArea.setSingleLine(false);
        this.textArea.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        this.textArea.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        */
    }

    /**
     * Starts the re-order activity.
     */
    private void startReorderActivity() {
        Intent intent = new Intent(this, ReorderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("saveData", this.noteAdapter.getSaveData());
        intent.putExtras(bundle);
        // Allow activity to actually read files.
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_REARRANGED);
    }

    /**
     * Starts the photo activity to choose a photo.
     */
    private void startPhotoActivity() {
        Intent intent = new Intent(this, PhotoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("saveData", this.noteAdapter.getSaveData());
        intent.putExtras(bundle);
        // Allow activity to read URI photos.
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    /**
     * Starts the Voice activity to get voice notes.
     */
    private void startVoiceActivity() {
        Intent intent = new Intent(this, VoiceActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("saveData", this.noteAdapter.getSaveData());
        intent.putExtras(bundle);
        // Allow activity to read URI photos.
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CODE_VOICE);
    }

    /**
     * Begins the settings activity.
     */
    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        String textSize = "";
        if (this.noteAdapter.getTextSize() == getResources().getInteger(R.integer.Tiny)) {
            textSize = getString(R.string.text_size_tiny);
        } else if (this.noteAdapter.getTextSize() == getResources().getInteger(R.integer.Small)) {
            textSize = getString(R.string.text_size_small);
        } else if (this.noteAdapter.getTextSize() == getResources().getInteger(R.integer.Medium)) {
            textSize = getString(R.string.text_size_medium);
        } else if (this.noteAdapter.getTextSize() == getResources().getInteger(R.integer.Large)) {
            textSize = getString(R.string.text_size_large);
        }
        Settings currentSettings = new Settings(textSize);
        Bundle bundle = new Bundle();
        bundle.putSerializable("settings", currentSettings);
        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.d(MAIN_ACTIVITY_TAG, "Request Code: " + requestCode + " Result Code: " + resultCode
                + " Data: " + data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Log.d(MAIN_ACTIVITY_TAG, "Successfully signed in.");
                    this.onSignInSuccess(data);
                } else {
                    this.onSignInFailed();
                }
                break;

            case REQUEST_CODE_SETTINGS:
                if (resultCode == Activity.RESULT_OK) {
                    Settings settings = (Settings) data.getExtras().getSerializable("settings");
                    String fontSize = settings.getTextSize();
                    this.setTextSize(fontSize);
                }
                break;

            case REQUEST_CODE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri photoUri = data.getParcelableExtra("photo");
                    if (photoUri == null)
                        break;
//                    this.noteAdapter.addData(new ItemData(photoUri.toString(), ItemData.TYPE_PHOTO));
                    Snackbar message = Snackbar.make(MainActivity.this.background,
                            getString(R.string.opened_photo_msg),
                            Snackbar.LENGTH_LONG);
                    // message.show();
                    SaveData saveData = (SaveData) data.getExtras().getSerializable("saveData");
                    this.noteAdapter.setDisplayData(saveData);
                } else {
                    Uri photoUri = data.getParcelableExtra("photo");
                    // Image chosen, but result cancelled, implying Permissions were not obtained.
                    if (photoUri != null) {
                        Snackbar message = Snackbar.make(MainActivity.this.background,
                                getString(R.string.error_photo_msg),
                                Snackbar.LENGTH_LONG);
                         message.show();
                    }
                }
                break;

            case REQUEST_CODE_REARRANGED:
                if (resultCode == RESULT_OK) {
                    SaveData saveData = (SaveData) data.getExtras().getSerializable("saveData");
                    if (saveData == null)
                        break;

                    // Some items were removed by user. Delete unnecessary photos.
                    if (saveData.getData().size() != this.noteAdapter.getItemCount()) {
                        this.removeDeletedPhotos(this.noteAdapter.getSaveData().getData(),
                                saveData.getData());
                    }

                    this.noteAdapter.setDisplayData(saveData);
                }
                break;
        }
    }

    /**
     * Sign in was successful, so handle it.
     * @param result The data.
     */
    private void onSignInSuccess(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                // Can't use lamda because Android does not compile with latest Java version.
                .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
                    @Override
                    public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                        // Use the authenticated account to sign in to the Drive service.
                        GoogleAccountCredential credential =
                                GoogleAccountCredential.usingOAuth2(MainActivity.this,
                                        Collections.singleton(DriveScopes.DRIVE_APPDATA));
                        credential.setSelectedAccount(googleSignInAccount.getAccount());
                        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                        Drive googleDriveService = new Drive.Builder(new NetHttpTransport(),
                                jsonFactory, credential)
                                .setApplicationName(getString(R.string.app_name))
                                .build();

                        // The DriveService encapsulates all REST API and SAF functionality.
                        MainActivity.this.service = new DriveService(googleDriveService);
                        if (!MainActivity.this.debug)
                            MainActivity.this.loadData();
                        else
                            MainActivity.this.resetData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Snackbar message = Snackbar.make(MainActivity.this.background,
                                getString(R.string.download_failed_msg),
                                Snackbar.LENGTH_LONG);
                        message.show();
                    }
                });
    }

    /**
     * Handles case where Sign in to Google failed.
     */
    private void onSignInFailed() {
        this.startNoteScreen();
        Snackbar message = Snackbar.make(this.background, getString(R.string.sign_in_fail_msg),
                Snackbar.LENGTH_LONG);
        message.show();
    }

    /**
     * Loads data from Google.
     */
    private void loadData() {
        this.startLoadScreen(getString(R.string.loading_message));
        DataDownloadTask task = new DataDownloadTask(this.service, Collections.singletonList((
                DownloadDoneListener) this));
        task.execute();
    }

    @Override
    public void onDownloadComplete(DownloadedData downloadedData) {
        this.startNoteScreen();
        if (downloadedData.getResultCode() == DownloadedData.RESULTS_OK) {
            this.onDownloadSuccess(downloadedData.getSaveData());
        } else if (downloadedData.getResultCode() == DownloadedData.RESULTS_ERROR) {
            this.onDownloadFailed();
        } else {
            this.createNewNotes();
        }
    }

    /**
     * Handles case where data download failed.
     */
    private void onDownloadFailed() {
        Snackbar message = Snackbar.make(this.background, getString(R.string.download_failed_msg),
                Snackbar.LENGTH_LONG);
        message.show();
    }

    /**
     * Handles case where data download succeeded.
     * @param saveData The data.
     */
    private void onDownloadSuccess(SaveData saveData) {
        this.noteAdapter.setDisplayData(saveData);
        Snackbar message = Snackbar.make(this.background, getString(R.string.download_success_msg),
                Snackbar.LENGTH_LONG);
        message.show();
    }

    /**
     * Create a new empty Notepad.
     */
    private void createNewNotes() {

    }

    /**
     * Saves data onto Google appDataFolder.
     */
    private void saveData() {
        this.startLoadScreen(getString(R.string.saving_message));
        DataUploadTask task = new DataUploadTask(this.service, this.noteAdapter.getSaveData(),
                Collections.singletonList((UploadDoneListener) this));
        task.execute();
    }

    @Override
    public void onUploadComplete(boolean successful) {
        this.startNoteScreen();
        if (successful) {
            this.onUploadSuccess();
        } else {
            this.onUploadFailed();
        }
    }

    /**
     * Handle situation where data upload was successful.
     */
    private void onUploadSuccess() {
        Snackbar message = Snackbar.make(this.background, getString(R.string.upload_success_msg),
                Snackbar.LENGTH_LONG);
        message.show();
    }

    /**
     * Handles situation where data upload was not successful.
     */
    private void onUploadFailed() {
        Snackbar message = Snackbar.make(this.background, getString(R.string.upload_failed_msg),
                Snackbar.LENGTH_LONG);
        message.show();
    }

    /**
     * Sets the new text size.
     * @param textSize The requested size of the text.
     */
    private void setTextSize(String textSize) {
        if (textSize.equals(getString(R.string.text_size_tiny))) {
            this.noteAdapter.setTextSize(getResources().getInteger(R.integer.Tiny));
        } else if (textSize.equals(getString(R.string.text_size_small))) {
            this.noteAdapter.setTextSize(getResources().getInteger(R.integer.Small));
        } else if (textSize.equals(getString(R.string.text_size_medium))) {
            this.noteAdapter.setTextSize(getResources().getInteger(R.integer.Medium));
        } else if (textSize.equals(getString(R.string.text_size_large))) {
            this.noteAdapter.setTextSize(getResources().getInteger(R.integer.Large));
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // If user did not sign in yet, don't do anything.
        if (this.service == null) {
            this.signIn();
            return super.onOptionsItemSelected(item);
        }
        if (this.service.isBusy()) {
            return super.onOptionsItemSelected(item);
        }

        switch (id) {
            case R.id.action_sync:
                if (!this.service.isBusy())
                    this.loadData();
                break;

            case R.id.action_save:
                if (!this.service.isBusy())
                    this.saveData();
                break;

            case R.id.action_photo:
                this.startPhotoActivity();
                break;

            case R.id.action_reorder:
                this.startReorderActivity();
                break;

            case R.id.action_voice:
                this.startVoiceActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tools) {
            this.startSettingsActivity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Deletes all removed photos from the device.
     * @param oldData The old list of items.
     * @param newData The new list of items.
     */
    private void removeDeletedPhotos(ArrayList<ItemData> oldData, ArrayList<ItemData> newData) {
        ArrayList<ItemData> clone = (ArrayList<ItemData>) oldData.clone();
        // Ignore all items that were not removed.
        for (ItemData itemData : newData) {
            clone.remove(itemData);
        }
        // The items left have been removed by the user. Loop through them and delete images.
        for (ItemData itemData : clone) {
            if (itemData.getViewType() == ItemData.TYPE_PHOTO) {
//                this.getContentResolver().delete(Uri.parse(itemData.getData()), null, null);
                File file = new File(Uri.parse(itemData.getData()).getPath());
                file.delete();
            }
        }
    }

    /**
     * Method used to textarea that download and upload to Drive are working.
     */
    private void testDrive() {

        PrintFilesTask task2 = new PrintFilesTask(this.service);
        task2.execute();
        DataDownloadTask task3 = new DataDownloadTask(this.service,
                Collections.singletonList((DownloadDoneListener) this));
        task3.execute();
    }

    /**
     * Method used to fix the data stored on Google Drive by uploading data containing
     * empty String.
     */
    private void resetData() {
        ArrayList<ItemData> data = new ArrayList<>();
        data.add(new ItemData("Testing 123", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("", ItemData.TYPE_TEXT));
        data.add(new ItemData("Testing 123", ItemData.TYPE_TEXT));
        DataUploadTask task = new DataUploadTask(this.service, new SaveData(
                data, 16),
                Collections.singletonList((UploadDoneListener) this));
        task.execute();
    }
}
