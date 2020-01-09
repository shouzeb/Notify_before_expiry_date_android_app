package com.example.bayan_oh.inspect;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class HelpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Button home = (Button) findViewById(R.id.home_btn);
        TextView title = (TextView)findViewById(R.id.title);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/TypoGroteskDemo.otf");
        title.setTypeface(type);

        home.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Go to Home
                Intent k = new Intent(HelpActivity.this, MainActivity.class);
                startActivity(k);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) { //Back key pressed
            // Go To Home
            Intent k = new Intent(HelpActivity.this, MainActivity.class);
            finish();
            startActivity(k);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
