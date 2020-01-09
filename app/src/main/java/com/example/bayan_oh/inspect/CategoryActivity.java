package com.example.bayan_oh.inspect;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class CategoryActivity extends ActionBarActivity {

    private DBHandler dbHandler = new DBHandler(this, null, null, 1);
    private int vid;
    private int cid = 0;
    private Typeface type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Screen Height
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;

        // Get Category ID form Previous Activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cid = extras.getInt("cid");
        }
        else {

        }

        // Create ScrollView
        ScrollView scrollView = new ScrollView(this);

        // Create LinearLayout
        final LinearLayout linearLayout = new LinearLayout(this);
        // Creating LayoutParams
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setMinimumHeight(screenHeight);
        linearLayout.setBackgroundColor(Color.parseColor("#faede5"));

        // Add LinearLayout to ScrollView
        scrollView.addView(linearLayout);

        // Set ScrollView as a root element of the screen
        setContentView(scrollView, linLayoutParam);

        // Get Category Name
        Category category = dbHandler.findCategory(cid);

        // Create inner Layout
        LinearLayout inlinearLayout = new LinearLayout(this);
        inlinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // Set Title
        TextView title = new TextView(this);
        title.setText(category.getCName());
        title.setTextSize(60);
        title.setId(R.id.title);
        title.setTextColor(Color.parseColor("#ffd4ad99"));
        type = Typeface.createFromAsset(getAssets(),"fonts/TypoGroteskDemo.otf");
        title.setTypeface(type);
        layoutParams.setMargins(30, 50, 0, 50);
        title.setLayoutParams(layoutParams);

        // Set Category Icon
        Drawable icon = getResources().getDrawable(getResources().getIdentifier(category.getIcon(), "drawable", getApplicationContext().getPackageName()));
        Bitmap bitmapResized = Bitmap.createScaledBitmap(((BitmapDrawable)icon).getBitmap(), 180, 180, false);
        ImageView ic = new ImageView(this);
        ic.setImageBitmap(bitmapResized);

        inlinearLayout.addView(ic);
        inlinearLayout.addView(title);
        inlinearLayout.setGravity(Gravity.CENTER);
        linearLayout.addView(inlinearLayout);

        // Find all products of category
        List<Product> products = dbHandler.findAllProducts(cid);


        for (Product product : products) {

            // Set Buttons
            layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);

            Button btn = new Button(this);
            btn.setText(product.getPName());
            btn.setId(product.getPID());
            btn.setTextSize(24);
            layoutParams.setMargins(50, 25, 50, 25);
            btn.setLayoutParams(layoutParams);
            btn.setBackgroundColor(Color.parseColor("#ffc19f"));
            btn.setTypeface(type);

            // Set Product Information
            layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150);

            TextView info = new TextView(getApplicationContext());
            info.setText("Expiration Date: " + product.getXPDate() + "\n" + "Notification Date: " + product.getNTDate());
            info.setTextSize(18);
            info.setId(product.getPID() * 10000);
            info.setVisibility(View.GONE);
            layoutParams.setMargins(80, 10, 80, 10);
            info.setLayoutParams(layoutParams);
            type = Typeface.createFromAsset(getAssets(),"fonts/TypoGroteskDemo.otf");
            info.setTypeface(type);

            // Add picture of product icon to button
            Bitmap pic = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
            btn.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(getResources(), pic), null, null, null);


            btn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    TextView info = (TextView) findViewById(v.getId() * 10000);

                    // Toggle product info onclick
                    if (info.getVisibility() == View.VISIBLE) {
                        info.setVisibility(View.GONE);
                    } else {
                        info.setVisibility(View.VISIBLE);
                    }

                }
            });

            btn.setOnLongClickListener(new View.OnLongClickListener() {

                public boolean onLongClick(View v) {

                    vid = v.getId();

                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(CategoryActivity.this, v);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.menu_inner, popup.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.delete)
                            {
                                Product product = dbHandler.findProduct(vid);
                                new AlertDialog.Builder(CategoryActivity.this)
                                        .setTitle("Delete Product")
                                        .setMessage("Are you sure you want to delete " + product.getPName() + "?")
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Delete Product
                                                dbHandler.deleteProduct(vid);

                                                // Cancel Alarm
                                                Intent intent = new Intent(CategoryActivity.this, NotifyMeReceiver.class);
                                                PendingIntent pendingIntent = PendingIntent.getBroadcast(CategoryActivity.this, vid, intent, 0);
                                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                                                alarmManager.cancel(pendingIntent);
                                                Toast.makeText(CategoryActivity.this, "Product Deleted", Toast.LENGTH_SHORT).show();

                                                //Refresh
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
                            else if (item.getItemId() == R.id.edit)
                            {
                                // Go To Edit Product
                                Intent k = new Intent(CategoryActivity.this, EditProductActivity.class);
                                k.putExtra("pid",vid);
                                startActivity(k);
                            }
                            return true;
                        }
                    });

                    popup.show();//showing popup menu

                    return true;
                }

            });

            linearLayout.addView(btn);
            linearLayout.addView(info);

        }

        // Set Add Product Button
        ImageButton add = new ImageButton(this);

        Drawable original_d = getResources().getDrawable(R.drawable.add_product);
        Bitmap bitmap = ((BitmapDrawable) original_d).getBitmap();
        Drawable scaled_d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 180, 160, true));

        add.setImageDrawable(scaled_d);
        layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
        layoutParams.setMargins(50, 25, 50, 25);
        add.setLayoutParams(layoutParams);
        add.setBackgroundColor(Color.parseColor("#dadada"));

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // Go To ADD PRODUCT
                Intent k = new Intent(CategoryActivity.this, AddProductActivity.class);
                k.putExtra("cid",cid);
                startActivity(k);

            }
        });

        linearLayout.addView(add);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_product) {
            // Go To ADD PRODUCT
            Intent k = new Intent(CategoryActivity.this, AddProductActivity.class);
            k.putExtra("cid",cid);
            startActivity(k);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Set Back KeyCode
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
            // Go To Categories
            Intent k = new Intent(CategoryActivity.this, CategoriesActivity.class);
            startActivity(k);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
