package com.nathansdunn.blendify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.nathansdunn.blendify.domain.PhotoSet;
import com.nathansdunn.blendify.domain.RequestCode;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "PhotoBlendActivity";

    private ActionBar actionBar;
    private int activeButton = R.id.action_pic1;
    private ImageView imageView;
    private FloatingActionButton fab;

    private PhotoSet photoSet;
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        getMenuInflater().inflate(R.menu.menu_main, toolbar.getMenu());

        //set up fab camera click button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        //request permissions
        requestPerms();

        //set up camera window
        imageView = (ImageView) findViewById(R.id.imageview);

        try {
            photoSet = new PhotoSet(timeStamp);
            photoSet.init();
        } catch (Exception e) {
            toast(e.getMessage());
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

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RequestCode.TAKE_PHOTO.getValue());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        actionBar.setDisplayShowTitleEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        activeButton = item.getItemId();
        enableCamera(activeButton == R.id.action_pic1 || activeButton == R.id.action_pic2);

        switch (item.getItemId()) {
            case R.id.action_pic1: toast("1"); return true;
            case R.id.action_pic2: toast("2"); return true;
            case R.id.action_blend: toast("blend"); return true;
            case R.id.action_contact: toast("contact"); return true;
            case R.id.action_save: toast("save"); return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void enableCamera(boolean enabled) {
        if (enabled) fab.setVisibility(View.VISIBLE);
        else fab.setVisibility(View.INVISIBLE);
    }
}
