package com.nathansdunn.youtruvian.domain;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by nathan on 25/08/16.
 */
public class PhotoSet {
    private static final String TAG = "PhotoSet";
    private static final String subDir = "/DCIM/youtruvian/";
    private Gson gson = new Gson();

    private String id;
    private File folder;
    private HashMap<String, String> contact;

    public PhotoSet(String id) throws IOException {
        this.id = id;
        loadContact();
    }

    public void loadContact() throws IOException {
        contact = new HashMap<>();
        if (getContactFile().exists()) {
            HashMap<Integer, String> map = null;
            try
            {
                FileInputStream fis = new FileInputStream(getContactFile());
                ObjectInputStream ois = new ObjectInputStream(fis);
                contact = (HashMap) gson.fromJson(
                        (String) ois.readObject(),
                        new TypeToken<HashMap<String, String>>(){}.getType());
                ois.close();
                fis.close();
            } catch(ClassNotFoundException c)
            {
                System.out.println("Class not found");
                c.printStackTrace();
                return;
            }
        }
    }

    private File getContactFile() {
        return new File(getSetFolder() + "/contact.txt");
    }

    public void addContact(String key, String value) throws IOException {
        contact.put(key, value);
        saveContact();
    }

    private void saveContact() throws IOException {
        //http://beginnersbook.com/2013/12/how-to-serialize-hashmap-in-java/
        File file = getContactFile();
        if (file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(gson.toJson(contact));
        oos.close();
        fos.close();
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
                throw new IOException("Problem saving image: ", e);
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
