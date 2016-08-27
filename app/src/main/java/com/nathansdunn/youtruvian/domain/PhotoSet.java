package com.nathansdunn.youtruvian.domain;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

    private File getSetFolder() {
        return new File(getPath() + id);
    }

    private String getPath() {

        String sdcard = System.getenv("SECONDARY_STORAGE");

        if ((sdcard == null) || (sdcard.length() == 0)) {
            sdcard = System.getenv("EXTERNAL_SDCARD_STORAGE");
        }
        if (sdcard == null) {
            sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return sdcard + subDir;
    }

    public void init() throws IOException {
        folder = getSetFolder();
        if (!folder.exists()) {
            //create folder
            if (!folder.mkdirs()) {
                throw new IOException(String.format("Unable to create folders: %s", folder.getAbsoluteFile()));
            } else {
                Log.d(TAG, "created directory:"+folder.getAbsoluteFile());
            }
        }
    }

    public File getPhotoFile(int id) throws IOException {
        // Create an image file name
        File file = new File(getPhotoPath(id));
        if (!file.exists()) file.createNewFile();
        return file;
    }

    public String getPhotoPath(int id) {
        return folder +"/photo_"+id+".jpg";
    }

    public void savePhoto(int id, Bitmap bitmap) throws IOException {
        String loc = getPhotoFile(id).getAbsolutePath();
        if (loc != null) {
            try {
                OutputStream os = new FileOutputStream(loc);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            } catch(IOException e) {
                throw new IOException("Problem combining images: ", e);
            }
        }
    }

    public String getTestPhotoPath(int id) {
        return getPath() + id + ".jpg";
    }

    public File getTestPhotoFile(int id) {
        return new File(getPhotoPath(id));
    }
}
