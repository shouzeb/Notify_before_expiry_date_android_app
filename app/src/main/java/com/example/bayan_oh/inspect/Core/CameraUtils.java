package com.example.bayan_oh.inspect.Core;

/* From Inspect Team:
* This class is part of Android Character Recognition project,
* created by Fadi, licensed under The Code Project Open License (CPOL)
* and is permitted to be modified and used.
* Link to the Android Character Recognition article:
* http://www.codeproject.com/Tips/840623/Android-Character-Recognition
* */
    /*This class to get the available camera*/

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;


public class CameraUtils {

    static final String TAG = "DBG_ " + CameraUtils.class.getName();

    //Check if the device has a camera
    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    //Get available camera
    public static Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "Cannot getCamera()");
            return null;
        }
    }
}
