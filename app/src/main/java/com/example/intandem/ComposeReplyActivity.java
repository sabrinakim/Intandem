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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intandem.models.Post;
import com.example.intandem.models.Reply;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ComposeReplyActivity extends AppCompatActivity {

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public static final String TAG = "ComposeReplyActivity";
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private ImageView ivPictureReply;
    private ImageButton btnReply;
    private ParseUser currUser;
    private Post currPost;
    private EditText etCaptionReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_reply);

        Bundle extras = getIntent().getExtras();
        currUser = extras.getParcelable("user");
        currPost = extras.getParcelable("post");

        ivPictureReply = findViewById(R.id.ivPictureReply);
        btnReply = findViewById(R.id.btnBack);
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
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider.intandem", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider); // --> we are telling the camera app where to store the photo that is taken.

        if (intent.resolveActivity(getPackageManager()) != null) {
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
                ivPictureReply.setImageBitmap(takenImage);

            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
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
        reply.setPost(currPost);

        reply.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "reply saved successfully");
                Log.d(TAG, String.valueOf(currPost.getCommentCount() + 1));
                currPost.setCommentCount(currPost.getCommentCount() + 1);
                currPost.saveInBackground();
                ivPictureReply.setImageResource(0);
                etCaptionReply.setText("");
                Intent i = new Intent(ComposeReplyActivity.this, ViewRepliesActivity.class);
                i.putExtra("currPost", currPost);
                startActivity(i);
            }
        });
    }
}