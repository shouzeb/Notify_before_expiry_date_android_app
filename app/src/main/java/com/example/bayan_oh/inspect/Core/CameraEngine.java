package com.example.bayan_oh.inspect.Core;

/* From Inspect Team:
* This class is part of Android Character Recognition project,
* created by Fadi, licensed under The Code Project Open License (CPOL)
* and is permitted to be modified and used.
* Link to the Android Character Recognition article:
* http://www.codeproject.com/Tips/840623/Android-Character-Recognition
* */

 import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;


public class CameraEngine {

    static final String TAG = "DBG_" + CameraUtils.class.getName();
    Camera camera;
    SurfaceHolder surfaceHolder;
    boolean on;

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {

        }
    };

    // This method is check whether the camera is on or not
    public boolean isOn() {
        return on;
    }

    private CameraEngine(SurfaceHolder surfaceHolder){
        this.surfaceHolder = surfaceHolder;
    }

    static public CameraEngine New(SurfaceHolder surfaceHolder){
        Log.d(TAG, "Creating camera engine");
        return  new CameraEngine(surfaceHolder);
    }

    // This method is to request the camera focus
    public void requestFocus() {
        if (camera == null)
            return;

        if (isOn()) {
            camera.autoFocus(autoFocusCallback);
        }
    }

    // This method is to open the camera
    public void start() {

        Log.d(TAG, "Entered CameraEngine - start()");
        this.camera = CameraUtils.getCamera();

        if (this.camera == null)
            return;

        Log.d(TAG, "Got camera hardware");

        try {

            this.camera.setPreviewDisplay(this.surfaceHolder);
            this.camera.setDisplayOrientation(90);
            this.camera.startPreview();

            on = true;

            Log.d(TAG, "CameraEngine preview started");

        } catch (IOException e) {
            Log.e(TAG, "Error in setPreviewDisplay");
        }
    }

    // This method is to stop the camera
    public void stop(){

        if(camera != null){
            camera.release();
            camera = null;
        }

        on = false;

        Log.d(TAG, "CameraEngine Stopped");
    }

    // This method is to capture an image
    public void takeShot(Camera.ShutterCallback shutterCallback,
                         Camera.PictureCallback rawPictureCallback,
                         Camera.PictureCallback jpegPictureCallback ){
        if(isOn()){
            camera.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
        }
    }
}
