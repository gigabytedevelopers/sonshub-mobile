package com.gigabytedevelopersinc.apps.sonshub.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gigabytedevelopersinc.apps.sonshub.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Project - SonsHub
 * Created with Android Studio
 * Company: Gigabyte Developers
 * User: Emmanuel Nwokoma
 * Title: Founder and CEO
 * Day: Monday, 18
 * Month: February
 * Year: 2019
 * Date: 18 Feb, 2019
 * Time: 4:40 AM
 * Desc: SplashActivity
 **/
public class SplashActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    ArrayList<String> listPermissionsNeeded = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        // list of permissions
        listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(checkAndRequestPermissions()) {
            // Carry on with the normal flow, when permissions are granted.
            int SPLASH_TIME_OUT = 2000;
            new Handler().postDelayed(() -> {
                // This method will be executed once the timer is over
                // Start Main Activity

                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);

                // Close this Splash Screen
                finish();
            }, SPLASH_TIME_OUT);
        }
    }

    private  boolean checkAndRequestPermissions() {
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    listPermissionsNeeded.toArray(new String[0]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    /**
     * Get Results for Requested Permissions
     *
     * @param requestCode The Request Code
     * @param permissions The Permissions
     * @param grantResults The Results for the granted Permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String @NotNull [] permissions, @NotNull int @NotNull [] grantResults) {
        String TAG = "tag";
        Timber.tag(TAG).d("Permission callback called-------");
        if (requestCode == REQUEST_ID_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            // Initialize the map with both permissions
            // Fill with actual results from user
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                // Check for both permissions
                for (int i = 0; i < listPermissionsNeeded.size(); i++) {
                    if (perms.get(listPermissionsNeeded.get(i)) == PackageManager.PERMISSION_GRANTED) {
                        Timber.tag(TAG).d("storage permission granted");
                        // process the normal flow
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        //else any one or both the permissions are not granted
                    } else {
                        Timber.tag(TAG).d("Some permissions are not granted ask again ");
                        // Permission is denied (this is the first time, when "never ask again" is not checked)
                        // so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        // show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, listPermissionsNeeded.get(i))) {
                            showDialogOK((dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        checkAndRequestPermissions();
                                        break;
                                    case DialogInterface.BUTTON_NEGATIVE:
                                        // proceed with logic by disabling the related features or quit the app.
                                        finish();
                                        break;
                                }
                            });
                        } else {
                            // Permission is denied (and never ask again is checked)
                            // shouldShowRequestPermissionRationale will return false
                            Timber.tag("Go to settings").i("and enable permissions");
                            // Never ask again was clicked. So, explain why we need permissions
                            explain();
                            // Or proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    /**
     * Explain why the app needs permissions
     *
     * @param okListener The OK Button Listener
     */
    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("Service Permissions are required for this app")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("You need to give some mandatory permissions to continue. Do you want to go to app settings?")
                .setPositiveButton("Yes", (paramDialogInterface, paramInt) -> {
                    //  permissionsClass.requestPermission(type, code);
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:com.gigabytedevelopersinc.apps.sonshub"))
                    );
                })
                .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> finish());
        dialog.show();
    }
}