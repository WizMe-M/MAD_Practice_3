package com.example.practice_3;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;

    Button btnCapturePhoto;
    Button btnCaptureVideo;

    ImageView viewPhoto;
    VideoView viewVideo;

    ActivityResultLauncher<Intent> builtinPhotoActivity;
    ActivityResultLauncher<Intent> builtinVideoActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        btnCaptureVideo = findViewById(R.id.btnCaptureVideo);
        viewPhoto = findViewById(R.id.photoView);
        viewVideo = findViewById(R.id.videoView);

        btnCapturePhoto.setOnClickListener(this::capturePhoto);
        btnCaptureVideo.setOnClickListener(this::askPermissionAndCaptureVideo);

        builtinPhotoActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap bitmap = (Bitmap) extras.get("data");
                        viewPhoto.setVisibility(View.VISIBLE);
                        viewVideo.setVisibility(View.INVISIBLE);
                        viewPhoto.setImageBitmap(bitmap);
                    }
                });

        builtinVideoActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri videoUri = data.getData();
                        viewVideo.setVisibility(View.VISIBLE);
                        viewPhoto.setVisibility(View.INVISIBLE);
                        viewVideo.setVideoURI(videoUri);
                        viewVideo.start();
                    }
                });
    }

    private void capturePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        builtinPhotoActivity.launch(intent);
    }


    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        builtinVideoActivity.launch(intent);
    }

    private void askPermissionAndCaptureVideo(View view) {

        if (android.os.Build.VERSION.SDK_INT >= 23) {

            int readPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            int writePermission = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED
                    || readPermission != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE },
                        REQUEST_ID_READ_WRITE_PERMISSION);
                return;
            }
        }

        captureVideo();
    }
}