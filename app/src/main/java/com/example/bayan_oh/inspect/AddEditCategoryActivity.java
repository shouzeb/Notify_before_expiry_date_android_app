package com.example.bayan_oh.inspect;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class AddEditCategoryActivity extends ActionBarActivity {

    private EditText cname;
    private DBHandler dbHandler = new DBHandler(this, null, null, 1);
    private int cid = -1;
    private ImageView selectedIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        // Initialize Views
        cname = (EditText) findViewById(R.id.cattxt);
        selectedIcon = (ImageView) findViewById(R.id.other);
        selectedIcon.setBackgroundColor(Color.parseColor("#ffc19f"));

        // Get Category ID form Previous Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cid = extras.getInt("cid");
            Category category = dbHandler.findCategory(cid);
            setTitle("Edit Category: "+category.getCName());
            cname.setText(category.getCName());
            cname.setSelection(cname.getText().length());
            selectedIcon.setBackgroundColor(Color.parseColor("#00000000"));
            selectedIcon = (ImageView) findViewById(getResources().getIdentifier(category.getIcon(), "id", getApplicationContext().getPackageName()));
            selectedIcon.setBackgroundColor(Color.parseColor("#ffc19f"));
        }
        else {

        }


    }

    // Set Selected Icon On Click
    public void setSelectedIcon(View view){

        int vid = view.getId();

        // Toggle Selected Icon
        if (vid == selectedIcon.getId()) {
            view.setBackgroundColor(Color.parseColor("#00000000"));
            selectedIcon = (ImageView) findViewById(R.id.other);
        } else {
            selectedIcon.setBackgroundColor(Color.parseColor("#00000000"));
            view.setBackgroundColor(Color.parseColor("#ffc19f"));
            selectedIcon = (ImageView) findViewById(vid);
        }

    }

    // Add Category On Click
    public void addCategory(View view) {

        if (cname.getText().toString().trim().equals("") || cname.getText().toString().trim().isEmpty())
        { // Category Name is Empty
            Toast.makeText(getApplicationContext(), "Category Name cannot be empty", Toast.LENGTH_LONG).show();
        }
        else if (cid == -1) {
            // Add category
            Category category = new Category(dbHandler.getNewCategoryID(), cname.getText().toString(), getResources().getResourceEntryName(selectedIcon.getId()));
            dbHandler.addCategory(category);

            // Go To Categories
            Intent k = new Intent(AddEditCategoryActivity.this, CategoriesActivity.class);
            Toast.makeText(getApplicationContext(), "Category Added", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(k);
        }
        else {
            // Edit category
            Category category =
                    new Category(cid, cname.getText().toString(), getResources().getResourceEntryName(selectedIcon.getId()));

            boolean result = dbHandler.editCategory(category);

            // Go To Categories
            Intent k = new Intent(AddEditCategoryActivity.this, CategoriesActivity.class);
            Toast.makeText(getApplicationContext(), "Category Edited", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(k);
        }
    }

    // Set Back KeyCode
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
            // Go To Categories
            Intent k = new Intent(AddEditCategoryActivity.this, CategoriesActivity.class);
            finish();
            startActivity(k);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
