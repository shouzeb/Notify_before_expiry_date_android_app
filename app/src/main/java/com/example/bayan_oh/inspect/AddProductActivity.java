package com.example.bayan_oh.inspect;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddProductActivity extends ActionBarActivity {

    private NumberPicker ynopicker;
    private NumberPicker mnopicker;
    private NumberPicker dnopicker;
    private Button add;
    private Button cancel;
    private ImageButton calendar;
    private ImageView ProductImage;
    private Bitmap pic;
    static final int REQUEST_IMAGE_CAPTURE=1;
    private EditText pname, xp ;
    private DatePickerDialog xpDatePickerDialog;
    private DBHandler dbHandler = new DBHandler(this, null, null, 1);
    private int cid = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Get Category ID form Previous Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cid = extras.getInt("cid");
        }
        else {

        }

        // Initialize Views
        add = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        calendar = (ImageButton) findViewById(R.id.btncalendar);
        pname = (EditText) findViewById(R.id._pname);
        xp = (EditText)findViewById(R.id._date);
        xp.setInputType(InputType.TYPE_NULL);
        ProductImage = (ImageView) findViewById(R.id.pimg);
        if(!hasCamera())
            ProductImage.setEnabled(false);

        // Set Number Pickers
        ynopicker=(NumberPicker)findViewById(R.id.ynumberPicker);
        ynopicker.setMaxValue(3);
        ynopicker.setMinValue(0);
        ynopicker.setWrapSelectorWheel(true);
        mnopicker=(NumberPicker)findViewById(R.id.mnumberPicker);
        mnopicker.setMaxValue(11);
        mnopicker.setMinValue(0);
        mnopicker.setWrapSelectorWheel(true);
        dnopicker=(NumberPicker)findViewById(R.id.dnumberPicker);
        dnopicker.setMaxValue(30);
        dnopicker.setMinValue(0);
        dnopicker.setWrapSelectorWheel(true);

        Drawable icon = getResources().getDrawable(android.R.drawable.ic_menu_gallery);
        pic = Bitmap.createScaledBitmap(((BitmapDrawable)icon).getBitmap(), 150, 150, false);

        // Date Picker Dialog
        Calendar newCalendar = Calendar.getInstance();
        xpDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                xp.setText(dateFormat.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Get Selected Notification Date
                int ynopickerValue = ynopicker.getValue();
                int mnopickerValue = mnopicker.getValue();
                int dnopickerValue = dnopicker.getValue();

                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(dateFormat.parse(xp.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Date xpdate = c.getTime();

                c.add(Calendar.DAY_OF_MONTH, -dnopickerValue);
                c.add(Calendar.MONTH, -mnopickerValue);
                c.add(Calendar.YEAR, -ynopickerValue);


                if (xp.getText().toString().trim().equals("") || xp.getText().toString().trim().isEmpty())
                { // Expiration Date is Empty
                    Toast.makeText(getApplicationContext(), "Expiration Date cannot be empty", Toast.LENGTH_LONG).show();
                }
                else if (xpdate.before(new Date()))
                { // Expiration Date has already ended
                    Toast.makeText(getApplicationContext(), "Expiration Date has already ended", Toast.LENGTH_LONG).show();
                }
                else if (c.getTime().before(new Date()))
                { // Notification Date has already ended
                    Toast.makeText(getApplicationContext(), "Notification Date has already ended", Toast.LENGTH_LONG).show();
                }
                else
                { // No Error: Add Product
                    if (pname.getText().toString().trim().equals("") || pname.getText().toString().trim().isEmpty())
                    { // Product Name is Empty
                        pname.setText("Product #"+String.valueOf(dbHandler.getNewProductID())); // Set to Default
                    }

                    // Add Product
                    Product product = new Product(dbHandler.getNewProductID(), pname.getText().toString(), xp.getText().toString(), dateFormat.format(c.getTime()), ynopickerValue+":"+mnopickerValue+":"+dnopickerValue, cid, getBytes(pic));
                    dbHandler.addProduct(product);

                    // Set Alarm
                    Intent myIntent = new Intent(AddProductActivity.this, NotifyMeReceiver.class);
                    myIntent.putExtra("pid", product.getPID());
                    myIntent.putExtra("pname", product.getPName());
                    myIntent.putExtra("xpdate", product.getXPDate());
                    myIntent.putExtra("ntdate", product.getNTDate());
                    myIntent.putExtra("remaining", product.getDiffDate());
                    myIntent.putExtra("image", product.getImage());
                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), product.getPID(), myIntent, 0);

                    String toParse = product.getNTDate() + " 00:00";

                    try {
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        Date date = formatter.parse(toParse);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
                    }
                    catch (ParseException e) {}


                    // Go To Category
                    Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                    i.putExtra("cid", cid);
                    Toast.makeText(getApplicationContext(), "Product added", Toast.LENGTH_SHORT).show();
                    finish();
                    startActivity(i);
                }

            }

        });

        calendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                xpDatePickerDialog.show();

            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Go To Category
                Intent k = new Intent(AddProductActivity.this, CategoryActivity.class);
                k.putExtra("cid", cid);
                finish();
                startActivity(k);

            }

        });

    }

    // Check id Device has Camera
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // Open Camera
    public void launchCamera(View view){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    // Convert Byte into Image
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    // After Camera Capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            // Get Image
            Bundle extras = data.getExtras();
            pic =(Bitmap) extras.get("data");

            // Resize Image
            Bitmap dstBmp;

            if (pic.getWidth() >= pic.getHeight()){

                dstBmp = Bitmap.createBitmap(
                        pic,
                        pic.getWidth()/2 - pic.getHeight()/2,
                        0,
                        pic.getHeight(),
                        pic.getHeight()
                );

            }else{

                dstBmp = Bitmap.createBitmap(
                        pic,
                        0,
                        pic.getHeight()/2 - pic.getWidth()/2,
                        pic.getWidth(),
                        pic.getWidth()
                );
            }

            // Set Image
            pic = dstBmp;
            ProductImage.setImageBitmap(pic);


        }
    }

    // Set Back KeyCode
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
            // Go To Category
            Intent k = new Intent(AddProductActivity.this, CategoryActivity.class);
            k.putExtra("cid", cid);
            finish();
            startActivity(k);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
