package com.example.bayan_oh.inspect;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        TextView help = (TextView) findViewById(R.id.help_page);
        TextView title = (TextView) findViewById(R.id.title);


        help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  Go to Help
                Intent k = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(k);
            }
        });

        Button cat = (Button) findViewById(R.id.categories_btn);
        Button scan = (Button) findViewById(R.id.scan);


        cat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  Go to Categories
                Intent k = new Intent(MainActivity.this, CategoriesActivity.class);
                startActivity(k);
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //  Go to Camera Scan
                Intent k = new Intent(MainActivity.this, CameraScanActivity.class);
                startActivity(k);
            }
        });

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/TypoGroteskDemo.otf");
        cat.setTypeface(type);
        scan.setTypeface(type);

        type = Typeface.createFromAsset(getAssets(),"fonts/TypoGroteskBoldDemo.otf");
        title.setTypeface(type);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
            // Exit System
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }





}
