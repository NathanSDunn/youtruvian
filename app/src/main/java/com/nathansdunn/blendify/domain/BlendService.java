package com.nathansdunn.blendify.domain;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BlendService {
    public static Bitmap combineImages(Bitmap c, Bitmap s) {
        //see http://stackoverflow.com/questions/2738834/combining-two-png-files-in-android
        // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth();
            height = c.getHeight() + s.getHeight();
        } else {
            width = s.getWidth();
            height = c.getHeight() + s.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, c.getHeight(), null);

        return cs;
    }
}
