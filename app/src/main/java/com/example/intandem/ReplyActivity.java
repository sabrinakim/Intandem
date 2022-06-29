package com.example.intandem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ReplyActivity extends AppCompatActivity {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final String TAG = "ReplyActivity";
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private ImageView ivPictureReply;
    private Button btnReply;
    private ParseUser currUser;
    private Post currPost;
    private EditText etCaptionReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Bundle extras = getIntent().getExtras();
        currUser = extras.getParcelable("user");
        currPost = extras.getParcelable("post");

        ivPictureReply = findViewById(R.id.ivPictureReply);
        btnReply = findViewById(R.id.btnReply);
        etCaptionReply = findViewById(R.id.etCaptionReply);

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etCaptionReply.getText().toString();
                saveReply(currUser, photoFile, caption);
            }
        });

        launchCamera();

    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName); // this is where the taken photo will be stored.

        // wrap File object into a content provider
        // required for API >= 24 --> this is for security reasons
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider.intandem", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider); // --> we are telling the camera app where to store the photo that is taken.

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPictureReply.setImageBitmap(takenImage);

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos --> points to the image location.
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void saveReply(ParseUser currentUser, File photoFile, String caption) {
        Reply reply = new Reply();
        reply.setUser(currentUser);
        reply.setPicture(new ParseFile(photoFile));
        reply.setCaption(caption);

        reply.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                ivPictureReply.setImageResource(0);
                Log.i(TAG, "reply saved successfully");
            }
        });

        PostToReply postToReply = new PostToReply();
        postToReply.setPost(currPost);
        postToReply.setReply(reply);

        postToReply.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "post to reply relation saved successfully");
            }
        });
    }
}