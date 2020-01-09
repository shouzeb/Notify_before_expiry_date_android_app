package com.example.bayan_oh.inspect;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;


import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class EditProductActivity extends ActionBarActivity {

    private Spinner spinner;
    private NumberPicker ynopicker;
    private NumberPicker mnopicker;
    private NumberPicker dnopicker;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private Button edit;
    private Button cancel;
    private ImageView ProductImage;
    private Bitmap pic;
    static final int REQUEST_IMAGE_CAPTURE=1;
    private EditText pname;
    private EditText cat_name;
    private int pid = -1;
    private DBHandler dbHandler = new DBHandler(this, null, null, 1);
    private Product product;
    private Product editedproduct;
    private Category category;
    private int selectedCategory;
    private PendingIntent pendingIntent;
    private String scannedXpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        //Initialize views
        edit = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        pname = (EditText) findViewById(R.id._pname);
        spinner = (Spinner) findViewById(R.id._cat);
        cat_name = (EditText)findViewById(R.id.Category_name);
        ProductImage = (ImageView) findViewById(R.id.pimg);
        if(!hasCamera())
            ProductImage.setEnabled(false);

        Drawable icon = getResources().getDrawable(android.R.drawable.ic_menu_gallery);
        pic = Bitmap.createScaledBitmap(((BitmapDrawable)icon).getBitmap(), 150, 150, false);

        addItemsOnSpinner(dbHandler);


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


        // Get Product ID form Previous Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (getIntent().hasExtra("expDate"))
            { // Fill Info Mode
                scannedXpdate = extras.getString("expDate");
                setTitle("Fill Product Information: ");
            }
            else
            { // Edit Mode
                pid = extras.getInt("pid");
                product = dbHandler.findProduct(pid);
                category = dbHandler.findCategory(product.getCID());
                setTitle("Edit Product: "+product.getPName());
                // Set Product Information
                pname.setText(product.getPName());
                pname.setSelection(pname.getText().length());
                spinner.setSelection(getIndex(spinner, category.getCName()));

                String[] separated = product.getDiffDate().split(":");

                ynopicker.setValue(Integer.valueOf(separated[0]));
                mnopicker.setValue(Integer.valueOf(separated[1]));
                dnopicker.setValue(Integer.valueOf(separated[2]));

                pic = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
                ProductImage.setImageBitmap(pic);
            }

        }
        else {

        }




        // On Custom Category Item Click
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView adapter, View v, int i, long lng) {

                if (adapter.getSelectedItemPosition() == spinner.getCount()-1) // Custom Item is selected
                {
                    cat_name.setVisibility(View.VISIBLE);
                    edit.setTop(R.id.title);
                }
                else
                { cat_name.setVisibility(View.GONE); }
                // Toggle Category Name textview visibility if custom item selected
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {

            }
        });


        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Get Selected Notification Date
                int ynopickerValue = ynopicker.getValue();
                int mnopickerValue = mnopicker.getValue();
                int dnopickerValue = dnopicker.getValue();

                Calendar c = Calendar.getInstance();
                try {
                    if (scannedXpdate == null) // Edit Mode
                        c.setTime(dateFormat.parse(product.getXPDate()));
                    else // Fill Info Mode
                        c.setTime(dateFormat.parse(scannedXpdate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                c.add(Calendar.DAY_OF_MONTH, -dnopickerValue);
                c.add(Calendar.MONTH, -mnopickerValue);
                c.add(Calendar.YEAR, -ynopickerValue);


                // get Selected Category
                int position = spinner.getSelectedItemPosition();
                List<Category> categories = dbHandler.findAllCategories();
                List<Integer> list = new ArrayList<Integer>();

                for (Category category : categories)
                {
                    list.add(category.getCID());
                }
                list.add(dbHandler.getNewCategoryID());

                if (spinner.getSelectedItemPosition() == spinner.getCount()-1 && (cat_name.getText().toString().trim().equals("") || cat_name.getText().toString().trim().isEmpty()))
                { // Custom Category Name is Empty
                    Toast.makeText(getApplicationContext(), "Category Name cannot be empty", Toast.LENGTH_LONG).show();
                }
                else if (c.getTime().before(new Date()))
                { // Notification date has already ended
                    Toast.makeText(getApplicationContext(), "Notification Date has already ended", Toast.LENGTH_LONG).show();
                }
                else
                { // No Error: Edit Product
                    if (pname.getText().toString().trim().equals("") || pname.getText().toString().trim().isEmpty())
                    { // Product Name is Empty
                        if (scannedXpdate == null)
                            pname.setText("Product #"+String.valueOf(pid)); // Set to Default
                        else
                            pname.setText("Product #"+String.valueOf(dbHandler.getNewProductID())); // Set to Default

                    }

                    if (position == spinner.getCount()-1)
                    { // Custom Category is Selected
                        Category newCategory = new Category(dbHandler.getNewCategoryID(), cat_name.getText().toString().trim(), "other");
                        dbHandler.addCategory(newCategory); // Add New Category
                    }

                    // Set Selected Category
                    Integer[] array = list.toArray(new Integer[spinner.getCount()]);
                    selectedCategory = array[position];

                    if (scannedXpdate != null)
                    { // Fill Info Mode
                        // Add Product
                        editedproduct = new Product(dbHandler.getNewProductID(), pname.getText().toString(), scannedXpdate, dateFormat.format(c.getTime()), ynopickerValue+":"+mnopickerValue+":"+dnopickerValue, selectedCategory, getBytes(pic));
                        dbHandler.addProduct(editedproduct);
                    }
                    else
                    { // Edit Mode
                        // Update Product
                        editedproduct = new Product(pid, pname.getText().toString(), product.getXPDate(), dateFormat.format(c.getTime()), ynopickerValue + ":" + mnopickerValue + ":" + dnopickerValue, selectedCategory, getBytes(pic));
                        boolean result = dbHandler.editProduct(editedproduct);
                    }

                    // Set Alarm
                    Intent myIntent = new Intent(EditProductActivity.this, NotifyMeReceiver.class);
                    myIntent.putExtra("pid", editedproduct.getPID());
                    myIntent.putExtra("pname", editedproduct.getPName());
                    myIntent.putExtra("xpdate", editedproduct.getXPDate());
                    myIntent.putExtra("ntdate", editedproduct.getNTDate());
                    myIntent.putExtra("remaining", editedproduct.getDiffDate());
                    myIntent.putExtra("image", editedproduct.getImage());
                    pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), editedproduct.getPID(), myIntent, 0);

                    String toParse = editedproduct.getNTDate() + " 00:00";

                    try {
                        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                        Date date = formatter.parse(toParse);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
                    }
                    catch (ParseException e) {}


                    // Go To Category
                    Intent i = new Intent(EditProductActivity.this, CategoryActivity.class);
                    i.putExtra("cid", selectedCategory);
                    if(scannedXpdate == null){
                        Toast.makeText(getApplicationContext(), "Product Edited", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                    startActivity(i);
                }

            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (scannedXpdate == null)
                { // Edit Mode
                    // Go To Category
                    Intent k = new Intent(EditProductActivity.this, CategoryActivity.class);
                    k.putExtra("cid", product.getCID());
                    finish();
                    startActivity(k);
                }
                else
                { // Fill Info Mode
                    // Go To Home
                    Intent k = new Intent(EditProductActivity.this, MainActivity.class);
                    finish();
                    startActivity(k);
                }

            }

        });
    }

    // Add Category Names to Spinner
    public void addItemsOnSpinner(DBHandler dbHandler) {

        List<Category> categories = dbHandler.findAllCategories();

        List<String> list = new ArrayList<String>();

        for (Category category : categories)
        {
            list.add(category.getCName());
        }
        list.add("Custom");

        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(dataAdapter);
        if(spinner.getSelectedItem().toString().trim().equalsIgnoreCase("custom")) {
            cat_name.setCursorVisible(true);
            spinner.setEnabled(false);}

    }

    // Find index of Category in Spinner
    private int getIndex(Spinner spinner, String myString)
    {
        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                index = i;
                break;
            }
        }
        return index;
    }

    // Check if Device has Camera
    private boolean hasCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    // Open Camera
    public void launchCamera(View view){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    // Convert Bytes into Image
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    // After Camera Capture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            //Get the photo
            Bundle extras = data.getExtras();
            pic =(Bitmap) extras.get("data");
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



            pic = dstBmp;
            ProductImage.setImageBitmap(pic);
        }
    }

    // Set Back KeyCode
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed

            if (scannedXpdate == null)
            { // Edit Mode
                // Go To Category
                Intent k = new Intent(EditProductActivity.this, CategoryActivity.class);
                k.putExtra("cid", product.getCID());
                finish();
                startActivity(k);
                return true;
            }
            else
            { // Fill Info Mode
                // Go To Scanned Expiration Date
                Intent k = new Intent(EditProductActivity.this, ScannedExpDateActivity.class);
                k.putExtra("recognizedDate", scannedXpdate);
                finish();
                startActivity(k);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
