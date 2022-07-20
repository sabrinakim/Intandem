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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.intandem.fragments.PostsFragment;
import com.example.intandem.models.CustomPlace;
import com.example.intandem.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ComposePictureActivity extends AppCompatActivity {

    public static final String TAG = "ComposePictureActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private File photoFile;
    private String photoFileName = "photo.jpg";
    private ImageView ivImage;
    private EditText etCaption;
    private Button btnShare;
//    private String event;
    private ParseUser user;
    private String duration;
    private String timeUnit;
    private CustomPlace customPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_picture);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ivImage = findViewById(R.id.ivImage);
        etCaption = findViewById(R.id.etCaption);
        btnShare = findViewById(R.id.btnShare);

        launchCamera();

        // unwrap parcel here
        Bundle extras = getIntent().getExtras();
        customPlace = extras.getParcelable("customPlace");
//        event = extras.getString("event");
//        Log.i(TAG, event);
        user = extras.getParcelable("user"); // user is null rn
        duration = extras.getString("duration");

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caption = etCaption.getText().toString();
                etCaption.setText(caption);
                savePost(user, customPlace, etCaption.getText().toString(), photoFile, duration);
            }
        });
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
                ivImage.setImageBitmap(takenImage);

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

    private void savePost(ParseUser currentUser, CustomPlace customPlace, String caption,
                          File photoFile, String duration) {
        Post post = new Post();
        post.setUser(currentUser);
//        post.setEvent(event);
        post.setCustomPlace(customPlace);
        post.setPicture(new ParseFile(photoFile));
        post.setCaption(caption);
        post.setUserFbId(currentUser.getString("fbId"));

        Calendar rightNow = Calendar.getInstance();
//        if (timeUnit.equals("Minute(s)")) {
//            rightNow.add(Calendar.MINUTE, Integer.parseInt(duration));
//        } else {
//            rightNow.add(Calendar.HOUR, Integer.parseInt(duration));
//        }

        double fraction = Double.parseDouble(duration) % 1;
        int minutes = (int) (fraction * 60);
        int hours = (int) Math.floor(Double.parseDouble(duration));
        rightNow.add(Calendar.MINUTE, minutes);
        rightNow.add(Calendar.HOUR, hours);

        Date expiration = rightNow.getTime();
        post.setExpiration(expiration);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "error while saving", e);
                }
                Log.i(TAG, "post save was successful!");
                etCaption.setText("");
                ivImage.setImageResource(0);
                Intent i = new Intent(ComposePictureActivity.this, MainActivity.class);
                i.putExtra("post", post);
                startActivity(i);
            }
        });
    }
}