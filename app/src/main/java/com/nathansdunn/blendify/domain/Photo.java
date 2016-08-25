package com.nathansdunn.blendify.domain;


import java.io.File;

public class Photo {
    private static final String TAG = "Photo";
    public int id;
    public File file;
    public boolean saved = false;

    public Photo(int id, File file) {
        this.id = id;
        this.file = file;
    }

    public String getPath() {
        return String.format("file:%s", file.getAbsolutePath());
    }
}
