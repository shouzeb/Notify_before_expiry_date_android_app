package com.example.bayan_oh.inspect.Core.TessTool;

/* From Inspect Team:
* This class is part of Android Character Recognition project,
* created by Fadi, licensed under The Code Project Open License (CPOL)
* and is permitted to be modified and used.
* Link to the Android Character Recognition article:
* http://www.codeproject.com/Tips/840623/Android-Character-Recognition
* */
     /* This class handles the tess-two library storing and retrieving */

 import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;


public class TessEngine {

    static final String TAG = "DBG_" + TessEngine.class.getName();
    private Context context;

    private TessEngine(Context context){
        this.context = context;
    }

    public static TessEngine Generate(Context context) {
        return new TessEngine(context);
    }

    public String detectText(Bitmap bitmap) {

        Log.d(TAG, "Initialization of TessBaseApi");

        TessDataManager.initTessTrainedData(context);
        TessBaseAPI tessBaseAPI = new TessBaseAPI();

        String path = TessDataManager.getTesseractFolder();
        Log.d(TAG, "Tess folder: " + path);

        tessBaseAPI.setDebug(true);
        //Init the Tess with the trained data file, with english language
        tessBaseAPI.init(path, "eng");

        //Setting the characters to be detected
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "1234567890/.-abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");
        tessBaseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=[]}{;:'\"\\|~`,<>?");
        tessBaseAPI.setPageSegMode(TessBaseAPI.OEM_TESSERACT_CUBE_COMBINED);
        Log.d(TAG, "Ended initialization of TessEngine");
        Log.d(TAG, "Running inspection on bitmap");

        tessBaseAPI.setImage(bitmap);

        String inspection = tessBaseAPI.getUTF8Text();

        Log.d(TAG, "Got data: " + inspection);
        tessBaseAPI.end();
        System.gc();

        return inspection;
    }

}
