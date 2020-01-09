package com.example.bayan_oh.inspect;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class CategoriesActivity extends ActionBarActivity  {

    private int vid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Screen Width and Height
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        int quarterScreenWidth = (int)(screenWidth *0.25);

        // Create ScrollView
        ScrollView scrollView = new ScrollView(this);

        // Create GridLayout
        final GridLayout gridLayout = new GridLayout(this);

        // Creating LayoutParams
        gridLayout.setColumnCount(4);
        gridLayout.setBackgroundColor(Color.parseColor("#faede5"));
        gridLayout.setMinimumHeight(screenHeight);

        // Add GridLayout to ScrollView
        scrollView.addView(gridLayout);

        // Set ScrollView as a root element of the screen
        setContentView(scrollView);

        // Find all categories
        final DBHandler dbHandler = new DBHandler(this, null, null, 1);
        List<Category> categories = dbHandler.findAllCategories();

        // Set GridLayout Elements
        for (Category category : categories) {

            LinearLayout inlinearLayout = new LinearLayout(this);
            inlinearLayout.setOrientation(LinearLayout.VERTICAL);

            // Set Buttons
            LinearLayout.LayoutParams inLayout = new LinearLayout.LayoutParams(quarterScreenWidth-20, quarterScreenWidth-20);

            ImageButton btn = new ImageButton(this);
            btn.setId(category.getCID());
            btn.setLayoutParams(inLayout);
            btn.setBackgroundColor(Color.parseColor("#00000000"));
            btn.setBackgroundResource(getResources().getIdentifier(category.getIcon(), "drawable", getApplicationContext().getPackageName()));

            // Set Button Label
            inLayout = new LinearLayout.LayoutParams(quarterScreenWidth-20, 110);

            TextView info = new TextView(this);
            info.setText(category.getCName());
            info.setTextSize(15);
            info.setGravity(Gravity.CENTER);
            info.setLayoutParams(inLayout);
            // Set Font
            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/TypoGroteskDemo.otf");
            info.setTypeface(type);

            btn.setOnLongClickListener(new View.OnLongClickListener() {

                public boolean onLongClick(View v) {

                    vid = v.getId();

                    // Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(CategoriesActivity.this, v);
                    // Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_inner, popup.getMenu());

                    // Registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.delete)
                            {
                                Category category = dbHandler.findCategory(vid);
                                new AlertDialog.Builder(CategoriesActivity.this)
                                        .setTitle("Delete Category")
                                        .setMessage("Are you sure you want to delete " + category.getCName() + "?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Delete Category Products And Cancel Alarms
                                                List<Product> products = dbHandler.findAllProducts(vid); // Cancel Products Alarms
                                                for (Product product : products){
                                                    Intent intent = new Intent(CategoriesActivity.this, NotifyMeReceiver.class);
                                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(CategoriesActivity.this, product.getPID(), intent, 0);
                                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                                                    alarmManager.cancel(pendingIntent);
                                                }
                                                // Delete Category
                                                dbHandler.deleteCategory(vid); // Delete Category
                                                Toast.makeText(CategoriesActivity.this, "Category Deleted", Toast.LENGTH_SHORT).show();

                                                // Refresh
                                                finish();
                                                startActivity(getIntent());

                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .show();
                            }
                            if (item.getItemId() == R.id.edit)
                            {
                                // Go To Edit Category
                                Intent k = new Intent(CategoriesActivity.this, AddEditCategoryActivity.class);
                                k.putExtra("cid",vid);
                                startActivity(k);
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu

                    return true;
                }

            });

            btn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    // Go To Category
                    Intent k = new Intent(CategoriesActivity.this, CategoryActivity.class);
                    k.putExtra("cid", v.getId());
                    startActivity(k);

                }
            });

            // Add inner LinearLayout to GridLayout
            inlinearLayout.addView(btn);
            inlinearLayout.addView(info);
            inLayout = new LinearLayout.LayoutParams(quarterScreenWidth, quarterScreenWidth+120);
            gridLayout.addView(inlinearLayout, inLayout);

        }

        // Set Add Button
        ImageButton add = new ImageButton(this);
        add.setBackgroundResource(R.drawable.add_cat);
        LinearLayout.LayoutParams inLayout = new LinearLayout.LayoutParams(quarterScreenWidth-20, quarterScreenWidth-20);
        add.setLayoutParams(inLayout);

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Go To ADD Category
                Intent k = new Intent(CategoriesActivity.this, AddEditCategoryActivity.class);
                startActivity(k);

            }
        });

        // Add inner Linear Layout to GridLayout
        LinearLayout inlinearLayout = new LinearLayout(this);
        inlinearLayout.setOrientation(LinearLayout.VERTICAL);
        inlinearLayout.addView(add);
        inLayout = new LinearLayout.LayoutParams(quarterScreenWidth, quarterScreenWidth+50);
        gridLayout.addView(inlinearLayout, inLayout);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categories, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_category) {
            // Go To ADD Category
            Intent k = new Intent(CategoriesActivity.this, AddEditCategoryActivity.class);
            startActivity(k);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Set Back KeyCode
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
            // Go To Home
            Intent k = new Intent(CategoriesActivity.this, MainActivity.class);
            finish();
            startActivity(k);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
