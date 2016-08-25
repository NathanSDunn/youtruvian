package com.nathansdunn.blendify.domain;

import android.os.Environment;
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

    private File folder;

    public PhotoSet(String id) {
        this.id = id;
    }

    private File getStorage() {
        String sdcard = System.getenv("SECONDARY_STORAGE");


        if ((sdcard == null) || (sdcard.length() == 0)) {
            sdcard = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        if (sdcard == null) {
            sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return new File(sdcard + subDir + id);
    }

    public void init() throws IOException {
        folder = getStorage();
        if (!folder.exists()) {
            //create folder
            if (!folder.mkdirs()) {
                throw new IOException(String.format("Unable to create folders: %s", folder.getAbsoluteFile()));
            } else {
                Log.d(TAG, "created directory:"+folder.getAbsoluteFile());
            }
        } else {
            throw new UnsupportedOperationException(String.format("Directory %d already exists", folder.getAbsoluteFile()));
        }
    }

    public File getPhoto(int id) throws IOException {
        // Create an image file name
        File file = new File(folder +"/photo_"+id+".jpg");
        if (!file.exists()) file.createNewFile();
        return file;
    }
}
