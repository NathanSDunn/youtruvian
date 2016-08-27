package com.nathansdunn.youtruvian.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.nathansdunn.youtruvian.R;
import com.nathansdunn.youtruvian.domain.BlendService;
import com.nathansdunn.youtruvian.domain.PhotoSet;
import com.nathansdunn.youtruvian.domain.RequestCode;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoBlendActivity extends AppCompatActivity {
    private static final String TAG = "PhotoBlendActivity";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String ACTIVEBUTTON_KEY = "activebutton";

    private Picasso picasso;
    private BlendService blendService = new BlendService();

    private ActionBar actionBar;
    private ImageView imageView;
    private FloatingActionButton fab;

    private PhotoSet photoSet;
    private String timeStamp;
    private int activeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRestoreInstanceState(savedInstanceState);
        setContentView(R.layout.activity_photoblend);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        getMenuInflater().inflate(R.menu.menu_photoblend, toolbar.getMenu());

        //set up fab camera click button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        //set up picasso
        picasso = new Picasso.Builder(this).build();

        //request permissions
        requestPerms();

        //set up camera window
        imageView = (ImageView) findViewById(R.id.imageview);

        if (timeStamp == null) {
            reset();
        } else {
            loadPhotoSet();
        }
    }

    private void requestPerms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    RequestCode.CAMERA_PERMS.getValue());
        }
    }

    private void toast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        Log.d(TAG, text);
    }

    private int getPhotoId() {
        if (activeButton == R.id.action_pic1) return 1;
        else if (activeButton == R.id.action_pic2) return 2;
        return 3;
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = photoSet.getPhotoFile(getPhotoId());
                Uri photoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, RequestCode.TAKE_PHOTO.getValue());
            } catch (IOException e) {
                toast("Unable to load photo #"+getPhotoId()+" file: "+e.getMessage());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.TAKE_PHOTO.getValue() && resultCode == RESULT_OK) {
            displayImage();
        }
    }

    private void displayImage() {
        try {
            File photo = photoSet.getPhotoFile(getPhotoId());
            String path = photoSet.getTestPhotoPath(getPhotoId());
            photo = new File(path);
            displayImage(photo);
        } catch (IOException e) {
            toast("Unable to display image #"+getPhotoId()+":"+e.getMessage());
        }
    }

    private void displayImage(File image) {
        toast("Loading:"+image.getAbsoluteFile());
        Picasso.with(this)
                .load(image)
                .into(imageView);
    }

    private void blend(int alpha) {
        toast("Blending...");
        try {
            Bitmap B = blendService.combineImages(
                    photoSet.getPhotoPath(1),
                    photoSet.getPhotoPath(2),
                    alpha);
            imageView.setImageBitmap(B);
            saveBlended(B);
        } catch (IOException e) {
            toast(e.getMessage());
        }
    }

    private void saveBlended(Bitmap blended) {
        try {
            photoSet.savePhoto(3, blended);
        } catch (IOException e) {
            toast("Unabled to save blended image:"+ e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photoblend, menu);
        actionBar.setDisplayShowTitleEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        activeButton = item.getItemId();
        enableCamera(activeButton == R.id.action_pic1 || activeButton == R.id.action_pic2);
        displayImage();
        //setImageViewVisibility(activeButton);

        switch (item.getItemId()) {
            case R.id.action_pic1: return true;
            case R.id.action_pic2: return true;
            case R.id.action_blend: blend(128); return true;
            case R.id.action_contact: toast("contact"); return true;
            case R.id.action_save: reset(); return true;
        }
        return false;
        //return super.onOptionsItemSelected(item);
    }

    private void enableCamera(boolean enabled) {
        if (enabled) fab.setVisibility(View.VISIBLE);
        else fab.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putString(TIMESTAMP_KEY, timeStamp);
        bundle.putInt(ACTIVEBUTTON_KEY, activeButton);
        super.onSaveInstanceState(bundle);
    }

    private void loadPhotoSet() {
        try {
            photoSet = new PhotoSet(timeStamp);
            photoSet.init();
        } catch (Exception e) {
            toast(e.getMessage());
        }
        displayImage();
    }

    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        } else {
            timeStamp = bundle.getString(TIMESTAMP_KEY);
        }

        activeButton = bundle.getInt(ACTIVEBUTTON_KEY);
        if (activeButton == 0) activeButton = R.id.action_pic1;
        super.onRestoreInstanceState(bundle);
    }

    private void reset() {
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        toast("New photo set:"+timeStamp);
        activeButton = R.id.action_pic1;

        View pic2 = findViewById(R.id.action_pic2);
        View blend = findViewById(R.id.action_blend);
        if (pic2 != null) pic2.setVisibility(View.INVISIBLE);
        if (blend != null) blend.setVisibility(View.INVISIBLE);
        loadPhotoSet();
    }
}
