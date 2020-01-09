package com.example.bayan_oh.inspect.Core.TessTool;

/* From Inspect Team:
* This class is part of Android Character Recognition project,
* created by Fadi, licensed under The Code Project Open License (CPOL)
* and is permitted to be modified and used.
* Link to the Android Character Recognition article:
* http://www.codeproject.com/Tips/840623/Android-Character-Recognition
* */
    /*This class calls the TesseractBaseApi under the Async class TessAsyncEngine
                to extract and recognize the expiration date*/

 import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.bayan_oh.inspect.Core.Imaging.Tools;


public class TessAsyncEngine extends AsyncTask<Object, Void, String> {

    static final String TAG = "DBG_" + TessAsyncEngine.class.getName();

    private Bitmap bmp;
    private Activity context;
    private TessAsyncCallback callback;
    private String day, month, year;

    public TessAsyncEngine(TessAsyncCallback c) {
        callback= c;
    }

    @Override
    protected String doInBackground(Object... params) {

        try {
            if(params.length < 2) {
                Log.e(TAG, "Error passing parameter to execute - missing params");
                return null;
            }
            if(!(params[0] instanceof Activity) || !(params[1] instanceof Bitmap)) {
                Log.e(TAG, "Error passing parameter to execute(context, bitmap)");
                return null;
            }

            context = (Activity)params[0];

            bmp = (Bitmap)params[1];
            if(context == null || bmp == null) {
                Log.e(TAG, "Error passed null parameter to execute(context, bitmap)");
                return null;
            }

            int rotate = 0;

            //Start to analyze and process the bitmap of the captured image, then extract the date
            if(params.length == 3 && params[2]!= null && params[2] instanceof Integer){
                rotate = (Integer) params[2];
            }

            if(rotate >= -180 && rotate <= 180 && rotate != 0)
            {
                bmp = Tools.preRotateBitmap(bmp, rotate);
                Log.d(TAG, "Rotated OCR bitmap " + rotate + " degrees");
            }

            TessEngine tessEngine =  TessEngine.Generate(context);
            bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
            String result = tessEngine.detectText(bmp);

            /*//Recognize date format then convert it to dd-mm-yyyy format
            //dd.mm.yyyy e.g. 01.06.2016
            //dd/mm/yyyy e.g. 01/06/2016
            //dd mm yyyy e.g. 01 06 2016
            if(result.trim().matches("^\\d{2}\\D\\d{2}\\D\\d{4}$")){
                result = result.trim();
                result = result.replaceAll("\\D", "-");
            }

            //dd/mm/yy e.g. 01/06/16
            //dd mm yy e.g. 01 06 16
            else if(result.trim().matches("^\\d{2}\\D\\d{2}\\D\\d{2}$")){
                result = result.trim();
                result = result.replaceAll("/", "-");
                day = result.substring(0,2);
                month = result.substring(2, 4);
                year = result.substring(4);
                year = "20"+year;
                result = day+"-"+month+"-"+year;
            }

            //mm.yyyy e.g. 06.2016
            //mm/yyyy e.g. 06/2016
            //mm yyyy e.g. 06 2016
            else if(result.trim().matches("^\\d{2}\\D\\d{4}$")){
                result = result.trim();
                result = result.replaceAll(".", "-");
                month = result.substring(0,2);
                year = result.substring(2);
                result = "01"+"-"+month+"-"+year;
            }

            //mm yy e.g. 06 16
            else if(result.trim().matches("^\\d{2}\\D\\d{2}$")){
                result = result.trim();
                month = result.substring(0,2);
                year = result.substring(2);
                year = "20"+year;
                result = "01"+"-"+month+"-"+year;
            }
            //mmm yyyy e.g.  AUG 2016 , this detects the months with 3-letter abbreviation
            else if(result.trim().matches("^[A-Z]{3}\\D\\d{4}$")){
                result = result.trim();
                month = result.substring(0,3);
                year = result.substring(3);
                month = month.toLowerCase();

                if(month == "jan")
                    month = "01";
                else if(month == "feb")
                    month = "02";
                else if(month == "mar")
                    month = "03";
                else if(month == "apr")
                    month = "04";
                else if(month == "may")
                    month = "05";
                else if(month == "aug")
                    month = "08";
                else if(month == "oct")
                    month = "10";
                else if(month == "nov")
                    month = "11";
                else if(month == "dec")
                    month = "12";

                result = "01"+"-"+month+"-"+year;
            }
            //mmm yyyy e.g.  AUG 2016 , this detects the months with 4-letter abbreviation
            else if(result.trim().matches("^[A-Z]{4}\\D\\d{4}$")){
                result = result.trim();
                month = result.substring(0,4);
                year = result.substring(4);
                month = month.toLowerCase();

                if(month == "june")
                    month = "06";
                else if(month == "july")
                    month = "07";
                else if(month == "sept")
                    month = "09";

                result = "01"+"-"+month+"-"+year;
            }*/

            Log.d(TAG, result);

            return result;

        } catch (Exception ex) {
            Log.d(TAG, "Error: " + ex + "\n" + ex.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        if(s == null || bmp == null || context == null)
            return;
        // Pass the "result" string to the interface calss
        callback.getResult(s);

        super.onPostExecute(s);
    }
}
