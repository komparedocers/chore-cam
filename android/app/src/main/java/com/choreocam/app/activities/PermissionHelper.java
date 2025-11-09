package com.choreocam.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    private static final int PERMISSION_REQUEST_CODE = 1001;

    private final Activity activity;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public boolean hasMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_VIDEO)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
        } else {
            // Below Android 13
            return ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        }
    }

    public void requestMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            ActivityCompat.requestPermissions(activity,
                new String[]{
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                },
                PERMISSION_REQUEST_CODE);
        } else {
            // Below Android 13
            ActivityCompat.requestPermissions(activity,
                new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                PERMISSION_REQUEST_CODE);
        }
    }
}
