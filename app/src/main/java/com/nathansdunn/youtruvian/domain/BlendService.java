package com.nathansdunn.youtruvian.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BlendService {
    public Bitmap combineImages(String pathX, String pathT, int alpha) throws IOException {
        return combineImages(
                BitmapFactory.decodeFile(pathX),
                BitmapFactory.decodeFile(pathT),
                alpha);
    }

    public Bitmap combineImages(Bitmap c, Bitmap s, int alpha) throws IOException {
        //see http://stackoverflow.com/questions/2738834/combining-two-png-files-in-android
        // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom

        //see http://android-er.blogspot.com/2013/08/merge-two-image-overlap-with-alpha.html

        if (c == null) {
            throw new IOException("First 'X' Image must be taken!");
        }
        if (s == null) {
            throw new IOException("Second 'T' Image must be taken!");
        }

        int width = 0, height = 0;

        /*

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth();
            height = c.getHeight() + s.getHeight();
        } else {
            width = s.getWidth();
            height = c.getHeight() + s.getHeight();
        }*/

        if (c != null) {
            width = c.getWidth();
            height = c.getHeight();
        }

        Bitmap cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        Paint paint = new Paint();
        paint.setAlpha(alpha); //transparency
        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, 0f, 0f, paint);

        return cs;
    }

    public Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public Observable<Bitmap> loadBitmap(final Picasso picasso, final String imageUrl) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {

                final Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        subscriber.onNext(bitmap);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        subscriber.onError(new Exception("failed to load " + imageUrl));
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };
                subscriber.add(new Subscription() {
                    private boolean unSubscribed;

                    @Override
                    public void unsubscribe() {
                        picasso.cancelRequest(target);
                        unSubscribed = true;
                    }

                    @Override
                    public boolean isUnsubscribed() {
                        return unSubscribed;
                    }
                });
                picasso.load(imageUrl).into(target);
            }
        })
        .observeOn(Schedulers.io())
        .subscribeOn(AndroidSchedulers.mainThread());
    }
}
