package com.nathansdunn.blendify.domain;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by nathan on 25/08/16.
 */
public class PhotoSet {
    private static final String TAG = "PhotoSet";
    private static final String subDir = "/DCIM/youtruvian/";

    private String id;
    private Photo photo1;
    private Photo photo2;
    private Photo photo3;

    private File cameraDataDir;

    public PhotoSet(String id) {
        this.id = id;
    }

    private File getStorage() {
        String sdcard = System.getenv("SECONDARY_STORAGE");
        String state = Environment.getExternalStorageState();
        String[] projection = new String[]{MediaStore.Images.ImageColumns._ID,MediaStore.Images.ImageColumns.DATA,MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images.ImageColumns.MIME_TYPE};
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;


        if ((sdcard == null) || (sdcard.length() == 0)) {
            sdcard = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        if (sdcard == null) {
            sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return new File(sdcard + subDir + id);
    }

    public void init() throws IOException {
        File folder = getStorage();
        if (!folder.exists()) {
            //create folder
            if (!folder.mkdirs()) {
                throw new IOException(String.format("Unable to create folders: %s", folder.getAbsoluteFile()));
            } else {
                Log.d(TAG, "created directory:"+folder.getAbsoluteFile());
            }

            //create temp files
            try {
                photo1 = tempPhoto(1);
                photo2 = tempPhoto(2);
                photo3 = tempPhoto(3);
            } catch (IOException e) {
                throw new IOException(String.format("Unable to create temp photos: %s", e.getMessage()));
            }
        } else {
            throw new UnsupportedOperationException(String.format("Directory %d already exists", folder.getAbsoluteFile()));
        }
    }

    private Photo tempPhoto(int id) throws IOException {
        // Create an image file name
        File file = File.createTempFile(
                String.format("photo%d",id),  /* prefix */
                ".jpg",         /* suffix */
                cameraDataDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return new Photo(id, file);
    }
}
