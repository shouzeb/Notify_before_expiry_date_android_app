package com.example.bayan_oh.inspect;

/* From Inspect Team:
* This class is part of Android Character Recognition project,
* created by Fadi, licensed under The Code Project Open License (CPOL)
* and is permitted to be modified and used.
* Link to the Android Character Recognition article:
* http://www.codeproject.com/Tips/840623/Android-Character-Recognition
* */

 import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

 //Import local classes
import com.example.bayan_oh.inspect.Core.CameraEngine;
import com.example.bayan_oh.inspect.Core.ExtraViews.FocusBoxView;
import com.example.bayan_oh.inspect.Core.Imaging.Tools;
import com.example.bayan_oh.inspect.Core.TessTool.TessAsyncCallback;
import com.example.bayan_oh.inspect.Core.TessTool.TessAsyncEngine;



public class CameraScanActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener,
        Camera.PictureCallback, Camera.ShutterCallback, TessAsyncCallback {

    static final String TAG = "DBG_" + CameraScanActivity.class.getName();
    String recognizedDate;
    Button focusButton;
    Button captureButton;
    Button cancelButton;
    FocusBoxView focusBox;
    SurfaceView cameraFrame;
    CameraEngine cameraEngine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_scan);

    }
    // Create the camera surface view and open it
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.d(TAG, "Surface Created - starting camera");

        if (cameraEngine != null && !cameraEngine.isOn()) {
            cameraEngine.start();
        }

        if (cameraEngine != null && cameraEngine.isOn()) {
            Log.d(TAG, "Camera engine already on");
            return;
        }

        cameraEngine = CameraEngine.New(holder);
        cameraEngine.start();

        Log.d(TAG, "Camera engine started");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    // UI References
    @Override
    protected void onResume() {
        super.onResume();

        cameraFrame = (SurfaceView) findViewById(R.id.camera_frame); //Camera Preview
        focusBox = (FocusBoxView) findViewById(R.id.focus_box); //Resizable focus box
        focusButton = (Button) findViewById(R.id.focus_button); // Focus button
        captureButton = (Button) findViewById(R.id.capture_button); //Capture button
        cancelButton = (Button) findViewById(R.id.cancel_button); //Cancel button

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraFrame.setOnClickListener(this);
        focusButton.setOnClickListener(this);
        captureButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (cameraEngine != null && cameraEngine.isOn()) {
            cameraEngine.stop();
        }

        SurfaceHolder surfaceHolder = cameraFrame.getHolder();
        surfaceHolder.removeCallback(this);
    }

    @Override
    public void onClick(View v) {

        //Reguest camera focus if the Focus button is clicked
        if(v == focusButton){
            if(cameraEngine != null && cameraEngine.isOn()){
                cameraEngine.requestFocus();
            }
        }

        //Capture the expiration date if the Capture button is clicked
        if(v == captureButton){
            if(cameraEngine != null && cameraEngine.isOn()){
                cameraEngine.takeShot(this, this, this);
            }
        }

        //Cancel the scanning process if the Capture button is clicked and return to the home activity
        if(v == cancelButton){
            if(cameraEngine!=null && cameraEngine.isOn()){
                Intent h = new Intent(CameraScanActivity.this, MainActivity.class);
                startActivity(h);
            }
        }
    }

    //Get the result of the TessAsyncEngine
    @Override
    public void getResult(String output){

        recognizedDate = output;
    }

    //Start the OCR process after expiration date is captured
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        Log.d(TAG, "Picture taken");

        if (data == null) {
            Log.d(TAG, "Got null data");
            return;
        }

        //Get the captured expiration date from the focus box and convert it to bitmap
        Bitmap bmp = Tools.getFocusedBitmap(this, camera, data, focusBox.getBox());

        Log.d(TAG, "Got bitmap");
        Log.d(TAG, "Initialization of TessBaseApi");

        //Preform the OCR process to extract and recognize the expiration date
        new TessAsyncEngine(this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, this, bmp);

        //After OCR process is complete, go to Scanned Expiration Date activity
        Intent d = new Intent(CameraScanActivity.this, ScannedExpDateActivity.class);
        //Pass the recognized date to the activity
        d.putExtra("recognizedDate", recognizedDate);
        startActivity(d);

    }



    @Override
    public void onShutter() {}

}
